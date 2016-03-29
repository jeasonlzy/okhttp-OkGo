package com.lzy.okhttpserver.download;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.lzy.okhttpserver.listener.DownloadListener;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：文件的下载任务Bean
 * 修订历史：
 * ================================================
 */
@DatabaseTable(tableName = "DownloadInfo")
public class DownloadInfo implements Comparable<DownloadInfo> {

    @DatabaseField(generatedId = true, columnName = "id")
    private int id;               //id自增长
    @DatabaseField(columnName = "url")
    private String url;           //文件URL
    @DatabaseField(columnName = "targetFolder")
    private String targetFolder;  //保存文件夹
    @DatabaseField(columnName = "targetPath")
    private String targetPath;    //保存文件地址
    @DatabaseField(columnName = "fileName")
    private String fileName;      //保存的文件名
    @DatabaseField(columnName = "progress")
    private float progress;       //下载进度
    @DatabaseField(columnName = "totalLength")
    private long totalLength;     //总大小
    @DatabaseField(columnName = "downloadLength")
    private long downloadLength;  //已下载大小
    @DatabaseField(columnName = "networkSpeed")
    private long networkSpeed;    //下载速度
    @DatabaseField(columnName = "state")
    private int state = 0;        //当前状态

    private DownloadTask task;                  //执行当前下载的任务
    private DownloadListener listener;          //当前下载任务的监听

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getTargetFolder() {
        return targetFolder;
    }

    public void setTargetFolder(String targetFolder) {
        this.targetFolder = targetFolder;
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

    public long getDownloadLength() {
        return downloadLength;
    }

    public void setDownloadLength(long downloadLength) {
        this.downloadLength = downloadLength;
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

    public DownloadTask getTask() {
        return task;
    }

    public void setTask(DownloadTask task) {
        this.task = task;
    }

    public DownloadListener getListener() {
        return listener;
    }

    public void setListener(DownloadListener listener) {
        this.listener = listener;
    }

    public void removeListener() {
        listener = null;
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

    /** 两个任务排序按照 id 的大小排序 */
    @Override
    public int compareTo(DownloadInfo another) {
        return ((Integer) getId()).compareTo(another.getId());
    }
}
