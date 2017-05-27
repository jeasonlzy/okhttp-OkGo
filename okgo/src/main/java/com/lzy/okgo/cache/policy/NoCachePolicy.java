package com.lzy.okgo.cache.policy;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.HttpResponse;
import com.lzy.okgo.request.HttpRequest;

import okhttp3.Call;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NoCachePolicy<T> extends BaseCachePolicy<T> {

    public NoCachePolicy(HttpRequest<T, ? extends HttpRequest> request) {
        super(request);
    }

    @Override
    public void onSuccess(final HttpResponse<T> success) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onSuccess(success.body(), success);
                mCallback.onFinish(success);
            }
        });
    }

    @Override
    public void onError(final HttpResponse<T> error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onError(error.getException(), error);
                mCallback.onFinish(error);
            }
        });
    }

    @Override
    public HttpResponse<T> requestSync(CacheEntity<T> cacheEntity, Call rawCall) {
        return requestNetworkSync();
    }

    @Override
    public void requestAsync(CacheEntity<T> cacheEntity, Call rawCall, Callback<T> callback) {
        mCallback = callback;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCallback.onStart(httpRequest);
            }
        });
        requestNetworkAsync();
    }
}
