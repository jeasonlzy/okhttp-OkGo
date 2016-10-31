package com.lzy.okgo;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.CookieStore;
import com.lzy.okgo.https.HttpsUtils;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.DeleteRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.HeadRequest;
import com.lzy.okgo.request.OptionsRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;
import com.lzy.okgo.utils.OkLogger;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：网络请求的入口类
 * 修订历史：
 * ================================================
 */
public class OkGo {
    public static final int DEFAULT_MILLISECONDS = 60000;       //默认的超时时间
    public static final int REFRESH_TIME = 100;                 //回调刷新时间（单位ms）

    private Handler mDelivery;                                  //用于在主线程执行的调度器
    private OkHttpClient.Builder okHttpClientBuilder;           //ok请求的客户端
    private HttpParams mCommonParams;                           //全局公共请求参数
    private HttpHeaders mCommonHeaders;                         //全局公共请求头
    private CacheMode mCacheMode;                               //全局缓存模式
    private int mRetryCount = 3;                                    //全局超时重试次数
    private long mCacheTime = CacheEntity.CACHE_NEVER_EXPIRE;   //全局缓存过期时间,默认永不过期
    private static Application context;                         //全局上下文
    private CookieJarImpl cookieJar;                            //全局 Cookie 实例

    private OkGo() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.hostnameVerifier(new DefaultHostnameVerifier());
        okHttpClientBuilder.connectTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.readTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        okHttpClientBuilder.writeTimeout(DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public static OkGo getInstance() {
        return OkGoHolder.holder;
    }

    private static class OkGoHolder {
        private static OkGo holder = new OkGo();
    }

    /** 必须在全局Application先调用，获取context上下文，否则缓存无法使用 */
    public static void init(Application app) {
        context = app;
    }

    /** 获取全局上下文 */
    public static Context getContext() {
        if (context == null) throw new IllegalStateException("请先在全局Application中调用 OkGo.init() 初始化！");
        return context;
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClientBuilder.build();
    }

    /** 对外暴露 OkHttpClient,方便自定义 */
    public OkHttpClient.Builder getOkHttpClientBuilder() {
        return okHttpClientBuilder;
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

    /** 调试模式,默认打开所有的异常调试 */
    public OkGo debug(String tag) {
        debug(tag, true);
        return this;
    }

    /**
     * 调试模式,第二个参数表示所有catch住的log是否需要打印
     * 一般来说,这些异常是由于不标准的数据格式,或者特殊需要主动产生的,并不是框架错误,如果不想每次打印,这里可以关闭异常显示
     */
    public OkGo debug(String tag, boolean isPrintException) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(tag);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        okHttpClientBuilder.addInterceptor(loggingInterceptor);
        OkLogger.debug(isPrintException);
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
    public OkGo setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        okHttpClientBuilder.hostnameVerifier(hostnameVerifier);
        return this;
    }

    /** https的全局自签名证书 */
    public OkGo setCertificates(InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /** https双向认证证书 */
    public OkGo setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(bksFile, password, certificates);
        okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        return this;
    }

    /** 全局cookie存取规则 */
    public OkGo setCookieStore(CookieStore cookieStore) {
        cookieJar = new CookieJarImpl(cookieStore);
        okHttpClientBuilder.cookieJar(cookieJar);
        return this;
    }

    /** 获取全局的cookie实例 */
    public CookieJarImpl getCookieJar() {
        return cookieJar;
    }

    /** 全局读取超时时间 */
    public OkGo setReadTimeOut(long readTimeOut) {
        okHttpClientBuilder.readTimeout(readTimeOut, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局写入超时时间 */
    public OkGo setWriteTimeOut(long writeTimeout) {
        okHttpClientBuilder.writeTimeout(writeTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 全局连接超时时间 */
    public OkGo setConnectTimeout(long connectTimeout) {
        okHttpClientBuilder.connectTimeout(connectTimeout, TimeUnit.MILLISECONDS);
        return this;
    }

    /** 超时重试次数 */
    public OkGo setRetryCount(int retryCount) {
        if (retryCount < 0) throw new IllegalArgumentException("retryCount must > 0");
        mRetryCount = retryCount;
        return this;
    }

    /** 超时重试次数 */
    public int getRetryCount() {
        return mRetryCount;
    }

    /** 全局的缓存模式 */
    public OkGo setCacheMode(CacheMode cacheMode) {
        mCacheMode = cacheMode;
        return this;
    }

    /** 获取全局的缓存模式 */
    public CacheMode getCacheMode() {
        return mCacheMode;
    }

    /** 全局的缓存过期时间 */
    public OkGo setCacheTime(long cacheTime) {
        if (cacheTime <= -1) cacheTime = CacheEntity.CACHE_NEVER_EXPIRE;
        mCacheTime = cacheTime;
        return this;
    }

    /** 获取全局的缓存过期时间 */
    public long getCacheTime() {
        return mCacheTime;
    }

    /** 获取全局公共请求参数 */
    public HttpParams getCommonParams() {
        return mCommonParams;
    }

    /** 添加全局公共请求参数 */
    public OkGo addCommonParams(HttpParams commonParams) {
        if (mCommonParams == null) mCommonParams = new HttpParams();
        mCommonParams.put(commonParams);
        return this;
    }

    /** 获取全局公共请求头 */
    public HttpHeaders getCommonHeaders() {
        return mCommonHeaders;
    }

    /** 添加全局公共请求参数 */
    public OkGo addCommonHeaders(HttpHeaders commonHeaders) {
        if (mCommonHeaders == null) mCommonHeaders = new HttpHeaders();
        mCommonHeaders.put(commonHeaders);
        return this;
    }

    /** 添加全局拦截器 */
    public OkGo addInterceptor(Interceptor interceptor) {
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