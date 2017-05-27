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

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.StringDialogCallback;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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
public class RedirectActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_redirect);
        ButterKnife.bind(this);
        setTitle("301重定向");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.redirect)
    public void redirect(View view) {
        OkGo.<String>get(Urls.URL_REDIRECT)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        handleResponse(response);
                        responseData.setText("注意看请求头的url和响应头的url是不一样的！\n这代表了在请求过程中发生了重定向，" +//
                                             "okhttp默认将重定向封装在了请求内部，只有最后一次请求的数据会被真正的请求下来触发回调，中间过程" +//
                                             "是默认实现的，不会触发回调！");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        handleError(response);
                    }
                });
    }
}
