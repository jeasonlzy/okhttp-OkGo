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
package com.lzy.demo.okgo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.callback.JsonConvert;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.BitmapConvert;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.request.BaseRequest;
import com.lzy.okrx.RxAdapter;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Response;
import rx.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class TestActivity extends BaseActivity {

    @Bind(R.id.image) ImageView imageView;
    @Bind(R.id.edit) EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("测试页面");
    }

    @OnClick(R.id.btn1)
    public void btn1(View view) {
        Call<String> stringCall = OkGo.get("").getCall(StringConvert.create());
        Call<Bitmap> bitmapCall = OkGo.get("").getCall(BitmapConvert.create());
        Call<File> fileCall = OkGo.get("").getCall(new FileConvert());
        Call<LzyResponse<ServerModel>> call = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {});
        Call<LzyResponse<ServerModel>> listCall = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {});

        Observable<String> stringObservable = OkGo.get("").getCall(StringConvert.create(), RxAdapter.<String>create());
        Observable<LzyResponse<ServerModel>> observable = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {}, RxAdapter.<LzyResponse<ServerModel>>create());
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
        OkGo.get(Urls.URL_METHOD)//
                .tag(this)//
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        System.out.println("---" + request.getMethod());
                        System.out.println("---" + request.getMethod());
                    }

                    @Override
                    public void onSuccess(String s, okhttp3.Call call, Response response) {

                    }
                });
    }
}
