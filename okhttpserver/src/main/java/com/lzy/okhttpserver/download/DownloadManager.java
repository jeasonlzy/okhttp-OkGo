package com.lzy.okhttpserver.download;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okhttpserver.listener.DownloadListener;
import com.lzy.okhttpserver.task.ExecutorWithListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：全局的下载管理类
 * 修订历史：
 * ================================================
 */
public class DownloadManager {

    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载管理目标文件夹

    //定义下载状态常量
    public static final int NONE = 0;         //无状态  --> 等待
    public static final int WAITING = 1;      //等待    --> 下载，暂停
    public static final int DOWNLOADING = 2;  //下载中  --> 暂停，完成，错误
    public static final int PAUSE = 3;        //暂停    --> 等待，下载
    public static final int FINISH = 4;       //完成    --> 重新下载
    public static final int ERROR = 5;        //错误    --> 等待

    private List<DownloadInfo> mDownloadInfoList;   //维护了所有下载任务的集合
    private DownloadUIHandler mDownloadUIHandler;   //主线程执行的handler
    private String mTargetFolder;                   //下载目录
    private DownloadInfoDao mDownloadDao;           //数据库操作类
    private Context mContext;                       //上下文
    private static DownloadManager mInstance;       //使用单例模式
    private DownloadThreadPool threadPool;          //下载的线程池

