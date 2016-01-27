package com.lzy.okhttpserver.upload;

import com.lzy.okhttpserver.download.DownloadInfo;
import com.lzy.okhttpserver.listener.UploadListener;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：文件上传的Bean
 * 修订历史：
 * ================================================
 */
public class UploadInfo {

    private String url;           //文件URL
    private String resourcePath;  //上传源文件地址
    private String key;           //指定上传文件的key
    private String fileName;      //指定上传文件的名字
    private float progress;       //下载进度
    private long totalLength;     //总大小
    private long uploadLength;    //已上传大小
    private long networkSpeed;    //下载速度
    private int state;            //当前状态
    private UploadTask task;                  //执行当前上传的任务
    private UploadListener listener;          //当前上传任务的监听

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public long getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(long totalLength) {
        this.totalLength = totalLength;
    }

    public long getUploadLength() {
        return uploadLength;
    }

    public void setUploadLength(long uploadLength) {
        this.uploadLength = uploadLength;
    }

    public long getNetworkSpeed() {
        return networkSpeed;
    }

    public void setNetworkSpeed(long networkSpeed) {
        this.networkSpeed = networkSpeed;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public UploadTask getTask() {
        return task;
    }

    public void setTask(UploadTask task) {
        this.task = task;
    }

    public UploadListener getListener() {
        return listener;
    }

    public void setListener(UploadListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        this.listener = null;
    }

    /** url 相同就认为是同一个任务 */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DownloadInfo) {
            DownloadInfo info = (DownloadInfo) o;
            return getUrl().equals(info.getUrl());
        }
        return false;
    }
}
