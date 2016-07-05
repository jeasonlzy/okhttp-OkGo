package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lzy.okhttpdemo.Bean.RequestInfo;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallback;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class PostTextActivity extends BaseActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_text);
        ButterKnife.bind(this);
        setTitle(Constant.getData().get(3)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @OnClick(R.id.postJson)
    public void postJson(View view) {

        HashMap<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "这里是需要提交的json格式数据");
        params.put("key3", "也可以使用三方工具将对象转成json字符串");
        params.put("key4", "其实你怎么高兴怎么写都行");
        JSONObject jsonObject = new JSONObject(params);

        OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .postJson(jsonObject.toString())//
                .execute(new TextCallBack<>(this, RequestInfo.class));
    }

    @OnClick(R.id.postString)
    public void postString(View view) {
        OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .postString("这是要上传的长文本数据！")//
                .execute(new TextCallBack<>(this, RequestInfo.class));
    }

    @OnClick(R.id.postBytes)
    public void postBytes(View view) {
        OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .postBytes("这是字节数据".getBytes())//
                .execute(new TextCallBack<>(this, RequestInfo.class));
    }

    private class TextCallBack<T> extends DialogCallback<T> {

        public TextCallBack(Activity activity, Class<T> clazz) {
            super(activity, clazz);
        }

        @Override
        public void onResponse(boolean isFromCache, T data, Request request, Response response) {
            handleResponse(isFromCache, data, request, response);
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
        }
    }
}
