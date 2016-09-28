package com.lzy.okgo.cache;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.utils.OkLogger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class CacheEntity<T> implements Serializable {

    private static final long serialVersionUID = -4337711009801627866L;

    public static final long CACHE_NEVER_EXPIRE = -1;        //缓存永不过期

    private long id;
    private String key;
    private HttpHeaders responseHeaders;
    private T data;
    private long localExpire;

    //该变量不必保存到数据库,程序运行起来后会动态计算
    private boolean isExpire;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HttpHeaders getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(HttpHeaders responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getLocalExpire() {
        return localExpire;
    }

    public void setLocalExpire(long localExpire) {
        this.localExpire = localExpire;
    }

    public boolean isExpire() {
        return isExpire;
    }

    public void setExpire(boolean expire) {
        isExpire = expire;
    }

    /**
     * @param cacheTime 允许的缓存时间
     * @param baseTime  基准时间,小于当前时间视为过期
     * @return 是否过期
     */
    public boolean checkExpire(CacheMode cacheMode, long cacheTime, long baseTime) {
        //304的默认缓存模式,设置缓存时间无效,需要依靠服务端的响应头控制
        if (cacheMode == CacheMode.DEFAULT) return getLocalExpire() < baseTime;
        if (cacheTime == CACHE_NEVER_EXPIRE) return false;
        return getLocalExpire() + cacheTime < baseTime;
    }

    public static <T> ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        ContentValues values = new ContentValues();
        values.put(CacheHelper.KEY, cacheEntity.getKey());
        values.put(CacheHelper.LOCAL_EXPIRE, cacheEntity.getLocalExpire());

        HttpHeaders headers = cacheEntity.getResponseHeaders();
        ByteArrayOutputStream headerBAOS = null;
        ObjectOutputStream headerOOS = null;
        try {
            if (headers != null) {
                headerBAOS = new ByteArrayOutputStream();
                headerOOS = new ObjectOutputStream(headerBAOS);
                headerOOS.writeObject(headers);
                headerOOS.flush();
                byte[] headerData = headerBAOS.toByteArray();
                values.put(CacheHelper.HEAD, headerData);
            }
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (headerOOS != null) headerOOS.close();
                if (headerBAOS != null) headerBAOS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        T data = cacheEntity.getData();
        ByteArrayOutputStream dataBAOS = null;
        ObjectOutputStream dataOOS = null;
        try {
            if (data != null) {
                dataBAOS = new ByteArrayOutputStream();
                dataOOS = new ObjectOutputStream(dataBAOS);
                dataOOS.writeObject(data);
                dataOOS.flush();
                byte[] dataData = dataBAOS.toByteArray();
                values.put(CacheHelper.DATA, dataData);
            }
        } catch (IOException e) {
            OkLogger.e(e);
        } finally {
            try {
                if (dataOOS != null) dataOOS.close();
                if (dataBAOS != null) dataBAOS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }
        return values;
    }

    public static <T> CacheEntity<T> parseCursorToBean(Cursor cursor) {
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setId(cursor.getInt(cursor.getColumnIndex(CacheHelper.ID)));
        cacheEntity.setKey(cursor.getString(cursor.getColumnIndex(CacheHelper.KEY)));
        cacheEntity.setLocalExpire(cursor.getLong(cursor.getColumnIndex(CacheHelper.LOCAL_EXPIRE)));

        byte[] headerData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.HEAD));
        ByteArrayInputStream headerBAIS = null;
        ObjectInputStream headerOIS = null;
        try {
            if (headerData != null) {
                headerBAIS = new ByteArrayInputStream(headerData);
                headerOIS = new ObjectInputStream(headerBAIS);
                Object header = headerOIS.readObject();
                cacheEntity.setResponseHeaders((HttpHeaders) header);
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (headerOIS != null) headerOIS.close();
                if (headerBAIS != null) headerBAIS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        byte[] dataData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.DATA));
        ByteArrayInputStream dataBAIS = null;
        ObjectInputStream dataOIS = null;
        try {
            if (dataData != null) {
                dataBAIS = new ByteArrayInputStream(dataData);
                dataOIS = new ObjectInputStream(dataBAIS);
                T data = (T) dataOIS.readObject();
                cacheEntity.setData(data);
            }
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            try {
                if (dataOIS != null) dataOIS.close();
                if (dataBAIS != null) dataBAIS.close();
            } catch (IOException e) {
                OkLogger.e(e);
            }
        }

        return cacheEntity;
    }

    @Override
    public String toString() {
        return "CacheEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", responseHeaders=" + responseHeaders +
                ", data=" + data +
                ", localExpire=" + localExpire +
                '}';
    }
}