/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.lzy.okgo.model.HttpResponse;
import com.lzy.okrx.adapter.ObservableHttp;

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

    public static Observable<HttpResponse<String>> getString(String header, String param) {
        return OkGo.<String>post(Urls.URL_METHOD)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .converter(new StringConvert())//
                .adapt(new ObservableHttp<String>());
    }

    public static Observable<HttpResponse<LzyResponse<ServerModel>>> getServerModel(String header, String param) {
        return OkGo.<LzyResponse<ServerModel>>post(Urls.URL_JSONOBJECT)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .converter(new JsonConvert<LzyResponse<ServerModel>>())//
                .adapt(new ObservableHttp<LzyResponse<ServerModel>>());
    }

    public static Observable<HttpResponse<LzyResponse<List<ServerModel>>>> getServerListModel(String header, String param) {
        return OkGo.<LzyResponse<List<ServerModel>>>post(Urls.URL_JSONARRAY)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .converter(new JsonConvert<LzyResponse<List<ServerModel>>>())//
                .adapt(new ObservableHttp<LzyResponse<List<ServerModel>>>());
    }

    public static Observable<HttpResponse<Bitmap>> getBitmap(String header, String param) {
        return OkGo.<Bitmap>post(Urls.URL_IMAGE)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .converter(new BitmapConvert())//
                .adapt(new ObservableHttp<Bitmap>());
    }

    public static Observable<HttpResponse<File>> getFile(String header, String param) {
        return OkGo.<File>post(Urls.URL_DOWNLOAD)//
                .headers("aaa", header)//
                .params("bbb", param)//
                .converter(new FileConvert())//
                .adapt(new ObservableHttp<File>());
    }
}
