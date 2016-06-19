package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.StringDialogCallback;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import java.io.File;
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
        ArrayList<String> params = new ArrayList<>();
        params.add("111");
        params.add("222");
        params.add("333");
        params.add("444");
        OkHttpUtils.get(Urls.URL_METHOD)//
                .tag(this)//
                .addUrlParams("aaa",params)
                .params("ddd","444")
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                    }
                });
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
        ArrayList<String> params = new ArrayList<>();
        params.add("111");
        params.add("222");
        params.add("333");
        params.add("444");
        OkHttpUtils.post(Urls.URL_METHOD)//
                .tag(this)//
                .addUrlParams("aaa",params)
                .params("bbb","666")
                .params("ccc","777")
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                    }
                });
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
        OkHttpUtils.post("http://dev.11yuehui.com/WebApi/Login/login.html")//
                .tag(this)//
                .params("username", "273029")    //用户名
                .params("password", "273029tc")  //密码
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                    }
                });
    }
}
