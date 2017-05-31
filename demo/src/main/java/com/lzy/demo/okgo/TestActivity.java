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

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.callback.JsonCallback;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.HttpRequest;

import org.json.JSONObject;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

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
        OkGo.<JSONObject>get(Urls.URL_JSONOBJECT)
                .execute(new JsonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(Response<JSONObject> response) {

                    }
                });
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
        OkGo.<String>get("https://www.qunar.com/")//
                .tag(this)//
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        System.out.println(response.body());
                    }
                });
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {

        OkGo.<File>get("http://www.apk3.com/uploads/soft/20160511/wshdyq.hit_1.15_25.apk")//
                .tag(this)//
                .execute(new FileCallback() {
                    @Override
                    public void onSuccess(Response<File> response) {
                        System.out.println("onSuccess");
                    }

                    @Override
                    public void onStart(HttpRequest<File, ? extends HttpRequest> request) {
                        System.out.println("onStart");
                    }

                    @Override
                    public void onError(Response<File> response) {
                        response.getException().printStackTrace();
                    }

                    @Override
                    public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
                        System.out.println(currentSize + " " + totalSize + " " + progress + " " + networkSpeed);
                    }
                });
    }
}
