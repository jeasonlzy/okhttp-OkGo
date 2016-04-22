package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lzy.okhttpdemo.Bean.RequestInfo;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallback;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheManager;
import com.lzy.okhttputils.cache.CacheMode;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class CacheActivity extends BaseActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cache);
        ButterKnife.bind(this);
        setTitle(Constant.getData().get(6)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @OnClick(R.id.getAll)
    public void getAll(View view) {
        List<CacheEntity<Object>> all = CacheManager.INSTANCE.getAll();
        StringBuilder sb = new StringBuilder();
        sb.append("共" + all.size() + "条缓存：").append("\n\n");
        for (int i = 0; i < all.size(); i++) {
            CacheEntity<Object> cacheEntity = all.get(i);
            sb.append("第" + (i + 1) + "条缓存：").append("\n").append(cacheEntity).append("\n\n");
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("所有缓存显示").setMessage(sb.toString()).show();
    }

    @OnClick(R.id.clear)
    public void clear(View view) {
        boolean clear = CacheManager.INSTANCE.clear();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("清除缓存").setMessage("是否清除成功：" + clear).show();
    }

    @OnClick(R.id.cache_default)
    public void cache_default(View view) {
        OkHttpUtils.get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.DEFAULT)//
                .cacheKey("cache_default")//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.request_failed_read_cache)
    public void request_failed_read_cache(View view) {
        OkHttpUtils.get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)//
                .cacheKey("request_failed_read_cache")//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.if_none_cache_request)
    public void if_none_cache_request(View view) {
        OkHttpUtils.get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.IF_NONE_CACHE_REQUEST)//
                .cacheKey("if_none_cache_request")//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    @OnClick(R.id.first_cache_then_request)
    public void first_cache_then_request(View view) {
        OkHttpUtils.get(Urls.URL_CACHE)//
                .tag(this)//
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)//
                .cacheKey("only_read_cache")//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new CacheCallBack(this));
    }

    private class CacheCallBack extends DialogCallback<RequestInfo> {

        public CacheCallBack(Activity activity) {
            super(activity, RequestInfo.class);
        }

        @Override
        public void onResponse(boolean isFromCache, RequestInfo requestInfo, Request request, Response response) {
            handleResponse(isFromCache, requestInfo, request, response);
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
        }
    }
}
