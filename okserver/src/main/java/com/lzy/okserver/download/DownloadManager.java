package com.lzy.okserver.download;

import android.os.Environment;
import android.text.TextUtils;

import com.lzy.okgo.request.BaseRequest;
import com.lzy.okserver.download.db.DownloadDBManager;
import com.lzy.okserver.listener.DownloadListener;
import com.lzy.okserver.task.ExecutorWithListener;

import java.io.File;
import java.io.Serializable;
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
    private static DownloadManager mInstance;       //使用单例模式
    private DownloadThreadPool threadPool;          //下载的线程池

    public static DownloadManager getInstance() {
        if (null == mInstance) {
            synchronized (DownloadManager.class) {
                if (null == mInstance) {
                    mInstance = new DownloadManager();
                }
            }
        }
        return mInstance;
    }

    private DownloadManager() {
        mDownloadInfoList = Collections.synchronizedList(new ArrayList<DownloadInfo>());
        mDownloadUIHandler = new DownloadUIHandler();
        threadPool = new DownloadThreadPool();
        //初始化目标Download保存目录
        String folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (!new File(folder).exists()) new File(folder).mkdirs();
        mTargetFolder = folder;

        mDownloadInfoList = DownloadDBManager.INSTANCE.getAll(); //获取所有任务
        if (mDownloadInfoList != null && !mDownloadInfoList.isEmpty()) {
            for (DownloadInfo info : mDownloadInfoList) {
                //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误
                if (info.getState() == WAITING || info.getState() == DOWNLOADING || info.getState() == PAUSE) {
                    info.setState(NONE);
                    info.setNetworkSpeed(0);
                    DownloadDBManager.INSTANCE.replace(info);
                }
            }
        }
    }

    /** 添加一个下载任务,依据taskTag标识是否属于同一个任务 */
    public void addTask(String taskTag, BaseRequest request, DownloadListener listener) {
        addTask(null, taskTag, null, request, listener, false);
    }

    /** 添加一个下载任务,依据taskTag标识是否属于同一个任务 */
    public void addTask(String taskTag, Serializable data, BaseRequest request, DownloadListener listener) {
        addTask(null, taskTag, data, request, listener, false);
    }

    /** 添加一个下载任务,依据taskTag标识是否属于同一个任务 */
    public void addTask(String fileName, String taskTag, BaseRequest request, DownloadListener listener) {
        addTask(fileName, taskTag, null, request, listener, false);
    }

    /**
     * 添加一个下载任务
     *
     * @param request   下载的网络请求
     * @param listener  下载监听
     * @param isRestart 是否重新开始下载
     */
    private void addTask(String fileName, String taskTag, Serializable data, BaseRequest request, DownloadListener listener, boolean isRestart) {
        DownloadInfo downloadInfo = getDownloadInfo(taskTag);
        if (downloadInfo == null) {
            downloadInfo = new DownloadInfo();
            downloadInfo.setUrl(request.getBaseUrl());
            downloadInfo.setTaskKey(taskTag);
            downloadInfo.setFileName(fileName);
            downloadInfo.setRequest(request);
            downloadInfo.setState(DownloadManager.NONE);
            downloadInfo.setTargetFolder(mTargetFolder);
            downloadInfo.setData(data);
            DownloadDBManager.INSTANCE.replace(downloadInfo);
            mDownloadInfoList.add(downloadInfo);
        }
        //无状态，暂停，错误才允许开始下载
        if (downloadInfo.getState() == DownloadManager.NONE || downloadInfo.getState() == DownloadManager.PAUSE || downloadInfo.getState() == DownloadManager.ERROR) {
            //构造即开始执行
            DownloadTask downloadTask = new DownloadTask(downloadInfo, isRestart, listener);
            downloadInfo.setTask(downloadTask);
        } else if (downloadInfo.getState() == DownloadManager.FINISH) {
            if (listener != null) listener.onFinish(downloadInfo);
        }
    }

    /** 开始所有任务 */
    public void startAllTask() {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            addTask(downloadInfo.getTaskKey(), downloadInfo.getRequest(), downloadInfo.getListener());
        }
    }

    /** 暂停 */
    public void pauseTask(String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
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
            if (info.getState() != DOWNLOADING) pauseTask(info.getTaskKey());
        }
        for (DownloadInfo info : mDownloadInfoList) {
            if (info.getState() == DOWNLOADING) pauseTask(info.getTaskKey());
        }
    }

    /** 停止 */
    public void stopTask(final String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
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

    /** 删除一个任务,会删除下载文件 */
    public void removeTask(String taskKey) {
        removeTask(taskKey, false);
    }

    /** 删除一个任务,会删除下载文件 */
    public void removeTask(String taskKey, boolean isDeleteFile) {
        final DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        pauseTask(taskKey);                         //暂停任务
        removeTaskByKey(taskKey);                   //移除任务
        if (isDeleteFile) deleteFile(downloadInfo.getTargetPath());   //删除文件
        DownloadDBManager.INSTANCE.delete(taskKey);            //清除数据库
    }

    /** 删除所有任务 */
    public void removeAllTask() {
        //集合深度拷贝，避免迭代移除报错
        List<String> taskKeys = new ArrayList<>();
        for (DownloadInfo info : mDownloadInfoList) {
            taskKeys.add(info.getTaskKey());
        }
        for (String url : taskKeys) {
            removeTask(url);
        }
    }

    /** 重新下载 */
    public void restartTask(final String taskKey) {
        final DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo != null && downloadInfo.getState() == DOWNLOADING) {
            //如果正在下载中，先暂停，等任务结束后再添加到队列开始下载
            pauseTask(taskKey);
            threadPool.getExecutor().addOnTaskEndListener(new ExecutorWithListener.OnTaskEndListener() {
                @Override
                public void onTaskEnd(Runnable r) {
                    if (r == downloadInfo.getTask().getRunnable()) {
                        //因为该监听是全局监听，每次任务被移除都会回调，所以以下方法执行一次后，必须移除，否者会反复调用
                        threadPool.getExecutor().removeOnTaskEndListener(this);
                        //此时监听给空，表示会使用之前的监听，true表示重新下载，会删除临时文件
                        addTask(downloadInfo.getFileName(), downloadInfo.getTaskKey(), downloadInfo.getData(), downloadInfo.getRequest(), downloadInfo.getListener(), true);
                    }
                }
            });
        } else {
            pauseTask(taskKey);
            restartTaskByKey(taskKey);
        }
    }

    /** 重新开始下载任务 */
    private void restartTaskByKey(String taskKey) {
        DownloadInfo downloadInfo = getDownloadInfo(taskKey);
        if (downloadInfo == null) return;
        if (downloadInfo.getState() != DOWNLOADING) {
            DownloadTask downloadTask = new DownloadTask(downloadInfo, true, downloadInfo.getListener());
            downloadInfo.setTask(downloadTask);
        }
    }

    /** 获取一个任务 */
    public DownloadInfo getDownloadInfo(String taskKey) {
        for (DownloadInfo downloadInfo : mDownloadInfoList) {
            if (taskKey.equals(downloadInfo.getTaskKey())) {
                return downloadInfo;
            }
        }
        return null;
    }

    /** 移除一个任务 */
    private void removeTaskByKey(String taskKey) {
        ListIterator<DownloadInfo> iterator = mDownloadInfoList.listIterator();
        while (iterator.hasNext()) {
            DownloadInfo info = iterator.next();
            if (taskKey.equals(info.getTaskKey())) {
                DownloadListener listener = info.getListener();
                if (listener != null) listener.onRemove(info);
                info.removeListener();     //清除回调监听
                iterator.remove();         //清除任务
                break;
            }
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
}