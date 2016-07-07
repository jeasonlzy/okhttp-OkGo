package com.lzy.okhttputils.cache;

import com.lzy.okhttputils.model.HttpHeaders;

import java.io.Serializable;

public class CacheEntity<T> implements Serializable {

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
