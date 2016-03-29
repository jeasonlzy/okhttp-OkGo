package com.lzy.okhttpdemo.callback;

import android.support.annotation.Nullable;

import com.lzy.okhttputils.callback.JsonCallBack;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean,可以是 Bean，String，List，Map
 * 修订历史：
 * ================================================
 */
public abstract class MyJsonCallBack<T> extends JsonCallBack<T> {

    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        System.out.println("parseNetworkResponse");
        return super.parseNetworkResponse(response);
    }

    @Override
    public void onAfter(@Nullable T t, Call call, Response response, @Nullable Exception e) {
        System.out.println("onAfter");
    }

    @Override
    public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
        System.out.println("upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
    }

    @Override
    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
        System.out.println("downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
    }

    @Override
    public void onError(Call call, @Nullable Response response, @Nullable Exception e) {
        System.out.println("onError");
        super.onError(call, response, e);
    }

    @Override
    public void onBefore(BaseRequest request) {
        System.out.println("onBefore");
        request.params("aaa", "111")//
                .params("bbb", "222")//
                .params("ccc", "333")//
                .headers("xxx", "444")//
                .headers("yyy", "555")//
                .headers("zzz", "666");
    }
}
