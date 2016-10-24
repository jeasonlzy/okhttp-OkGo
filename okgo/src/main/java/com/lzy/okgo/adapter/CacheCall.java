package com.lzy.okgo.adapter;

import android.graphics.Bitmap;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheManager;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.callback.AbsCallbackWrapper;
import com.lzy.okgo.exception.OkGoException;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.BaseRequest;
import com.lzy.okgo.utils.HeaderParser;
import com.lzy.okgo.utils.HttpUtils;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：带缓存的请求
 * 修订历史：
 * ================================================
 */
public class CacheCall<T> implements Call<T> {

    private volatile boolean canceled;
    private boolean executed;
    private BaseRequest baseRequest;
    private okhttp3.Call rawCall;
    private CacheEntity<T> cacheEntity;
    private AbsCallback<T> mCallback;

    private int currentRetryCount;

    public CacheCall(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    @Override
    public void execute(AbsCallback<T> callback) {

        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        mCallback = callback;
        if (mCallback == null) mCallback = new AbsCallbackWrapper<>();

        //请求执行前UI线程调用
        mCallback.onBefore(baseRequest);
        //请求之前获取缓存信息，添加缓存头和其他的公共头
        if (baseRequest.getCacheKey() == null)
            baseRequest.setCacheKey(HttpUtils.createUrlFromParams(baseRequest.getBaseUrl(), baseRequest.getParams().urlParamsMap));
        if (baseRequest.getCacheMode() == null) baseRequest.setCacheMode(CacheMode.NO_CACHE);

        //无缓存模式,不需要进入缓存逻辑
        final CacheMode cacheMode = baseRequest.getCacheMode();
        if (cacheMode != CacheMode.NO_CACHE) {
            //noinspection unchecked
            cacheEntity = (CacheEntity<T>) CacheManager.INSTANCE.get(baseRequest.getCacheKey());
            //检查缓存的有效时间,判断缓存是否已经过期
            if (cacheEntity != null && cacheEntity.checkExpire(cacheMode, baseRequest.getCacheTime(), System.currentTimeMillis())) {
                cacheEntity.setExpire(true);
            }
            HeaderParser.addCacheHeaders(baseRequest, cacheEntity, cacheMode);
        }
        //构建请求
        RequestBody requestBody = baseRequest.generateRequestBody();
        final Request request = baseRequest.generateRequest(baseRequest.wrapRequestBody(requestBody));
        rawCall = baseRequest.generateCall(request);

        if (cacheMode == CacheMode.IF_NONE_CACHE_REQUEST) {
            //如果没有缓存，或者缓存过期,就请求网络，否者直接使用缓存
            if (cacheEntity != null && !cacheEntity.isExpire()) {
                T data = cacheEntity.getData();
                HttpHeaders headers = cacheEntity.getResponseHeaders();
                if (data == null || headers == null) {
                    //由于没有序列化等原因,可能导致数据为空
                    sendFailResultCallback(true, rawCall, null, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
                } else {
                    sendSuccessResultCallback(true, data, rawCall, null);
                    return;//获取缓存成功,不请求网络
                }
            } else {
                sendFailResultCallback(true, rawCall, null, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
            }
        } else if (cacheMode == CacheMode.FIRST_CACHE_THEN_REQUEST) {
            //先使用缓存，不管是否存在，仍然请求网络
            if (cacheEntity != null && !cacheEntity.isExpire()) {
                T data = cacheEntity.getData();
                HttpHeaders headers = cacheEntity.getResponseHeaders();
                if (data == null || headers == null) {
                    //由于没有序列化等原因,可能导致数据为空
                    sendFailResultCallback(true, rawCall, null, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
                } else {
                    sendSuccessResultCallback(true, data, rawCall, null);
                }
            } else {
                sendFailResultCallback(true, rawCall, null, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
            }
        }

        if (canceled) {
            rawCall.cancel();
        }
        currentRetryCount = 0;
        rawCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                if (e instanceof SocketTimeoutException && currentRetryCount < baseRequest.getRetryCount()) {
                    //超时重试处理
                    currentRetryCount++;
                    okhttp3.Call newCall = baseRequest.generateCall(call.request());
                    newCall.enqueue(this);
                } else {
                    mCallback.parseError(call, e);
                    //请求失败，一般为url地址错误，网络错误等,并且过滤用户主动取消的网络请求
                    if (!call.isCanceled()) {
                        sendFailResultCallback(false, call, null, e);
                    }
                }
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                int responseCode = response.code();
                //304缓存数据
                if (responseCode == 304 && cacheMode == CacheMode.DEFAULT) {
                    if (cacheEntity == null) {
                        sendFailResultCallback(true, call, response, OkGoException.INSTANCE("服务器响应码304，但是客户端没有缓存！"));
                    } else {
                        T data = cacheEntity.getData();
                        HttpHeaders headers = cacheEntity.getResponseHeaders();
                        if (data == null || headers == null) {
                            //由于没有序列化等原因,可能导致数据为空
                            sendFailResultCallback(true, call, response, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
                        } else {
                            sendSuccessResultCallback(true, data, call, response);
                        }
                    }
                    return;
                }
                //响应失败，一般为服务器内部错误，或者找不到页面等
                if (responseCode == 404 || responseCode >= 500) {
                    sendFailResultCallback(false, call, response, OkGoException.INSTANCE("服务器数据异常!"));
                    return;
                }

                try {
                    Response<T> parseResponse = parseResponse(response);
                    T data = parseResponse.body();
                    //网络请求成功，保存缓存数据
                    handleCache(response.headers(), data);
                    //网络请求成功回调
                    sendSuccessResultCallback(false, data, call, response);
                } catch (Exception e) {
                    //一般为服务器响应成功，但是数据解析错误
                    sendFailResultCallback(false, call, response, e);
                }
            }
        });
    }

    /**
     * 请求成功后根据缓存模式，更新缓存数据
     *
     * @param headers 响应头
     * @param data    响应数据
     */
    @SuppressWarnings("unchecked")
    private void handleCache(Headers headers, T data) {
        if (baseRequest.getCacheMode() == CacheMode.NO_CACHE) return;    //不需要缓存,直接返回
        if (data instanceof Bitmap) return;             //Bitmap没有实现Serializable,不能缓存

        CacheEntity<T> cache = HeaderParser.createCacheEntity(headers, data, baseRequest.getCacheMode(), baseRequest.getCacheKey());
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            CacheManager.INSTANCE.remove(baseRequest.getCacheKey());
        } else {
            //缓存命中，更新缓存
            CacheManager.INSTANCE.replace(baseRequest.getCacheKey(), (CacheEntity<Object>) cache);
        }
    }

    /** 失败回调，发送到主线程 */
    @SuppressWarnings("unchecked")
    private void sendFailResultCallback(final boolean isFromCache, final okhttp3.Call call, final okhttp3.Response response, final Exception e) {
        final CacheMode cacheMode = baseRequest.getCacheMode();

        OkGo.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                if (isFromCache) {
                    mCallback.onCacheError(call, e);             //缓存失败回调 （UI线程）
                    if (cacheMode == CacheMode.DEFAULT || cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
                        mCallback.onAfter(null, e);              //请求结束回调 （UI线程）
                    }
                } else {
                    mCallback.onError(call, response, e);        //请求失败回调 （UI线程）
                    if (cacheMode != CacheMode.REQUEST_FAILED_READ_CACHE) {
                        mCallback.onAfter(null, e);              //请求结束回调 （UI线程）
                    }
                }
            }
        });

