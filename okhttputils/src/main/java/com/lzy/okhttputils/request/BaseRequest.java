package com.lzy.okhttputils.request;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheManager;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.https.HttpsUtils;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.model.HttpParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
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

    public R url(@NonNull String url) {
        this.url = url;
        return (R) this;
    }

    public R tag(Object tag) {
        this.tag = tag;
        return (R) this;
    }

    public R readTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
        return (R) this;
    }

    public R writeTimeOut(long writeTimeOut) {
        this.writeTimeOut = writeTimeOut;
        return (R) this;
    }

    public R connTimeOut(long connTimeOut) {
        this.connectTimeout = connTimeOut;
        return (R) this;
    }

    public R cacheMode(CacheMode cacheMode) {
        this.cacheMode = cacheMode;
        return (R) this;
    }

    public R cacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
        return (R) this;
    }

    public R setCertificates(@NonNull InputStream... certificates) {
        this.certificates = certificates;
        return (R) this;
    }

    public R headers(HttpHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R removeHeader(String key) {
        headers.remove(key);
        return (R) this;
    }

    public R params(HttpParams params) {
        this.params.put(params);
        return (R) this;
    }

    public R params(String key, String value) {
        params.put(key, value);
        return (R) this;
    }

    public R params(String key, File file) {
        params.put(key, file);
        return (R) this;
    }

    public R params(String key, File file, String fileName) {
        params.put(key, file, fileName);
        return (R) this;
    }

    public R params(String key, File file, String fileName, MediaType contentType) {
        params.put(key, file, fileName, contentType);
        return (R) this;
    }

    public R removeUrlParam(String key) {
        params.removeUrl(key);
        return (R) this;
    }

    public R removeFileParam(String key) {
        params.removeFile(key);
        return (R) this;
    }

    public AbsCallback getCallback() {
        return mCallback;
    }

    public void setCallback(AbsCallback callback) {
        this.mCallback = callback;
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
    public abstract RequestBody generateRequestBody();

    /** 对请求body进行包装，用于回调上传进度 */
    public RequestBody wrapRequestBody(RequestBody requestBody) {
        return new ProgressRequestBody(requestBody, new ProgressRequestBody.Listener() {
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
    }

    /** 根据不同的请求方式，将RequestBody转换成Request对象 */
    public abstract Request generateRequest(RequestBody requestBody);

    /** 根据当前的请求参数，生成对应的 Call 任务 */
    public Call generateCall(Request request) {
        if (readTimeOut <= 0 && writeTimeOut <= 0 && connectTimeout <= 0 && certificates == null) {
            return OkHttpUtils.getInstance().getOkHttpClient().newCall(request);
        } else {
            OkHttpClient.Builder newClientBuilder = OkHttpUtils.getInstance().getOkHttpClient().newBuilder();
            if (readTimeOut > 0) newClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
            if (writeTimeOut > 0) newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            if (connectTimeout > 0) newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            if (certificates != null)
                newClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
            return newClientBuilder.build().newCall(request);
        }
    }

    /** 阻塞方法，同步请求执行 */
    public Response execute() throws IOException {
        addDefaultHeaders();
        RequestBody requestBody = generateRequestBody();
        final Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);
        return call.execute();
    }

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    public <T> void execute(AbsCallback<T> callback) {
        mCallback = callback;
        if (mCallback == null) mCallback = AbsCallback.CALLBACK_DEFAULT;

        final CacheEntity<T> cacheEntity = addDefaultHeaders();

        mCallback.onBefore(this);      //请求执行前调用 （UI线程）
        RequestBody requestBody = generateRequestBody();
        Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);

        switch (cacheMode) {
            //只读取缓存
            case ONLY_READ_CACHE:
                if (cacheEntity == null) {
                    sendFailResultCallback(true, call, null, new IllegalArgumentException("缓存不存在，无法读取！"), mCallback);
                    return;
                } else {
                    T data = cacheEntity.getData();
                    sendSuccessResultCallback(true, data, call, null, mCallback);
                    return;
                }
                //如果没有缓存，就请求网络，否者直接使用缓存
            case IF_NONE_CACHE_REQUEST:
                if (cacheEntity != null) {
                    T data = cacheEntity.getData();
                    sendSuccessResultCallback(true, data, call, null, mCallback);
                    return;
                }
                break;
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
                if (responseCode == 304) {
                    if (cacheEntity == null) {
                        sendFailResultCallback(true, call, response, new IllegalArgumentException("服务器响应码304，但是客户端没有缓存！"), mCallback);
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

                try {
                    //解析过程中抛出异常，一般为 json 格式错误，或者数据解析异常
                    T data = (T) mCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(false, data, call, response, mCallback);
                    // 请求成功缓存数据
                    CacheEntity<T> finalCache = cacheEntity;
                    if (finalCache == null) finalCache = parseCacheHeaders(response.headers(), data, true);
                    if (finalCache != null) cacheManager.replace(cacheKey, (CacheEntity<Object>) finalCache);
                } catch (Exception e) {
                    sendFailResultCallback(false, call, response, e, mCallback);
                }
            }
        });
    }

    /**
     * 对每个请求添加默认的请求头，如果有缓存，并返回缓存实体对象
     */
    @Nullable
    private <T> CacheEntity<T> addDefaultHeaders() {
        //如果没有明确的指定key，默认使用url和和请求参数的拼接
        if (cacheKey == null) cacheKey = createUrlFromParams(url, params.urlParamsMap);
        if (cacheMode == null) cacheMode = CacheMode.DEFAULT;
        //TODO 可能会报强制转换错误，处理方法，如果异常，请求网络
        final CacheEntity<T> cacheEntity = (CacheEntity<T>) cacheManager.get(cacheKey);

        //1. 按照标准的 http 协议，添加304相关响应头
        if (cacheEntity == null) {
            removeHeader(HttpHeaders.HEAD_KEY_IF_NONE_MATCH);
            removeHeader(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE);
        } else if (cacheEntity.getLocalExpire() < System.currentTimeMillis()) {
            HttpHeaders headers = cacheEntity.getHeaders();
            String eTag = headers.get(HttpHeaders.HEAD_KEY_E_TAG);
            if (eTag != null) headers(HttpHeaders.HEAD_KEY_IF_NONE_MATCH, eTag);
            long lastModified = HttpHeaders.getLastModified(headers.get(HttpHeaders.HEAD_KEY_LAST_MODIFIED));
            if (lastModified > 0)
                headers(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE, HttpHeaders.formatMillisToGMT(lastModified));
        }

        // 2. 添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);

        // 3. 添加 UserAgent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
        return cacheEntity;
    }

    /**
     * 根据请求结果生成对应的缓存实体类
     *
     * @param responseHeaders 返回数据中的响应头
     * @param data            解析出来的数据
     * @param forceCache      是否强制缓存
     * @return 缓存的实体类
     */
    public <T> CacheEntity<T> parseCacheHeaders(Headers responseHeaders, T data, boolean forceCache) {
        long date = HttpHeaders.getDate(responseHeaders.get(HttpHeaders.HEAD_KEY_DATE));
        long expires = HttpHeaders.getExpiration(responseHeaders.get(HttpHeaders.HEAD_KEY_EXPIRES));
        String cacheControl = HttpHeaders.getCacheControl(responseHeaders.get(HttpHeaders.HEAD_KEY_CACHE_CONTROL), responseHeaders.get(HttpHeaders.HEAD_KEY_PRAGMA));

        long maxAge = 0;
        long staleWhileRevalidate = 0;
        boolean mustRevalidate = false;

        if (!TextUtils.isEmpty(cacheControl)) {
            if ((cacheControl.equals("no-cache") || cacheControl.equals("no-store")) && !forceCache) {
                //不缓存，返回空
                return null;
            } else if (cacheControl.startsWith("max-age=")) {
                try {
                    maxAge = Long.parseLong(cacheControl.substring(8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (cacheControl.startsWith("stale-while-revalidate=")) {
                try {
                    staleWhileRevalidate = Long.parseLong(cacheControl.substring(23));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (cacheControl.equals("must-revalidate") || cacheControl.equals("proxy-revalidate")) {
                mustRevalidate = true;
            }
        }

        CacheEntity<T> cacheEntity = new CacheEntity<>();
        long localExpire = 0;   // 缓存相对于本地的到期时间
        long now = System.currentTimeMillis();
        // If must-revalidate, 当缓存过期时, 强制从服务器验证
        // Http1.1
        if (!TextUtils.isEmpty(cacheControl)) {
            localExpire = now + maxAge * 1000;
            if (mustRevalidate) localExpire += staleWhileRevalidate * 1000;
        }
        // Http1.0
        else if (date > 0 && expires >= date) {
            localExpire = now + (expires - date);
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        for (String headerName : responseHeaders.names()) {
            httpHeaders.put(headerName, responseHeaders.get(headerName));
        }

        cacheEntity.setKey(cacheKey);
        cacheEntity.setData(data);
        cacheEntity.setLocalExpire(localExpire);
        cacheEntity.setHeaders(httpHeaders);
        return cacheEntity;
    }

    /** 失败回调，发送到主线程 */
    public <T> void sendFailResultCallback(final boolean isFromCache, final Call call,//
                                           final Response response, final Exception e, final AbsCallback<T> callback) {

        //不同的缓存模式，可能会导致该失败进入两次，一次缓存失败，一次网络请求失败
        if (!isFromCache && cacheMode == CacheMode.REQUEST_FAILED_READ_CACHE) {
            CacheEntity<T> cacheEntity = (CacheEntity<T>) cacheManager.get(cacheKey);
            if (cacheEntity != null) {
                T data = cacheEntity.getData();
                sendSuccessResultCallback(true, data, call, response, callback);
                return;
            } else {
                sendFailResultCallback(true, call, response, new IllegalArgumentException("请求网络失败后，无法读取缓存或者缓存不存在！"), callback);
            }
        }

        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                callback.onError(isFromCache, call, response, e);         //请求失败回调 （UI线程）
                callback.onAfter(isFromCache, null, call, response, e);   //请求结束回调 （UI线程）
            }
        });
    }

    /** 成功回调，发送到主线程 */
    public <T> void sendSuccessResultCallback(final boolean isFromCache, final T t, //
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