package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallBack;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.PostRequest;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class PostTextActivity extends BaseActivity {

    @Bind(R.id.requestState) TextView requestState;
    @Bind(R.id.requestHeaders) TextView requestHeaders;
    @Bind(R.id.responseData) TextView responseData;
    @Bind(R.id.responseHeader) TextView responseHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_text);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(3)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @OnClick(R.id.postJson)
    public void postJson(View view) {
        OkHttpUtils.post("--------")//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .postJson("{--------------}")//
                .execute(new TextCallBack(this));
    }

    @OnClick(R.id.postString)
    public void postString(View view) {
        OkHttpUtils.post("----------")//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .content("asdfasdfasdfas这是中文这是中文asdfasdfasdf")//
                .mediaType(PostRequest.MEDIA_TYPE_PLAIN)//
                .execute(new TextCallBack(this));
    }

    private class TextCallBack extends DialogCallBack<String> {

        public TextCallBack(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, Response response) {
            requestState.setText("请求成功  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

            Headers requestHeadersString = request.headers();
            Set<String> requestNames = requestHeadersString.names();
            StringBuilder sb = new StringBuilder();
            for (String name : requestNames) {
                sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
            }
            requestHeaders.setText(sb.toString());

            responseData.setText(s);

            Headers responseHeadersString = response.headers();
            Set<String> names = responseHeadersString.names();
            sb = new StringBuilder();
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
            responseHeader.setText(sb.toString());
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            Request request = call.request();
            requestState.setText("请求失败  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

            Headers requestHeadersString = request.headers();
            Set<String> requestNames = requestHeadersString.names();
            StringBuilder sb = new StringBuilder();
            for (String name : requestNames) {
                sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
            }
            requestHeaders.setText(sb.toString());

            responseData.setText("--");
            if (response != null) {
                Headers responseHeadersString = response.headers();
                Set<String> names = responseHeadersString.names();
                sb = new StringBuilder();
                for (String name : names) {
                    sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
                }
                responseHeader.setText(sb.toString());
            } else {
                responseHeader.setText("--");
            }
        }
    }
}
