package com.lzy.okgo.cache.policy;

import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.HttpResponse;

import okhttp3.Call;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/25
 * 描    述：
 * 修订历史：
 * ================================================
 */
public interface CachePolicy<T> {

    /**
     * 获取数据成功的回调
     *
     * @param success 获取的数据，可是是缓存或者网络
     */
    void onSuccess(HttpResponse<T> success);

    /**
     * 获取数据失败的回调
     *
     * @param error 失败的信息，可是是缓存或者网络
     */
    void onError(HttpResponse<T> error);

    /**
     * 控制是否执行后续的回调动作
     *
     * @param call     请求的对象
     * @param response 响应的对象
     * @return true，不执行回调， false 执行回调
     */
    boolean onAnalysisResponse(okhttp3.Call call, okhttp3.Response response);

    /**
     * 构建缓存
     *
     * @return 获取的缓存
     */
    CacheEntity<T> prepareCache();

    /**
     * 构建请求对象
     *
     * @return 准备请求的对象
     */
    okhttp3.Call prepareRawCall();

    /**
     * 同步请求获取数据
     *
     * @param cacheEntity 本地的缓存
     * @param rawCall     请求的对象
     * @return 从缓存或本地获取的数据
     */
    HttpResponse<T> requestSync(CacheEntity<T> cacheEntity, okhttp3.Call rawCall);

    /**
     * 异步请求获取数据
     *
     * @param cacheEntity 本地的缓存
     * @param rawCall     请求的对象
     * @param callback    异步回调
     */
    void requestAsync(CacheEntity<T> cacheEntity, Call rawCall, Callback<T> callback);

    /**
     * 当前请求是否已经执行
     *
     * @return true，已经执行， false，没有执行
     */
    boolean isExecuted();

    /**
     * 取消请求
     */
    void cancel();

    /**
     * 是否已经取消
     *
     * @return true，已经取消，false，没有取消
     */
    boolean isCanceled();
}
