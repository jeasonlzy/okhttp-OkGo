package com.lzy.okgo.callback;

import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.convert.Converter;
import com.lzy.okgo.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作   者：廖子尧
 * 版   本：1.0
 * 创建日期：2016/1/14
 * 描   述：抽象的回调接口
 * 修订历史：
 * ================================================
 * <p>该类的回调具有如下顺序,虽然顺序写的很复杂,但是理解后,是很简单,并且合情合理的
 * <p>1.无缓存模式{@link CacheMode#NO_CACHE}<br>
 * ---网络请求成功 onBefore -> convertSuccess -> onSuccess -> onAfter<br>
 * ---网络请求失败 onBefore -> parseError -> onError -> onAfter<br>
 * <p>2.默认缓存模式,遵循304头{@link CacheMode#DEFAULT}<br>
 * ---网络请求成功,服务端返回非304 onBefore -> convertSuccess -> onSuccess -> onAfter<br>
 * ---网络请求成功服务端返回304 onBefore -> onCacheSuccess -> onAfter<br>
 * ---网络请求失败 onBefore -> parseError -> onError -> onAfter<br>
 * <p>3.请求网络失败后读取缓存{@link CacheMode#REQUEST_FAILED_READ_CACHE}<br>
 * ---网络请求成功,不读取缓存 onBefore -> convertSuccess -> onSuccess -> onAfter<br>
 * ---网络请求失败,读取缓存成功 onBefore -> parseError -> onError -> onCacheSuccess -> onAfter<br>
 * ---网络请求失败,读取缓存失败 onBefore -> parseError -> onError -> onCacheError -> onAfter<br>
 * <p>4.如果缓存不存在才请求网络，否则使用缓存{@link CacheMode#IF_NONE_CACHE_REQUEST}<br>
 * ---已经有缓存,不请求网络 onBefore -> onCacheSuccess -> onAfter<br>
 * ---没有缓存请求网络成功 onBefore -> onCacheError -> convertSuccess -> onSuccess -> onAfter<br>
 * ---没有缓存请求网络失败 onBefore -> onCacheError -> parseError -> onError -> onAfter<br>
 * <p>5.先使用缓存，不管是否存在，仍然请求网络{@link CacheMode#FIRST_CACHE_THEN_REQUEST}<br>
 * ---无缓存时,网络请求成功 onBefore -> onCacheError -> convertSuccess -> onSuccess -> onAfter<br>
 * ---无缓存时,网络请求失败 onBefore -> onCacheError -> parseError -> onError -> onAfter<br>
 * ---有缓存时,网络请求成功 onBefore -> onCacheSuccess -> convertSuccess -> onSuccess -> onAfter<br>
 * ---有缓存时,网络请求失败 onBefore -> onCacheSuccess -> parseError -> onError -> onAfter<br>
 */
public abstract class AbsCallback<T> implements Converter<T> {

    /** 请求网络开始前，UI线程 */
    public void onBefore(BaseRequest request) {
    }

    /** 对返回数据进行操作的回调， UI线程 */
    public abstract void onSuccess(T t, Call call, Response response);

    /** 缓存成功的回调,UI线程 */
    public void onCacheSuccess(T t, Call call) {
    }

    /** 请求失败，响应错误，数据解析错误等，都会回调该方法， UI线程 */
    public void onError(Call call, Response response, Exception e) {
    }

    /** 缓存失败的回调,UI线程 */
    public void onCacheError(Call call, Exception e) {
    }

    /** 网络失败结束之前的回调 */
    public void parseError(Call call, Exception e) {
    }

    /** 请求网络结束后，UI线程 */
    public void onAfter(T t, Exception e) {
        if (e != null) e.printStackTrace();
    }

    /**
     * Post执行上传过程中的进度回调，get请求不回调，UI线程
     *
     * @param currentSize  当前上传的字节数
     * @param totalSize    总共需要上传的字节数
     * @param progress     当前上传的进度
     * @param networkSpeed 当前上传的速度 字节/秒
     */
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }

    /**
     * 执行下载过程中的进度回调，UI线程
     *
     * @param currentSize  当前下载的字节数
     * @param totalSize    总共需要下载的字节数
     * @param progress     当前下载的进度
     * @param networkSpeed 当前下载的速度 字节/秒
     */
    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
    }
}