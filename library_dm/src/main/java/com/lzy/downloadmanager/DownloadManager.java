package com.lzy.downloadmanager;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.lzy.okhttputils.L;

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
 * 描    述：下载管理类
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
    private DownloadThreadPool threadPool;

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
    }

    /** 设置下载目标目录 */
    public String getTargetFolder() {
        return mTargetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.mTargetFolder = targetFolder;
    }

    /** 必须在首次执行前设置，否者无效 ,范围1-5之间 */
    public void setCorePoolSize(int corePoolSize) {
        threadPool.setCorePoolSize(corePoolSize);
    }

    public DownloadUIHandler getHandler() {
        return mDownloadUIHandler;
    }

    public List<DownloadInfo> getAllTask() {
        return mDownloadInfoList;
    }

    public DownloadInfo getTaskByUrl(@NonNull String url) {
        for (DownloadInfo info : mDownloadInfoList) {
            if (url.equals(info.getUrl())) return info;
        }
        return null;
    }

    public void removeTaskByUrl(@NonNull String url) {
        ListIterator<DownloadInfo> iterator = mDownloadInfoList.listIterator();
        while (iterator.hasNext()) {
            DownloadInfo info = iterator.next();
            if (url.equals(info.getUrl())) {
                //通知监听移除任务
                List<DownloadListener> listeners = info.getListeners();
                for (DownloadListener l : listeners) {
                    l.onRemove(info);
                }
                info.removeAllListener();  //清除回调监听
                iterator.remove();         //清除任务
                break;
            }
        }
    }

    /** 添加一个下载任务 */
    public void addTask(@NonNull String url) {
        addTask(url, null);
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
        if (downloadInfo.getState() == DownloadManager.NONE //
                || downloadInfo.getState() == DownloadManager.PAUSE//
                || downloadInfo.getState() == DownloadManager.ERROR) {
            downloadInfo.setState(DownloadManager.WAITING);  //先让状态变为等待
            L.e("addTask  " + downloadInfo.getState());
            DownloadTask downloadTask = new DownloadTask(downloadInfo, mContext, isRestart, listener);
            downloadInfo.setTask(downloadTask);
            threadPool.execute(downloadTask);//将下载任务交给线程池管理
            //通知监听添加任务
            List<DownloadListener> listeners = downloadInfo.getListeners();
            for (DownloadListener l : listeners) {
                l.onAdd(downloadInfo);
            }
        } else {
            L.d("任务正在下载或等待中 url:" + url);
        }
    }

    /** 暂停的方法 */
    public void pause(String url) {
        L.e("pause");
        //将状态更改为暂停,已经下载完成的不允许暂停，可以重新下载
        final DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo != null && downloadInfo.getState() != DownloadManager.FINISH) {
            downloadInfo.setState(PAUSE);
            //将暂停的task移除线程池，为其他任务留出资源.
            //其实不用移除，当任务结束后，自动会被清除掉
            threadPool.getExecutor().setOnTaskEndListener(new EndThreadPoolExecutor.OnTaskEndListener() {
                @Override
                public void onTaskEnd(Runnable r) {
                    if (r == downloadInfo.getTask()) {
                        threadPool.remove(downloadInfo.getTask());
                    }
                }
            });
            L.e("pause " + downloadInfo.getState());
        }
    }

    /** 暂停全部任务 */
    public void pauseAll() {
        for (DownloadInfo info : mDownloadInfoList) {
            pause(info.getUrl());
        }
    }

    /** 重新下载 */
    public void restartTask(final String url) {
        L.e("restartTask");
        final DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) {
            L.d("还有没有下载过，请先下载 url:" + url);
            return;
        }
        pause(url);  //先暂停，等任务结束后再开始下载
        threadPool.getExecutor().setOnTaskEndListener(new EndThreadPoolExecutor.OnTaskEndListener() {
            @Override
            public void onTaskEnd(Runnable r) {
                if (r == downloadInfo.getTask()) {
                    addTask(url, null, true); //此时监听给空，表示会使用之前的监听，true表示重新下载，会删除临时文件
                }
            }
        });
    }

    /** 删除一个任务 */
    public void removeTask(String url) {
        DownloadInfo downloadInfo = getTaskByUrl(url);
        if (downloadInfo == null) {
            L.d("还有没有下载过，请先下载 url:" + url);
            return;
        }
        pause(url);            //暂停任务
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

    /** 根据路径删除文件 */
    private boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) return file.delete();
        return false;
    }
}