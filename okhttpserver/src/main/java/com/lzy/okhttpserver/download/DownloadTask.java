package com.lzy.okhttpserver.download;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.lzy.okhttpserver.listener.DownloadListener;
import com.lzy.okhttpserver.task.PriorityAsyncTask;
import com.lzy.okhttpserver.L;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：文件的下载任务类
 * 修订历史：
 * ================================================
 */
public class DownloadTask extends PriorityAsyncTask<Void, DownloadInfo, DownloadInfo> {

    private static final int BUFFER_SIZE = 1024 * 8; //读写缓存大小

    private DownloadInfoDao downloadDao;             //数据库操作类
    private DownloadUIHandler mDownloadUIHandler;    //下载UI回调
    private DownloadInfo mDownloadInfo;              //当前任务的信息
    private long mPreviousTime;                      //上次更新的时间，用于计算下载速度
    private boolean isRestartTask;                   //是否重新下载的标识位
    private boolean isPause;                         //当前任务是暂停还是停止， true 暂停， false 停止

    public DownloadTask(DownloadInfo downloadInfo, Context context, boolean isRestart, DownloadListener downloadListener) {
        mDownloadInfo = downloadInfo;
        isRestartTask = isRestart;
        mDownloadInfo.setListener(downloadListener);
        mDownloadUIHandler = DownloadManager.getInstance(context).getHandler();
        downloadDao = new DownloadInfoDao(context);
        //将当前任务在定义的线程池中执行
        executeOnExecutor(DownloadManager.getInstance(context).getThreadPool().getExecutor());
    }

