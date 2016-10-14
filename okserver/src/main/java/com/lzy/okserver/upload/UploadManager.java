package com.lzy.okserver.upload;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.BaseBodyRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.listener.UploadListener;

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
    private static UploadManager mInstance;         //使用单例模式
    private UploadThreadPool threadPool;            //上传的线程池

    public static UploadManager getInstance() {
        if (null == mInstance) {
            synchronized (UploadManager.class) {
                if (null == mInstance) {
                    mInstance = new UploadManager();
                }
            }
        }
        return mInstance;
    }

    private UploadManager() {
        mUploadInfoList = Collections.synchronizedList(new ArrayList<UploadInfo>());
        mUploadUIHandler = new UploadUIHandler();
        threadPool = new UploadThreadPool();
    }

    /** 添加一个上传任务,默认使用post请求 */
    @Deprecated
    public <T> void addTask(String url, File resource, String key, UploadListener<T> listener) {
        PostRequest request = OkGo.post(url).params(key, resource);
        addTask(url, request, listener);
    }

    /** 添加一个上传任务 */
    public <T> void addTask(String taskKey, BaseBodyRequest request, UploadListener<T> listener) {
        UploadInfo uploadInfo = new UploadInfo();
        uploadInfo.setTaskKey(taskKey);
        uploadInfo.setState(UploadManager.NONE);
        uploadInfo.setRequest(request);
        mUploadInfoList.add(uploadInfo);
        //构造即开始执行
        UploadTask uploadTask = new UploadTask<T>(uploadInfo, listener);
        uploadInfo.setTask(uploadTask);
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
}