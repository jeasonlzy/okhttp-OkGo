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
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.BitmapDialogCallback;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.ButterKnife;
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
public class BitmapRequestActivity extends BaseDetailActivity {

    @Bind(R.id.imageView) ImageView imageView;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bitmap_request);
        ButterKnife.bind(this);
        setTitle("请求图片");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.requestImage)
    public void requestJson(View view) {
        OkGo.<Bitmap>get(Urls.URL_IMAGE)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new BitmapDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<Bitmap> response) {
                        handleResponse(response);
                        imageView.setImageBitmap(response.body());
                    }

                    @Override
                    public void onError(Response<Bitmap> response) {
                        handleError(response);
                    }
                });
    }
}
