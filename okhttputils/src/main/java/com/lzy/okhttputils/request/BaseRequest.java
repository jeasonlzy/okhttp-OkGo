package com.lzy.okhttputils.request;

import android.support.annotation.NonNull;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheManager;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.https.HttpsUtils;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.utils.HeaderParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：所有请求的基类，其中泛型 R 主要用于属性设置方法后，返回对应的子类型，以便于实现链式调用
 * 修订历史：
 * ================================================
 */
public abstract class BaseRequest<R extends BaseRequest> {
    protected String url;
    protected Object tag;
    protected long readTimeOut;
    protected long writeTimeOut;
    protected long connectTimeout;
    protected CacheMode cacheMode;
    protected String cacheKey;
    protected InputStream[] certificates;
    protected HostnameVerifier hostnameVerifier;
    protected HttpParams params = new HttpParams();
    protected HttpHeaders headers = new HttpHeaders();
    private AbsCallback mCallback;
    private CacheManager cacheManager;

    public BaseRequest(String url) {
        this.url = url;
        OkHttpUtils okHttpUtils = OkHttpUtils.getInstance();
        cacheManager = CacheManager.INSTANCE;
        //添加公共请求参数
        if (okHttpUtils.getCommonParams() != null) {
            params.put(okHttpUtils.getCommonParams());
        }
        if (okHttpUtils.getCommonHeaders() != null) {
            headers.put(okHttpUtils.getCommonHeaders());
        }
        //添加缓存模式
        if (okHttpUtils.getCacheMode() != null) {
            cacheMode = okHttpUtils.getCacheMode();
        }
    }

    @SuppressWarnings("unchecked")
    public R url(@NonNull String url) {
        this.url = url;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R connTimeOut(long connTimeOut) {
        this.connectTimeout = connTimeOut;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setCertificates(InputStream... certificates) {
        this.certificates = certificates;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, String value) {
        params.put(key, value);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, File file) {
        params.put(key, file);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, File file, String fileName) {
        params.put(key, file, fileName);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R params(String key, File file, String fileName, MediaType contentType) {
        params.put(key, file, fileName, contentType);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeUrlParam(String key) {
        params.removeUrl(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R removeFileParam(String key) {
        params.removeFile(key);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    public R setCallback(AbsCallback callback) {
        this.mCallback = callback;
        return (R) this;
    }

    public HttpParams getParams() {
        return params;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    /** 将传递进来的参数拼接成 url */
    protected String createUrlFromParams(String url, Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf('&') > 0 || url.indexOf('?') > 0) sb.append("&");
            else sb.append("?");
            for (Map.Entry<String, String> urlParams : params.entrySet()) {
                String urlValue = URLEncoder.encode(urlParams.getValue(), "UTF-8");
                sb.append(urlParams.getKey()).append("=").append(urlValue).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }

    /** 通用的拼接请求头 */
    protected Request.Builder appendHeaders(Request.Builder requestBuilder) {
        Headers.Builder headerBuilder = new Headers.Builder();
        ConcurrentHashMap<String, String> headerMap = headers.headersMap;
        if (headerMap.isEmpty()) return requestBuilder;
        for (String key : headerMap.keySet()) {
            headerBuilder.add(key, headerMap.get(key));
        }
        requestBuilder.headers(headerBuilder.build());
        return requestBuilder;
    }

    /** 根据不同的请求方式和参数，生成不同的RequestBody */
    protected abstract RequestBody generateRequestBody();

    /** 生成类是表单的请求体 */
    protected RequestBody generateMultipartRequestBody() {
        if (params.fileParamsMap.isEmpty()) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : params.urlParamsMap.keySet()) {
                bodyBuilder.add(key, params.urlParamsMap.get(key));
            }
            return bodyBuilder.build();
        } else {
            //表单提交，有文件
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //拼接键值对
            if (!params.urlParamsMap.isEmpty()) {
                for (Map.Entry<String, String> entry : params.urlParamsMap.entrySet()) {
                    multipartBodybuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
            //拼接文件
            for (Map.Entry<String, HttpParams.FileWrapper> entry : params.fileParamsMap.entrySet()) {
                RequestBody fileBody = RequestBody.create(entry.getValue().contentType, entry.getValue().file);
                multipartBodybuilder.addFormDataPart(entry.getKey(), entry.getValue().fileName, fileBody);
            }
            return multipartBodybuilder.build();
        }
    }

    /** 对请求body进行包装，用于回调上传进度 */
    protected RequestBody wrapRequestBody(RequestBody requestBody) {
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody);
        progressRequestBody.setListener(new ProgressRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength, final long networkSpeed) {
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null)
                            mCallback.upProgress(bytesWritten, contentLength, bytesWritten * 1.0f / contentLength, networkSpeed);
                    }
                });
            }
        });
        return progressRequestBody;
    }

    /** 根据不同的请求方式，将RequestBody转换成Request对象 */
    protected abstract Request generateRequest(RequestBody requestBody);

    /** 根据当前的请求参数，生成对应的 Call 任务 */
    protected Call generateCall(Request request) {
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && certificates == null) {
            return OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        } else {
            OkHttpClient.Builder newClientBuilder = OkHttpUtils.getInstance().getOkHttpClient().newBuilder();
            if (readTimeOut > 0) newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            if (writeTimeOut > 0) newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            if (connectTimeout > 0) newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            if (hostnameVerifier != null) newClientBuilder.hostnameVerifier(hostnameVerifier);
            if (certificates != null) {
                newClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
            }
            return newClientBuilder.build().newCall(request);
        }
    }

