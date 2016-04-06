package com.lzy.okhttpdemo.callback;

import android.support.annotation.Nullable;

import com.lzy.okhttputils.callback.FileCallBack;
import com.lzy.okhttputils.request.BaseRequest;

import java.io.File;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/15
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class DialogFileCallBack extends FileCallBack {

    public DialogFileCallBack(String destFileDir, String destFileName) {
        super(destFileDir, destFileName);
    }

    @Override
    public File parseNetworkResponse(Response response) throws Exception {
        System.out.println("parseNetworkResponse");
        return super.parseNetworkResponse(response);
    }

    @Override
    public void onBefore(BaseRequest request) {
        System.out.println("onBefore");
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable File file, Call call, @Nullable Response response, @Nullable Exception e) {
        System.out.println("isFromCache:" + isFromCache + "  onAfter");
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
    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
        System.out.println("isFromCache:" + isFromCache + "  onError");
        super.onError(isFromCache, call, response, e);
    }
}
