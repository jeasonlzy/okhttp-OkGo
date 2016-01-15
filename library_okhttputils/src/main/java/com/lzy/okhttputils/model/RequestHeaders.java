package com.lzy.okhttputils.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2015/10/10
 * 描    述：请求头的包装类
 * 修订历史：
 * ================================================
 */
public class RequestHeaders {

    public ConcurrentHashMap<String, String> headersMap;

    private void init() {
        headersMap = new ConcurrentHashMap<>();
    }

    public RequestHeaders() {
        init();
    }

    public RequestHeaders(String key, String value) {
        init();
        put(key, value);
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            headersMap.put(key, value);
        }
    }

    public void put(RequestHeaders headers) {
        if (headers != null) {
            if (headers.headersMap != null && !headers.headersMap.isEmpty())
                headersMap.putAll(headers.headersMap);
        }
    }
}
