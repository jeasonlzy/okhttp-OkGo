package com.lzy.okhttputils.request;

import android.support.annotation.NonNull;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.https.HttpsUtils;
import com.lzy.okhttputils.model.RequestHeaders;
import com.lzy.okhttputils.model.RequestParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
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
    protected InputStream[] certificates;
    protected RequestParams params = new RequestParams();
    protected RequestHeaders headers = new RequestHeaders();
    private AbsCallback mCallback;

    public BaseRequest(String url) {
        this.url = url;
        //添加公共请求参数
        if (OkHttpUtils.getInstance().getCommonParams() != null) {
            params.put(OkHttpUtils.getInstance().getCommonParams());
        }
        if (OkHttpUtils.getInstance().getCommonHeader() != null) {
            headers.put(OkHttpUtils.getInstance().getCommonHeader());
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

    public R setCertificates(@NonNull InputStream... certificates) {
        this.certificates = certificates;
        return (R) this;
    }

    public R headers(RequestHeaders headers) {
        this.headers.put(headers);
        return (R) this;
    }

    public R headers(String key, String value) {
        headers.put(key, value);
        return (R) this;
    }

    public R params(RequestParams params) {
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
            if (writeTimeOut > 0)
                newClientBuilder.writeTimeout(writeTimeOut, TimeUnit.MILLISECONDS);
            if (connectTimeout > 0)
                newClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
            if (certificates != null)
                newClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
            return newClientBuilder.build().newCall(request);
        }
    }

    /** 阻塞方法，同步请求执行 */
    public Response execute() throws IOException {
        RequestBody requestBody = generateRequestBody();
        final Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);
        return call.execute();
    }

    /** 非阻塞方法，异步请求，但是回调在子线程中执行 */
    public <T> void execute(AbsCallback<T> callback) {
        mCallback = callback;
        if (mCallback == null) mCallback = AbsCallback.CALLBACK_DEFAULT;

        mCallback.onBefore(this);      //请求执行前调用 （UI线程）
        RequestBody requestBody = generateRequestBody();
        Request request = generateRequest(wrapRequestBody(requestBody));
        Call call = generateCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //请求失败，一般为url地址错误，网络错误等
                sendFailResultCallback(request, null, e, mCallback);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //响应失败，一般为服务器内部错误，或者找不到页面等
                if (response.code() >= 400 && response.code() <= 599) {
                    sendFailResultCallback(response.request(), response, null, mCallback);
                    return;
                }

                try {
                    //解析过程中抛出异常，一般为 json 格式错误，或者数据解析异常
                    T t = (T) mCallback.parseNetworkResponse(response);
                    sendSuccessResultCallback(t, response.request(), response, mCallback);
                } catch (Exception e) {
                    sendFailResultCallback(response.request(), response, e, mCallback);
                }
            }
        });
    }

    /** 失败回调，发送到主线程 */
    public <T> void sendFailResultCallback(final Request request, final Response response, final Exception e, final AbsCallback<T> callback) {
        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                callback.onError(request, response, e);         //请求失败回调 （UI线程）
                callback.onAfter(null, request, response, e);   //请求结束回调 （UI线程）
            }
        });
    }

    /** 成功回调，发送到主线程 */
    public <T> void sendSuccessResultCallback(final T t, final Request request, final Response response, final AbsCallback<T> callback) {
        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(t);                         //请求成功回调 （UI线程）
                callback.onAfter(t, request, response, null);   //请求结束回调 （UI线程）
            }
        });
    }
}