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

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.DialogCallback;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.db.CacheManager;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CacheActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cache);
        ButterKnife.bind(this);
        setTitle("网络缓存基本用法");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @SuppressWarnings("unchecked")
    @OnClick(R.id.getAll)
    public void getAll(View view) {
        // 获取所有的缓存，但是一般每个缓存的泛型都不一样，所以缓存的泛型使用 ？
        List<CacheEntity<?>> all = CacheManager.getInstance().getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("共" + all.size() + "条缓存：").append("\n\n");
        for (int i = 0; i < all.size(); i++) {
            CacheEntity<?> cacheEntity = all.get(i);
            sb.append("第" + (i + 1) + "条缓存：").append("\n").append(cacheEntity).append("\n\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("所有缓存显示").setMessage(sb.toString()).show();
    }

    @OnClick(R.id.clear)
    public void clear(View view) {
        boolean clear = CacheManager.getInstance().clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除缓存").setMessage("是否清除成功：" + clear).show();
    }

    @OnClick(R.id.no_cache)
    public void no_cache(View view) {
        OkGo.<LzyResponse<ServerModel>>get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.NO_CACHE)//
                .cacheKey("no_cache")   //对于无缓存模式,该参数无效
                .cacheTime(5000)        //对于无缓存模式,该时间无效
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.cache_default)
    public void cache_default(View view) {
        OkGo.<LzyResponse<ServerModel>>get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.DEFAULT)//
                .cacheKey("cache_default")//
                .cacheTime(5000)//对于默认的缓存模式,该时间无效,依靠的是服务端对304缓存的控制
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.request_failed_read_cache)
    public void request_failed_read_cache(View view) {
        OkGo.<LzyResponse<ServerModel>>get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//
                .cacheKey("request_failed_read_cache")//
                .cacheTime(5000)            // 单位毫秒.5秒后过期
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.if_none_cache_request)
    public void if_none_cache_request(View view) {
        OkGo.<LzyResponse<ServerModel>>get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)//
                .cacheKey("if_none_cache_request")//
                .cacheTime(5000)            // 单位毫秒.5秒后过期
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.first_cache_then_request)
    public void first_cache_then_request(View view) {
        OkGo.<LzyResponse<ServerModel>>get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)//
                .cacheKey("first_cache_then_request")//
                .cacheTime(5000)            // 单位毫秒.5秒后过期
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    private class CacheCallBack extends DialogCallback<LzyResponse<ServerModel>> {

        CacheCallBack(Activity activity) {
            super(activity);
        }

        @Override
        public void onSuccess(Response<LzyResponse<ServerModel>> response) {
            handleResponse(response);
            Call call = response.getRawCall();
            requestState.setText("请求成功  是否来自缓存：false  请求方式：" + call.request().method() + "\n" + "url：" + call.request().url());
        }

        @Override
        public void onCacheSuccess(Response<LzyResponse<ServerModel>> response) {
            handleResponse(response);
            Call call = response.getRawCall();
            requestState.setText("请求成功  是否来自缓存：true  请求方式：" + call.request().method() + "\n" + "url：" + call.request().url());
        }

        @Override
        public void onError(Response<LzyResponse<ServerModel>> response) {
            handleError(response);
            Call call = response.getRawCall();
            requestState.setText("请求失败  是否来自缓存：false  请求方式：" + call.request().method() + "\n" + "url：" + call.request().url());
        }
    }
}
