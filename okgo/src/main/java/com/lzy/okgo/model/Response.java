package com.lzy.okgo.model;

import okhttp3.Headers;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：响应体的包装类
 * 修订历史：
 * ================================================
 */
public final class Response<T> {

    private final okhttp3.Response rawResponse;
    private final T body;

    private Response(okhttp3.Response rawResponse, T body) {
        this.rawResponse = rawResponse;
        this.body = body;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public int code() {
        return rawResponse.code();
    }

    public String message() {
        return rawResponse.message();
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public T body() {
        return body;
    }

    public static <T> Response<T> success(T body, okhttp3.Response rawResponse) {
        if (rawResponse == null) throw new NullPointerException("rawResponse == null");
        if (!rawResponse.isSuccessful()) throw new IllegalArgumentException("rawResponse must be successful response");
        return new Response<>(rawResponse, body);
    }
}