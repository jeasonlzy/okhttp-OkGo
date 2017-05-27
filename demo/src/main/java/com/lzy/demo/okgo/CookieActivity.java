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
import com.lzy.okgo.cookie.store.CookieStore;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CookieActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cookie);
        ButterKnife.bind(this);
        setTitle("cookie管理与session保持");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.getCookie)
    public void getCookie(View view) {
        //一般手动取出cookie的目的只是交给 webview 等等，非必要情况不要自己操作
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        HttpUrl httpUrl = HttpUrl.parse(Urls.URL_METHOD);
        List<Cookie> cookies = cookieStore.getCookie(httpUrl);
        showToast(httpUrl.host() + "对应的cookie如下：" + cookies.toString());
    }

    @OnClick(R.id.getAllCookie)
    public void getAllCookie(View view) {
        //一般手动取出cookie的目的只是交给 webview 等等，非必要情况不要自己操作
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        List<Cookie> allCookie = cookieStore.getAllCookie();
        showToast("所有cookie如下：" + allCookie.toString());
    }

    @OnClick(R.id.addCookie)
    public void addCookie(View view) {

        HttpUrl httpUrl = HttpUrl.parse(Urls.URL_METHOD);
        Cookie.Builder builder = new Cookie.Builder();
        Cookie cookie = builder.name("myCookieKey1").value("myCookieValue1").domain(httpUrl.host()).build();
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        cookieStore.saveCookie(httpUrl, cookie);

        showToast("详细添加cookie的代码，请看demo的代码");

        OkGo.<String>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(Response<String> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(Response<String> response) {
                        handleError(response);
                    }
                });
    }

    @OnClick(R.id.removeCookie)
    public void removeCookie(View view) {
        HttpUrl httpUrl = HttpUrl.parse(Urls.URL_METHOD);
        CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
        cookieStore.removeCookie(httpUrl);

        showToast("详细移除cookie的代码，请看demo的代码");
    }

    @OnClick(R.id.updateCookie)
    public void updateCookie(View view) {
        showToast("暂时未实现，可以先移除再添加，效果一样");
    }
}
