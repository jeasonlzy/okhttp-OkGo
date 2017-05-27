/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.okgo.adapter;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.policy.CachePolicy;
import com.lzy.okgo.cache.policy.DefaultCachePolicy;
import com.lzy.okgo.cache.policy.FirstCacheRequestPolicy;
import com.lzy.okgo.cache.policy.NoCachePolicy;
import com.lzy.okgo.cache.policy.NoneCacheRequestPolicy;
import com.lzy.okgo.cache.policy.RequestFailedCachePolicy;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.HttpRequest;
import com.lzy.okgo.utils.TypeUtils;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：带缓存的请求
 * 修订历史：
 * ================================================
 */
public class CacheCall<T> implements Call<T> {

    private CachePolicy<T> policy = null;
    private HttpRequest<T, ? extends HttpRequest> httpRequest;

    public CacheCall(HttpRequest<T, ? extends HttpRequest> httpRequest) {
        this.httpRequest = httpRequest;
    }

    @Override
    public Response<T> execute() {
        policy = preparePolicy();
        CacheEntity<T> cacheEntity = policy.prepareCache();
        okhttp3.Call rawCall = policy.prepareRawCall();
        return policy.requestSync(cacheEntity, rawCall);
    }

    @Override
    public void execute(Callback<T> callback) {
        TypeUtils.checkNotNull(callback, "callback == null");

        policy = preparePolicy();
        CacheEntity<T> cacheEntity = policy.prepareCache();
        okhttp3.Call rawCall = policy.prepareRawCall();
        policy.requestAsync(cacheEntity, rawCall, callback);
    }

    private CachePolicy<T> preparePolicy() {
        switch (httpRequest.getCacheMode()) {
            case DEFAULT:
                policy = new DefaultCachePolicy<>(httpRequest);
                break;
            case NO_CACHE:
                policy = new NoCachePolicy<>(httpRequest);
                break;
            case IF_NONE_CACHE_REQUEST:
                policy = new NoneCacheRequestPolicy<>(httpRequest);
                break;
            case FIRST_CACHE_THEN_REQUEST:
                policy = new FirstCacheRequestPolicy<>(httpRequest);
                break;
            case REQUEST_FAILED_READ_CACHE:
                policy = new RequestFailedCachePolicy<>(httpRequest);
                break;
        }
        if (httpRequest.getCachePolicy() != null) {
            policy = httpRequest.getCachePolicy();
        }
        TypeUtils.checkNotNull(policy, "policy == null");
        return policy;
    }

    @Override
    public boolean isExecuted() {
        return policy.isExecuted();
    }

    @Override
    public void cancel() {
        policy.cancel();
    }

    @Override
    public boolean isCanceled() {
        return policy.isCanceled();
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    @Override
    public Call<T> clone() {
        return new CacheCall<>(httpRequest);
    }

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }
}
