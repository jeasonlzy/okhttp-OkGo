package com.lzy.okhttpserver.upload;

import android.content.Context;
import android.support.annotation.NonNull;

import com.lzy.okhttpserver.listener.UploadListener;
import com.lzy.okhttpserver.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：全局的上传管理
 * 修订历史：
 * ================================================
 */
public class UploadManager {
    //定义上传状态常量
    public static final int NONE = 0;         //无状态  --> 等待
    public static final int WAITING = 1;      //等待    --> 下载
    public static final int UPLOADING = 2;    //下载中  --> 完成，错误
    public static final int FINISH = 3;       //完成    --> 重新上传
    public static final int ERROR = 4;        //错误    --> 等待

    private List<UploadInfo> mUploadInfoList;       //维护了所有下载任务的集合
    private UploadUIHandler mUploadUIHandler;       //主线程执行的handler
    private Context mContext;                       //上下文
    private static UploadManager mInstance;         //使用单例模式
    private UploadThreadPool threadPool;            //上传的线程池

    public static UploadManager getInstance(Context context) {
        if (null == mInstance) {
            synchronized (UploadManager.class) {
                if (null == mInstance) {
                    mInstance = new UploadManager(context);
                }
            }
        }
        return mInstance;
    }

    private UploadManager(Context context) {
        mContext = context;
        mUploadInfoList = Collections.synchronizedList(new ArrayList<UploadInfo>());
        mUploadUIHandler = new UploadUIHandler();
        threadPool = new UploadThreadPool();
    }

    public UploadThreadPool getThreadPool() {
        return threadPool;
    }

    public UploadUIHandler getHandler() {
        return mUploadUIHandler;
    }

    public List<UploadInfo> getAllTask() {
        return mUploadInfoList;
    }

    public UploadInfo getTaskByResourcePath(@NonNull String resourcePath) {
        for (UploadInfo uploadInfo : mUploadInfoList) {
            if (resourcePath.equals(uploadInfo.getResourcePath())) {
                return uploadInfo;
            }
        }
        return null;
    }

    /**
     * 添加一个上传任务
     *
     * @param url      上传地址
     * @param listener 上传监听
     */
    public <T> void addTask(@NonNull String url, @NonNull File resource, @NonNull String key, UploadListener<T> listener) {
        UploadInfo uploadInfo = getTaskByResourcePath(url);
        if (uploadInfo == null) {
            uploadInfo = new UploadInfo();
            uploadInfo.setUrl(url);
            uploadInfo.setState(UploadManager.NONE);
            uploadInfo.setResourcePath(resource.getAbsolutePath());
            uploadInfo.setKey(key);
            mUploadInfoList.add(uploadInfo);
        }
        //无状态，暂停，错误才允许开始上传
        if (uploadInfo.getState() == UploadManager.NONE || uploadInfo.getState() == UploadManager.ERROR) {
            //构造即开始执行
            UploadTask uploadTask = new UploadTask<T>(uploadInfo, mContext, listener);
            uploadInfo.setTask(uploadTask);
        } else {
            L.d("任务正在上传或等待中 url:" + url);
        }
    }
}
