package com.lzy.okserver.download;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okgo.request.BaseRequest;
import com.lzy.okgo.utils.OkLogger;
import com.lzy.okserver.download.db.DownloadRequest;
import com.lzy.okserver.listener.DownloadListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：文件的下载任务Bean
 * 修订历史：
 * ================================================
 */
public class DownloadInfo implements Comparable<DownloadInfo> {

    //表中的字段
    public static final String ID = "_id";
    public static final String TASK_KEY = "taskKey";
    public static final String URL = "url";
    public static final String TARGET_FOLDER = "targetFolder";
    public static final String TARGET_PATH = "targetPath";
    public static final String FILE_NAME = "fileName";
    public static final String PROGRESS = "progress";
    public static final String TOTAL_LENGTH = "totalLength";
    public static final String DOWNLOAD_LENGTH = "downloadLength";
    public static final String NETWORK_SPEED = "networkSpeed";
    public static final String STATE = "state";
    public static final String DOWNLOAD_REQUEST = "downloadRequest";
    public static final String DATA = "data";

    private int id;                     //id自增长
    private String taskKey;             //下载的标识键
    private String url;                 //文件URL
    private String targetFolder;        //保存文件夹
    private String targetPath;          //保存文件地址
    private String fileName;            //保存的文件名
    private float progress;             //下载进度
    private long totalLength;           //总大小
    private long downloadLength;        //已下载大小
    private long networkSpeed;          //下载速度
    private int state = 0;              //当前状态
    private BaseRequest request;        //当前任务的网络请求
    private Serializable data;          //额外的数据

    private DownloadRequest downloadRequest = new DownloadRequest();
    private DownloadTask task;          //执行当前下载的任务
    private DownloadListener listener;  //当前下载任务的监听

    public static ContentValues buildContentValues(DownloadInfo downloadInfo) {
        ContentValues values = new ContentValues();
        values.put(TASK_KEY, downloadInfo.getTaskKey());
        values.put(URL, downloadInfo.getUrl());
        values.put(TARGET_FOLDER, downloadInfo.getTargetFolder());
        values.put(TARGET_PATH, downloadInfo.getTargetPath());
        values.put(FILE_NAME, downloadInfo.getFileName());
        values.put(PROGRESS, downloadInfo.getProgress());
        values.put(TOTAL_LENGTH, downloadInfo.getTotalLength());
        values.put(DOWNLOAD_LENGTH, downloadInfo.getDownloadLength());
        values.put(NETWORK_SPEED, downloadInfo.getNetworkSpeed());
        values.put(STATE, downloadInfo.getState());

        BaseRequest request = downloadInfo.getRequest();
        DownloadRequest downloadRequest = downloadInfo.getDownloadRequest();
        downloadRequest.cacheKey = request.getCacheKey();
        downloadRequest.cacheTime = request.getCacheTime();
        downloadRequest.cacheMode = request.getCacheMode();
        downloadRequest.url = request.getBaseUrl();
        downloadRequest.params = request.getParams();
        downloadRequest.headers = request.getHeaders();
        downloadRequest.method = DownloadRequest.getMethod(request);

        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(downloadRequest);
            oos.flush();
            byte[] requestData = baos.toByteArray();
            values.put(DownloadInfo.DOWNLOAD_REQUEST, requestData);
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (baos != null) baos.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(downloadInfo.getData());
            oos.flush();
            byte[] data = baos.toByteArray();
            values.put(DownloadInfo.DATA, data);
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (oos != null) oos.close();
                if (baos != null) baos.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }
        return values;
    }

    public static DownloadInfo parseCursorToBean(Cursor cursor) {
        DownloadInfo info = new DownloadInfo();
        info.setId(cursor.getInt(cursor.getColumnIndex(DownloadInfo.ID)));
        info.setTaskKey(cursor.getString(cursor.getColumnIndex(DownloadInfo.TASK_KEY)));
        info.setUrl(cursor.getString(cursor.getColumnIndex(DownloadInfo.URL)));
        info.setTargetFolder(cursor.getString(cursor.getColumnIndex(DownloadInfo.TARGET_FOLDER)));
        info.setTargetPath(cursor.getString(cursor.getColumnIndex(DownloadInfo.TARGET_PATH)));
        info.setFileName(cursor.getString(cursor.getColumnIndex(DownloadInfo.FILE_NAME)));
        info.setProgress(cursor.getFloat(cursor.getColumnIndex(DownloadInfo.PROGRESS)));
        info.setTotalLength(cursor.getLong(cursor.getColumnIndex(DownloadInfo.TOTAL_LENGTH)));
        info.setDownloadLength(cursor.getLong(cursor.getColumnIndex(DownloadInfo.DOWNLOAD_LENGTH)));
        info.setNetworkSpeed(cursor.getLong(cursor.getColumnIndex(DownloadInfo.NETWORK_SPEED)));
        info.setState(cursor.getInt(cursor.getColumnIndex(DownloadInfo.STATE)));

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        byte[] requestData = cursor.getBlob(cursor.getColumnIndex(DownloadInfo.DOWNLOAD_REQUEST));
        try {
            if (requestData != null) {
                bais = new ByteArrayInputStream(requestData);
                ois = new ObjectInputStream(bais);
                DownloadRequest downloadRequest = (DownloadRequest) ois.readObject();
                info.setDownloadRequest(downloadRequest);
                BaseRequest request = DownloadRequest.createRequest(downloadRequest.url, downloadRequest.method);
                if (request != null) {
                    request.cacheMode(downloadRequest.cacheMode);
                    request.cacheTime(downloadRequest.cacheTime);
                    request.cacheKey(downloadRequest.cacheKey);
                    request.params(downloadRequest.params);
                    request.headers(downloadRequest.headers);
                    info.setRequest(request);
                }
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (ois != null) ois.close();
                if (bais != null) bais.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        byte[] data = cursor.getBlob(cursor.getColumnIndex(DownloadInfo.DATA));
        try {
            if (data != null) {
                bais = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bais);
                Serializable serializableData = (Serializable) ois.readObject();
                info.setData(serializableData);
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (ois != null) ois.close();
                if (bais != null) bais.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }
        return info;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskKey() {
        return taskKey;
    }

    public void setTaskKey(String taskKey) {
        this.taskKey = taskKey;
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

    public BaseRequest getRequest() {
        return request;
    }

    public void setRequest(BaseRequest request) {
        this.request = request;
    }

    public DownloadTask getTask() {
        return task;
    }

    public void setTask(DownloadTask task) {
        this.task = task;
    }

    public DownloadRequest getDownloadRequest() {
        return downloadRequest;
    }

    public void setDownloadRequest(DownloadRequest downloadRequest) {
        this.downloadRequest = downloadRequest;
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

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    /** taskKey 相同就认为是同一个任务 */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof DownloadInfo) {
            DownloadInfo info = (DownloadInfo) o;
            return getTaskKey().equals(info.getTaskKey());
        }
        return false;
    }

    /** 两个任务排序按照 id 的大小排序 */
    @Override
    public int compareTo(DownloadInfo another) {
        if (another == null) return 0;
        return ((Integer) getId()).compareTo(another.getId());
    }
}