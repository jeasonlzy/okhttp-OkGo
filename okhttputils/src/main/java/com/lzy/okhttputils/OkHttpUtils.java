package com.lzy.okhttputils;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;

import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.cookie.CookieJarImpl;
import com.lzy.okhttputils.cookie.store.CookieStore;
import com.lzy.okhttputils.cookie.store.MemoryCookieStore;
import com.lzy.okhttputils.https.HttpsUtils;
import com.lzy.okhttputils.interceptor.LoggerInterceptor;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.request.DeleteRequest;
import com.lzy.okhttputils.request.GetRequest;
import com.lzy.okhttputils.request.HeadRequest;
import com.lzy.okhttputils.request.OptionsRequest;
import com.lzy.okhttputils.request.PostRequest;
import com.lzy.okhttputils.request.PutRequest;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okio.Buffer;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：网络请求的入口类
 * 修订历史：
 * ================================================
 */
public class OkHttpUtils {
    public static final int DEFAULT_MILLISECONDS = 10000; //默认的超时时间
    private static OkHttpUtils mInstance;                 //单例
    private Handler mDelivery;                            //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;     //ok请求的客户端
    private HttpParams mCommonParams;                     //全局公共请求参数
    private HttpHeaders mCommonHeaders;                   //全局公共请求头
    private CacheMode mCacheMode;                         //全局缓存模式
    private static Application context;                   //全局上下文

    private OkHttpUtils() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        //允许cookie的自动化管理，默认内存管理
        okHttpClientBuilder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
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

    /** 必须在全局Application先调用，获取context上下文，否则缓存无法使用 */
    public static void init(Application app) {
        context = app;
    }

    /** 获取全局上下文 */
    public static Context getContext() {
        if (context == null) throw new IllegalStateException("请先在全局Application中调用 OkHttpUtils.init() 初始化！");
        return context;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
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
    public static OptionsRequest options(String url) {
        return new OptionsRequest(url);
    }

    /** 调试模式 */
    public OkHttpUtils debug(String tag) {
        okHttpClientBuilder.addInterceptor(new LoggerInterceptor(tag, true));
        return this;
    }

    /**
     * 此类是用于主机名验证的基接口。 在握手期间，如果 URL 的主机名和服务器的标识主机名不匹配，
     * 则验证机制可以回调此接口的实现程序来确定是否应该允许此连接。策略可以是基于证书的或依赖于其他验证方案。
     * 当验证 URL 主机名使用的默认规则失败时使用这些回调。如果主机名是可接受的，则返回 true
     */
    public class DefaultHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /** https的全局访问规则 */
    public OkHttpUtils setHostnameVerifier(HostnameVerifier hostnameVerifier) {
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

    /** 全局cookie存取规则 */
    public OkHttpUtils setCookieStore(CookieStore cookieStore) {
        okHttpClientBuilder.cookieJar(new CookieJarImpl(cookieStore));
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

    /** 全局的缓存模式 */
    public OkHttpUtils setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /** 获取全局的缓存模式 */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public OkHttpUtils addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public OkHttpUtils addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 添加全局拦截器 */
    public OkHttpUtils addInterceptor(@Nullable Interceptor interceptor) {
        okHttpClientBuilder.addInterceptor(interceptor);
        return this;
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