    /** 阻塞方法，同步请求执行 */
    public Response execute() throws IOException {
        //添加缓存头和其他的公共头，同步请求不做缓存，缓存为空
        HeaderParser.addDefaultHeaders(this, null, null);

        //构建请求体，同步阻塞请求
        RequestBody requestBody = generateRequestBody();
        final Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);
        return call.execute();
    }

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    @SuppressWarnings("unchecked")
    public <T> void execute(AbsCallback<T> callback) {
        mCallback = callback;
        if (mCallback == null) mCallback = AbsCallback.CALLBACK_DEFAULT;

        //请求之前获取缓存信息，添加缓存头和其他的公共头
        if (cacheKey == null) cacheKey = createUrlFromParams(url, params.urlParamsMap);
        if (cacheMode == null) cacheMode = CacheMode.DEFAULT;
        final CacheEntity<T> cacheEntity = (CacheEntity<T>) cacheManager.get(cacheKey);
        HeaderParser.addDefaultHeaders(this, cacheEntity, cacheMode);

        //请求执行前UI线程调用
        mCallback.onBefore(this);
        RequestBody requestBody = generateRequestBody();
        Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);

        if (cacheMode == CacheMode.IF_NONE_CACHE_REQUEST) {
            //如果没有缓存，就请求网络，否者直接使用缓存
            if (cacheEntity != null) {
                T data = cacheEntity.getData();
                sendSuccessResultCallback(true, data, call, null, mCallback);
                return;//返回即不请求网络
            } else {
                sendFailResultCallback(true, call, null, new IllegalStateException("没有获取到缓存！"), mCallback);
            }
        } else if (cacheMode == CacheMode.FIRST_CACHE_THEN_REQUEST) {
            //先使用缓存，不管是否存在，仍然请求网络
            if (cacheEntity != null) {
                T data = cacheEntity.getData();
                sendSuccessResultCallback(true, data, call, null, mCallback);
            } else {
                sendFailResultCallback(true, call, null, new IllegalStateException("没有获取到缓存！"), mCallback);
            }
        }

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //请求失败，一般为url地址错误，网络错误等
                sendFailResultCallback(false, call, null, e, mCallback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                int responseCode = response.code();
                //304缓存数据
                if (responseCode == 304 && cacheMode == CacheMode.DEFAULT) {
                    if (cacheEntity == null) {
                        sendFailResultCallback(true, call, response, new IllegalStateException("服务器响应码304，但是客户端没有缓存！"), mCallback);
                    } else {
                        T data = cacheEntity.getData();
                        sendSuccessResultCallback(true, data, call, response, mCallback);
                    }
                    return;
                }
                //响应失败，一般为服务器内部错误，或者找不到页面等
                if (responseCode >= 400 && responseCode <= 599) {
                    sendFailResultCallback(false, call, response, null, mCallback);
                    return;
                }

                T data = (T) mCallback.parseNetworkResponse(response);
                sendSuccessResultCallback(false, data, call, response, mCallback);
                //网络请求成功，保存缓存数据
                handleCache(response.headers(), data);
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
    private <T> void handleCache(Headers headers, T data) {
        // DEFAULT 默认遵循 304 规则，其他缓存模式忽略 304 缓存头
        boolean forceCache = (cacheMode != CacheMode.DEFAULT);
        CacheEntity<T> cache = HeaderParser.parseCacheHeaders(headers, data, cacheKey, forceCache);
        if (cache == null) {
            //服务器不需要缓存，移除本地缓存
            cacheManager.remove(cacheKey);
        } else {
            //缓存命中，更新缓存
            cacheManager.replace(cacheKey, (CacheEntity<Object>) cache);
        }
    }

    /** 失败回调，发送到主线程 */
    @SuppressWarnings("unchecked")
    private <T> void sendFailResultCallback(final boolean isFromCache, final Call call,//
                                            final Response response, final Exception e, final AbsCallback<T> callback) {

        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                callback.onError(isFromCache, call, response, e);         //请求失败回调 （UI线程）
                callback.onAfter(isFromCache, null, call, response, e);   //请求结束回调 （UI线程）
            }
        });

        //不同的缓存模式，可能会导致该失败进入两次，一次缓存失败，一次网络请求失败
        if (!isFromCache && cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
            CacheEntity<T> cacheEntity = (CacheEntity<T>) cacheManager.get(cacheKey);
            if (cacheEntity != null) {
                T data = cacheEntity.getData();
                sendSuccessResultCallback(true, data, call, response, callback);
            } else {
                sendFailResultCallback(true, call, response, new IllegalStateException("请求网络失败后，无法读取缓存或者缓存不存在！"), callback);
            }
        }
    }

    /** 成功回调，发送到主线程 */
    private <T> void sendSuccessResultCallback(final boolean isFromCache, final T t, //
                                               final Call call, final Response response, final AbsCallback<T> callback) {
        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(isFromCache, t, call.request(), response);                         //请求成功回调 （UI线程）
                callback.onAfter(isFromCache, t, call, response, null);      //请求结束回调 （UI线程）
            }
        });
    }
}