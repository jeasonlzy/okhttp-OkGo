package com.lzy.okhttpdemo.callback;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.lzy.okhttpdemo.ui.WaitDialog;
import com.lzy.okhttputils.callback.BitmapCallback;
import com.lzy.okhttputils.request.BaseRequest;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成bitmap
 * 修订历史：
 * ================================================
 */
public abstract class DialogBitmapCallBack extends BitmapCallback {

    private WaitDialog dialog;

    public DialogBitmapCallBack(Activity activity) {
        dialog = new WaitDialog(activity);
    }

    @Override
    public void onBefore(BaseRequest request) {
        System.out.println("onBefore");
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public Bitmap parseNetworkResponse(Response response) throws Exception {
        System.out.println("parseNetworkResponse");
        return super.parseNetworkResponse(response);
    }

    @Override
    public void onAfter(boolean isFromCache, @Nullable Bitmap bitmap, Call call, @Nullable Response response, @Nullable Exception e) {
        System.out.println("onAfter");
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
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
        System.out.println("onError");
        super.onError(isFromCache, call, response, e);
    }
}
