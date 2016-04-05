package com.lzy.okhttputils.cache;


import com.lzy.okhttputils.model.HttpHeaders;

import java.io.Serializable;

public class CacheEntity<T> implements Serializable {

    private long id;
    private String key;
    private HttpHeaders headers;
    private T data;
    private long localExpire;

    public CacheEntity() {
    }

    public CacheEntity(long id, String key, HttpHeaders headers, T data, long localExpire) {
        this.id = id;
        this.key = key;
        this.headers = headers;
        this.data = data;
        this.localExpire = localExpire;
    }

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

    public HttpHeaders getHeaders() {
        return headers;
    }

    public void setHeaders(HttpHeaders headers) {
        this.headers = headers;
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

    @Override
    public String toString() {
        return "CacheEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", headers=" + headers +
                ", data=" + data +
                ", localExpire=" + localExpire +
                '}';
    }
}
