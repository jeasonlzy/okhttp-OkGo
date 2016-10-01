package com.lzy.demo.okrx;

import android.graphics.Bitmap;

import com.lzy.demo.callback.JsonConvert;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.BitmapConvert;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import java.io.File;
import java.util.List;

import rx.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/30
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ServerApi {

    public static Observable<String> getString(String header, String param) {
        return OkGo.post(Urls.URL_METHOD)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .getCall(StringConvert.create(), RxAdapter.<String>create());
    }

    public static Observable<LzyResponse<ServerModel>> getServerModel(String header, String param) {
        return OkGo.post(Urls.URL_JSONOBJECT)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .getCall(new JsonConvert<LzyResponse<ServerModel>>() {}, RxAdapter.<LzyResponse<ServerModel>>create());
    }

    public static Observable<LzyResponse<List<ServerModel>>> getServerListModel(String header, String param) {
        return OkGo.post(Urls.URL_JSONARRAY)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .getCall(new JsonConvert<LzyResponse<List<ServerModel>>>() {}, RxAdapter.<LzyResponse<List<ServerModel>>>create());
    }

    public static Observable<Bitmap> getBitmap(String header, String param) {
        return OkGo.post(Urls.URL_IMAGE)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .getCall(BitmapConvert.create(), RxAdapter.<Bitmap>create());
    }

    public static Observable<File> getFile(String header, String param) {
        return OkGo.post(Urls.URL_DOWNLOAD)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .getCall(new FileConvert(), RxAdapter.<File>create());
    }
}
