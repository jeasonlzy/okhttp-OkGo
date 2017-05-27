package com.lzy.okgo.cache.policy;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.exception.CacheException;
import com.lzy.okgo.model.HttpResponse;
import com.lzy.okgo.request.HttpRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DefaultCachePolicy<T> extends BaseCachePolicy<T> {

    public DefaultCachePolicy(HttpRequest<T, ? extends HttpRequest> request) {
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
    public boolean onAnalysisResponse(final Call call, final Response response) {
        if (response.code() != 304) return false;

        if (cacheEntity == null) {
            final HttpResponse<T> error = HttpResponse.error(true, call, response, CacheException.NON_AND_304(httpRequest.getCacheKey()));
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onError(error.getException(), error);
                    mCallback.onFinish(error);
                }
            });
        } else {
            final HttpResponse<T> success = HttpResponse.success(true, cacheEntity.getData(), call, response);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCallback.onCacheSuccess(success.body(), success);
                    mCallback.onFinish(success);
                }
            });
        }
        return true;
    }

    @Override
    public HttpResponse<T> requestSync(CacheEntity<T> cacheEntity, okhttp3.Call rawCall) {
        HttpResponse<T> response = requestNetworkSync();
        //HTTP cache protocol
        if (response.isSuccessful() && response.code() == 304) {
            if (cacheEntity == null) {
                response = HttpResponse.error(true, rawCall, response.getRawResponse(), CacheException.NON_AND_304(httpRequest.getCacheKey()));
            } else {
                response = HttpResponse.success(true, cacheEntity.getData(), rawCall, response.getRawResponse());
            }
        }
        return response;
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
