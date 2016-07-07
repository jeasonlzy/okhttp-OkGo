package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn1)
    public void btn1(View view) {
//        OkHttpUtils.post("http://mail.luichi.info:8885/appserver/account/login")//
//                .tag(this)//
//                .params("loginName", "15805187431")//
//                .params("password", "E10ADC3949BA59ABBE56E057F20F883E")//
//                .execute(new StringCallback() {
//                    @Override
//                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
//                    }
//                });
        OkHttpUtils.post("http://touch.qunar.com")//
                .tag(this)//
                .addCookie("aaa", "111")//
                .addCookie("bbb", "222")//
                .addCookie("ccc", "333")//
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                    }
                });
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
//        OkHttpUtils.post("http://mail.luichi.info:8885/appserver/order/getOrderList")//
//                .tag(this)//
//                .params("pageSize", "10")//
//                .params("start", "0")//
//                .params("orderStatus", "")//
//                .params("orderType", "0")//
//                .execute(new StringCallback() {
//                    @Override
//                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
//                    }
//                });
        OkHttpUtils.post("http://touch.qunar.com/bbb")//
                .tag(this)//
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                    }
                });
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
        ArrayList<String> urls = new ArrayList<>();
        urls.add("http://touch.qunar.com");
        urls.add("http://touch.qunar.com");
        urls.add("http://touch.qunar.com");
        urls.add("http://touch.qunar.com");
        urls.add("http://touch.qunar.com");
        urls.add("http://touch.qunar.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        urls.add("http://m.weibo.com");
        for (int i = 0; i < urls.size(); i++) {
            OkHttpUtils.get(urls.get(i))//
                    .tag(this)//
                    .addCookie("aaa", "111")//
                    .addCookie("bbb", "222")//
                    .addCookie("ccc", "333")//
                    .execute(new StringCallback() {
                        @Override
                        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                            System.out.println(request.url());
                        }
                    });
        }
    }
}