    /** 暂停的方法 */
    public void pause() {
        if (mDownloadInfo.getState() == DownloadManager.WAITING) {
            //如果是等待状态的,因为该状态取消不会回调任何方法，需要手动触发
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.PAUSE);
            postMessage(null, null);
        } else {
            isPause = true;
        }
        super.cancel(false);
    }

    /** 停止的方法 */
    public void stop() {
        if (mDownloadInfo.getState() == DownloadManager.PAUSE || //
                mDownloadInfo.getState() == DownloadManager.ERROR ||//
                mDownloadInfo.getState() == DownloadManager.WAITING) {
            //如果状态是暂停或错误的,停止不会回调任何方法，需要手动触发
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.NONE);
            postMessage(null, null);
        } else {
            isPause = false;
        }
        super.cancel(false);
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        L.e("onPreExecute:" + mDownloadInfo.getFileName());

        //添加成功的回调
        DownloadListener listener = mDownloadInfo.getListener();
        if (listener != null) listener.onAdd(mDownloadInfo);

        //如果是重新下载，需要删除临时文件
        if (isRestartTask) {
            deleteFile(mDownloadInfo.getTargetPath());
            mDownloadInfo.setProgress(0);
            mDownloadInfo.setDownloadLength(0);
            mDownloadInfo.setTotalLength(0);
            isRestartTask = false;
        }

        mDownloadInfo.setNetworkSpeed(0);
        mDownloadInfo.setState(DownloadManager.WAITING);
        postMessage(null, null);
    }

    /** 如果调用了Cancel，就不会执行该方法，所以任务结束的回调不放在这里面 */
    @Override
    protected void onPostExecute(DownloadInfo downloadInfo) {
    }

    /** 一旦该方法执行，意味着开始下载了 */
    @Override
    protected DownloadInfo doInBackground(Void... params) {
        if (isCancelled()) return mDownloadInfo;
        L.e("doInBackground:" + mDownloadInfo.getFileName());
        mPreviousTime = System.currentTimeMillis();
        mDownloadInfo.setNetworkSpeed(0);
        mDownloadInfo.setState(DownloadManager.DOWNLOADING);
        postMessage(null, null);

        //构建下载文件路径，如果有设置，就用设置的，否者就自己创建
        String url = mDownloadInfo.getUrl();
        String fileName = mDownloadInfo.getFileName();
        if (TextUtils.isEmpty(fileName)) {
            fileName = getUrlFileName(url);
            mDownloadInfo.setFileName(fileName);
        }
        if (TextUtils.isEmpty(mDownloadInfo.getTargetPath())) {
            File file = new File(mDownloadInfo.getTargetFolder(), fileName);
            mDownloadInfo.setTargetPath(file.getAbsolutePath());
        }

        //检查手机上文件的有效性
        File file = new File(mDownloadInfo.getTargetPath());
        long startPos;
        if (file.length() != mDownloadInfo.getDownloadLength()) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("断点文件异常，需要删除后重新下载", null);
            return mDownloadInfo;
        } else {
            //断点下载的情况
            startPos = mDownloadInfo.getDownloadLength();
        }
        //再次检查文件有效性，文件大小大于总文件大小
        if (startPos > mDownloadInfo.getTotalLength()) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("断点文件异常，需要删除后重新下载", null);
            return mDownloadInfo;
        }
        if (startPos == mDownloadInfo.getTotalLength() && startPos > 0) {
            mDownloadInfo.setProgress(1.0f);
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.FINISH);
            postMessage(null, null);
            return mDownloadInfo;
        }
        //设置断点写文件
        ProgressRandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new ProgressRandomAccessFile(file, "rw", startPos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("没有找到已存在的断点文件", e);
            return mDownloadInfo;
        }
        L.e("startPos:" + startPos + "  path:" + mDownloadInfo.getTargetPath());

        //构建请求体,默认使用get请求下载,设置断点头
        Response response;
        try {
            response = OkHttpUtils.get(url).headers("RANGE", "bytes=" + startPos + "-").execute();
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("网络异常", e);
            return mDownloadInfo;
        }
        //获取流对象，准备进行读写文件
        long totalLength = response.body().contentLength();
        if (mDownloadInfo.getTotalLength() == 0) {
            mDownloadInfo.setTotalLength(totalLength);
        }
        InputStream is = response.body().byteStream();
        //读写文件流
        try {
            download(is, randomAccessFile);
        } catch (IOException e) {
            e.printStackTrace();
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR);
            postMessage("文件读写异常", e);
            return mDownloadInfo;
        }

        //循环结束走到这里，a.下载完成     b.暂停      c.判断是否下载出错
        if (isCancelled()) {
            L.e("state: 暂停" + mDownloadInfo.getState());
            mDownloadInfo.setNetworkSpeed(0);
            if (isPause) mDownloadInfo.setState(DownloadManager.PAUSE); //暂停
            else mDownloadInfo.setState(DownloadManager.NONE);          //停止
            postMessage(null, null);
        } else if (file.length() == mDownloadInfo.getTotalLength() && mDownloadInfo.getState() == DownloadManager.DOWNLOADING) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.FINISH); //下载完成
            postMessage(null, null);
        } else if (file.length() != mDownloadInfo.getDownloadLength()) {
            mDownloadInfo.setNetworkSpeed(0);
            mDownloadInfo.setState(DownloadManager.ERROR); //由于不明原因，文件保存有误
            postMessage("未知原因", null);
        }
        return mDownloadInfo;
    }

    private void postMessage(String errorMsg, Exception e) {
        downloadDao.update(mDownloadInfo); //发消息前首先更新数据库
        DownloadUIHandler.MessageBean messageBean = new DownloadUIHandler.MessageBean();
        messageBean.downloadInfo = mDownloadInfo;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        Message msg = mDownloadUIHandler.obtainMessage();
        msg.obj = messageBean;
        mDownloadUIHandler.sendMessage(msg);
    }

    /** 通过 ‘？’ 和 ‘/’ 判断文件名 */
    private String getUrlFileName(String url) {
        int index = url.lastIndexOf('?');
        String filename;
        if (index > 1) {
            filename = url.substring(url.lastIndexOf('/') + 1, index);
        } else {
            filename = url.substring(url.lastIndexOf('/') + 1);
        }
        return filename;
    }

    /** 根据路径删除文件 */
    private boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) {
            boolean delete = file.delete();
            L.e("deleteFile:" + delete + " path:" + path);
            return delete;
        }
        return false;
    }

    /** 执行文件下载 */
    private int download(InputStream input, RandomAccessFile out) throws IOException {
        if (input == null || out == null) return -1;

        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int downloadSize = 0;
        int len;
        try {
            out.seek(out.length());
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && !isCancelled()) {
                out.write(buffer, 0, len);
                downloadSize += len;
            }
        } finally {
            try {
                out.close();
                in.close();
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return downloadSize;
    }

    /** 文件读写 */
    private final class ProgressRandomAccessFile extends RandomAccessFile {
        private long lastDownloadLength = 0; //总共已下载的大小
        private long curDownloadLength = 0;  //当前已下载的大小（可能分几次下载）
        private long lastRefreshUiTime;

        public ProgressRandomAccessFile(File file, String mode, long lastDownloadLength) throws FileNotFoundException {
            super(file, mode);
            this.lastDownloadLength = lastDownloadLength;
            this.lastRefreshUiTime = System.currentTimeMillis();
        }

        @Override
        public void write(byte[] buffer, int offset, int count) throws IOException {
            super.write(buffer, offset, count);

            //已下载大小
            long downloadLength = lastDownloadLength + count;
            curDownloadLength += count;
            lastDownloadLength = downloadLength;
            mDownloadInfo.setDownloadLength(downloadLength);

            //计算下载速度
            long totalTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
            if (totalTime == 0) {
                totalTime += 1;
            }
            long networkSpeed = curDownloadLength / totalTime;
            mDownloadInfo.setNetworkSpeed(networkSpeed);

            //下载进度
            float progress = downloadLength * 1.0f / mDownloadInfo.getTotalLength();
            mDownloadInfo.setProgress(progress);
            long curTime = System.currentTimeMillis();
            //每200毫秒刷新一次数据
            if (curTime - lastRefreshUiTime >= 100 || progress == 1.0f) {
                postMessage(null, null);
//                L.e(mDownloadInfo.getDownloadLength() + " " + mDownloadInfo.getTotalLength() + " " + mDownloadInfo.getProgress());
                lastRefreshUiTime = System.currentTimeMillis();
            }
        }
    }
}