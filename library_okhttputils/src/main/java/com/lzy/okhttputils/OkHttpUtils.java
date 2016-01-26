package com.lzy.okhttputils;

import android.os.Handler;
import android.os.Looper;

import com.lzy.okhttputils.cookie.SimpleCookieJar;
import com.lzy.okhttputils.https.HttpsUtils;
import com.lzy.okhttputils.model.RequestHeaders;
import com.lzy.okhttputils.model.RequestParams;
import com.lzy.okhttputils.request.DeleteRequest;
import com.lzy.okhttputils.request.GetRequest;
import com.lzy.okhttputils.request.HeadRequest;
import com.lzy.okhttputils.request.PatchRequest;
import com.lzy.okhttputils.request.PostRequest;
import com.lzy.okhttputils.request.PutRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okio.Buffer;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：网络请求的入口类
 * 修订历史：
 * 本框架基于 张鸿洋 https://github.com/hongyangAndroid/okhttp-utils
 * 和  https://github.com/pengjianbo/OkHttpFinal 两个框架修改而成，
 * 目前Get，Post的请求已经完成，支持大文件上传下载，上传进度回调，下载进度回调，
 * 表单上传（多文件和多参数一起上传），链式调用，整合Gson，自动解析返回对象，
 * 支持Https和自签名证书，支持cookie自动管理，
 * 。。。。。。。。。。
 * 后期将要实现的功能，统一的上传管理和下载管理
 * ================================================
 */
public class OkHttpUtils {
    public static final int DEFAULT_MILLISECONDS = 5000; //默认的超时时间
    private static OkHttpUtils mInstance;                //单例
    private Handler mDelivery;                           //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;    //ok请求的客户端
    private RequestParams mCommonParams;                 //全局公共请求参数
    private RequestHeaders mCommonHeader;                //全局公共请求头
    private HostnameVerifier hostnameVerifier;

    private OkHttpUtils() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        //允许cookie的自动化管理
        okHttpClientBuilder.cookieJar(new SimpleCookieJar());
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        if (hostnameVerifier == null)
            okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        return okHttpClientBuilder.build();
    }

    /** get请求 */
    public static GetRequest get(String url) {
        return new GetRequest(url);
    }

    /** post请求 */
    public static PostRequest post(String url) {
        return new PostRequest(url);
    }

    /** put请求 */
    public static PutRequest put(String url) {
        return new PutRequest(url);
    }

    /** head请求 */
    public static HeadRequest head(String url) {
        return new HeadRequest(url);
    }

    /** delete请求 */
    public static DeleteRequest delete(String url) {
        return new DeleteRequest(url);
    }

    /** patch请求 */
    public static PatchRequest patch(String url) {
        return new PatchRequest(url);
    }

    /** 调试模式 */
    public static void debug(boolean debug, String tag) {
        L.debug = debug;
        L.tag = tag;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    private class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /** https的全局访问规则 */
    public OkHttpUtils setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /** https的全局自签名证书 */
    public OkHttpUtils setCertificates(InputStream... certificates) {
        okHttpClientBuilder.sslSocketFactory(HttpsUtils.getSslSocketFactory(certificates, null, null));
        return this;
    }

    /** https的全局自签名证书 */
    public OkHttpUtils setCertificates(String... certificates) {
        for (String certificate : certificates) {
            InputStream inputStream = new Buffer().writeUtf8(certificate).inputStream();
            setCertificates(inputStream);
        }
        return this;
    }

    /** 全局读取超时时间 */
    public OkHttpUtils setReadTimeOut(int readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局写入超时时间 */
    public OkHttpUtils setWriteTimeOut(int writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局连接超时时间 */
    public OkHttpUtils setConnectTimeout(int connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局公共请求参数 */
    public RequestParams getCommonParams() {
        return mCommonParams;
    }

    public void addCommonParams(RequestParams commonParams) {
        if (mCommonParams == null) mCommonParams = new RequestParams();
        mCommonParams.put(mCommonParams);
    }

    /** 全局公共请求头 */
    public RequestHeaders getCommonHeader() {
        return mCommonHeader;
    }

    public void addCommonHeader(RequestHeaders commonHeader) {
        if (mCommonHeader == null) mCommonHeader = new RequestHeaders();
        mCommonHeader.put(commonHeader);
    }

    /** 根据Tag取消请求 */
    public void cancelTag(Object tag) {
        for (Call call : getOkHttpClient().dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : getOkHttpClient().dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}