    public static DownloadManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (DownloadManager.class) {
                if (null == mInstance) {
                    mInstance = new DownloadManager(context);
                }
            }
        }
        return mInstance;
    }

    private DownloadManager(Context context) {
        mContext = context;
        mDownloadInfoList = Collections.synchronizedList(new ArrayList<DownloadInfo>());
        mDownloadUIHandler = new DownloadUIHandler();
        threadPool = new DownloadThreadPool();
        //初始化目标Download保存目录
        String folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (!new File(folder).exists()) {
            new File(folder).mkdirs();
        }
        mTargetFolder = folder;
        mDownloadDao = new DownloadInfoDao(context);    //构建下载Download的操作类
        mDownloadInfoList = mDownloadDao.queryForAll(); //获取所有任务
        for (DownloadInfo info : mDownloadInfoList) {
            //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误
            if (info.getState() == WAITING || info.getState() == DOWNLOADING || info.getState() == PAUSE) {
                info.setState(NONE);
                info.setNetworkSpeed(0);
                mDownloadDao.update(info);
            }
        }
    }

    /** 设置下载目标目录 */
    public String getTargetFolder() {
        return mTargetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.mTargetFolder = targetFolder;
    }

    public DownloadThreadPool getThreadPool() {
        return threadPool;
    }

    public DownloadUIHandler getHandler() {
        return mDownloadUIHandler;
    }

    public List<DownloadInfo> getAllTask() {
        return mDownloadInfoList;
    }

    public DownloadInfo getTaskByUrl(@NonNull String url) {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            if (url.equals(downloadInfo.getUrl())) {
                return downloadInfo;
            }
        }
        return null;
    }

    public void removeTaskByUrl(@NonNull String url) {
        ListIterator<DownloadInfo> iterator = mDownloadInfoList.listIterator();
        while (iterator.hasNext()) {
            DownloadInfo info = iterator.next();
            if (url.equals(info.getUrl())) {
                DownloadListener listener = info.getListener();
                if (listener != null) listener.onRemove(info);
                info.removeListener();     //清除回调监听
                iterator.remove();         //清除任务
                break;
            }
        }
    }

    /** 添加一个下载任务 */
    public void addTask(@NonNull String url, DownloadListener listener) {
        addTask(url, listener, false);
    }

    /**
     * 添加一个下载任务
     *
     * @param url       下载地址
     * @param listener  下载监听
     * @param isRestart 是否重新开始下载
     */
    private void addTask(@NonNull String url, DownloadListener listener, boolean isRestart) {
        DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) {
            downloadInfo = new DownloadInfo();
            downloadInfo.setUrl(url);
            downloadInfo.setState(DownloadManager.NONE);
            downloadInfo.setTargetFolder(mTargetFolder);
            mDownloadDao.create(downloadInfo);
            mDownloadInfoList.add(downloadInfo);
        }
        //无状态，暂停，错误才允许开始下载
        if (downloadInfo.getState() == DownloadManager.NONE //
                || downloadInfo.getState() == DownloadManager.PAUSE//
                || downloadInfo.getState() == DownloadManager.ERROR) {
            //构造即开始执行
            DownloadTask downloadTask = new DownloadTask(downloadInfo, mContext, isRestart, listener);
            downloadInfo.setTask(downloadTask);
        } else {
            Log.d("DownloadManager", "任务正在下载或等待中 url:" + url);
        }
    }

    /** 开始所有任务的方法 */
    public void startAllTask() {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            addTask(downloadInfo.getUrl(), downloadInfo.getListener());
        }
    }

    /** 暂停的方法 */
    public void pauseTask(String url) {
        DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) return;
        int state = downloadInfo.getState();
        //等待和下载中才允许暂停
        if ((state == DOWNLOADING || state == WAITING) && downloadInfo.getTask() != null) {
            downloadInfo.getTask().pause();
        }
    }

    /** 暂停全部任务,先暂停没有下载的，再暂停下载中的 */
    public void pauseAllTask() {
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() != DOWNLOADING) pauseTask(info.getUrl());
        }
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() == DOWNLOADING) pauseTask(info.getUrl());
        }
    }

    /** 停止的方法 */
    public void stopTask(final String url) {
        DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) return;
        //无状态和完成状态，不允许停止
        if ((downloadInfo.getState() != NONE && downloadInfo.getState() != FINISH) && downloadInfo.getTask() != null) {
            downloadInfo.getTask().stop();
        }
    }

    /** 停止全部任务,先停止没有下载的，再停止下载中的 */
    public void stopAllTask() {
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() != DOWNLOADING) stopTask(info.getUrl());
        }
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() == DOWNLOADING) stopTask(info.getUrl());
        }
    }

    /** 删除一个任务 */
    public void removeTask(final String url) {
        final DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) return;
        pauseTask(url);        //暂停任务
        removeTaskByUrl(url);  //移除任务
        deleteFile(downloadInfo.getTargetPath()); //删除文件
        mDownloadDao.delete(url); //清除数据库
    }

    /** 删除所有任务 */
    public void removeAllTask() {
        //集合深度拷贝，避免迭代移除报错
        List<String> urls = new ArrayList<>();
        for (DownloadInfo info : mDownloadInfoList) {
            urls.add(info.getUrl());
        }
        for (String url : urls) {
            removeTask(url);
        }
    }

    /** 重新下载 */
    public void restartTask(final String url) {
        final DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo.getState() == DOWNLOADING) {
            //如果正在下载中，先暂停，等任务结束后再添加到队列开始下载
            pauseTask(url);
            threadPool.getExecutor().addOnTaskEndListener(new ExecutorWithListener.OnTaskEndListener() {
                @Override
                public void onTaskEnd(Runnable r) {
                    if (r == downloadInfo.getTask().getRunnable()) {
                        //因为该监听是全局监听，每次任务被移除都会回调，所以以下方法执行一次后，必须移除，否者会反复调用
                        threadPool.getExecutor().removeOnTaskEndListener(this);
                        addTask(url, downloadInfo.getListener(), true); //此时监听给空，表示会使用之前的监听，true表示重新下载，会删除临时文件
                    }
                }
            });
        } else {
            pauseTask(url);
            startTask(url);
        }
    }

    /** 重新开始下载任务 */
    private void startTask(@NonNull String url) {
        DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) return;
        if (downloadInfo.getState() != DOWNLOADING) {
            DownloadTask downloadTask = new DownloadTask(downloadInfo, mContext, true, downloadInfo.getListener());
            downloadInfo.setTask(downloadTask);
        }
    }

    /** 根据路径删除文件 */
    private boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) return file.delete();
        return false;
    }
}