        //不同的缓存模式，可能会导致该失败进入两次，一次缓存失败，一次网络请求失败
        if (!isFromCache && cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
            if (cacheEntity != null && !cacheEntity.isExpire()) {
                T data = cacheEntity.getData();
                HttpHeaders headers = cacheEntity.getResponseHeaders();
                if (data == null || headers == null) {
                    //由于没有序列化等原因,可能导致数据为空
                    sendFailResultCallback(true, call, response, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
                } else {
                    sendSuccessResultCallback(true, data, call, response);
                }
            } else {
                sendFailResultCallback(true, call, response, OkGoException.INSTANCE("没有获取到缓存,或者缓存已经过期!"));
            }
        }
    }

    /** 成功回调，发送到主线程 */
    private void sendSuccessResultCallback(final boolean isFromCache, final T t, final okhttp3.Call call, final okhttp3.Response response) {
        final CacheMode cacheMode = baseRequest.getCacheMode();

        OkGo.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                if (isFromCache) {
                    mCallback.onCacheSuccess(t, call);           //缓存成功回调 （UI线程）
                    if (cacheMode == CacheMode.DEFAULT || cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE || cacheMode == CacheMode.IF_NONE_CACHE_REQUEST) {
                        mCallback.onAfter(t, null);              //请求结束回调 （UI线程）
                    }
                } else {
                    mCallback.onSuccess(t, call, response);      //请求成功回调 （UI线程）
                    mCallback.onAfter(t, null);                  //请求结束回调 （UI线程）
                }
            }
        });
    }

    @Override
    public Response<T> execute() throws Exception {
        synchronized (this) {
            if (executed) throw new IllegalStateException("Already executed.");
            executed = true;
        }
        okhttp3.Call call = baseRequest.getCall();
        if (canceled) {
            call.cancel();
        }
        return parseResponse(call.execute());
    }

    private Response<T> parseResponse(okhttp3.Response rawResponse) throws Exception {
        //noinspection unchecked
        T body = (T) baseRequest.getConverter().convertSuccess(rawResponse);
        return Response.success(body, rawResponse);
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public void cancel() {
        canceled = true;
        if (rawCall != null) {
            rawCall.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return canceled;
    }

    @Override
    public Call<T> clone() {
        return new CacheCall<>(baseRequest);
    }

    @Override
    public BaseRequest getBaseRequest() {
        return baseRequest;
    }
}