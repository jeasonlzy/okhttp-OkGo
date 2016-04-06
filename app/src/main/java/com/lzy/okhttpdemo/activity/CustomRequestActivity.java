package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.lzy.okhttpdemo.Bean.BaseBean;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallBack;
import com.lzy.okhttpdemo.fragment.OkhttpFragment;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class CustomRequestActivity extends BaseActivity {

    @Bind(R.id.requestState) TextView requestState;
    @Bind(R.id.requestHeaders) TextView requestHeaders;
    @Bind(R.id.responseData) TextView responseData;
    @Bind(R.id.responseHeader) TextView responseHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_request);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(1)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);

    }

    @OnClick(R.id.requestJson)
    public void requestJson(View view) {
        OkHttpUtils.get(Urls.URL_NOHTTP_JSONOBJECT)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new DialogCallBack<BaseBean>(this) {
                    @Override
                    public void onResponse(boolean isFromCache, BaseBean bean, Request request, Response response) {
                        handleJsonResponse(isFromCache, bean, request, response);
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        handleError(isFromCache, call, response);
                    }
                });
    }

    @OnClick(R.id.requestJsonArray)
    public void requestJsonArray(View view) {
        OkHttpUtils.get(Urls.URL_NOHTTP_JSONARRAY)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new DialogCallBack<List<BaseBean>>(this) {
                    @Override
                    public void onResponse(boolean isFromCache, List<BaseBean> beans, Request request, Response response) {
                        handleJsonArrayResponse(isFromCache, beans, request, response);
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        handleError(isFromCache, call, response);
                    }
                });
    }

    private void handleJsonResponse(boolean isFromCache, BaseBean bean, Request request, Response response) {
        requestState.setText("请求成功  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

        Headers requestHeadersString = request.headers();
        Set<String> requestNames = requestHeadersString.names();
        StringBuilder sb = new StringBuilder();
        for (String name : requestNames) {
            sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
        }
        requestHeaders.setText(sb.toString());

        responseData.setText(bean.toString());

        Headers responseHeadersString = response.headers();
        Set<String> names = responseHeadersString.names();
        sb = new StringBuilder();
        for (String name : names) {
            sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
        }
        responseHeader.setText(sb.toString());
    }

    private void handleJsonArrayResponse(boolean isFromCache, List<BaseBean> beans, Request request, Response response) {
        requestState.setText("请求成功  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

        Headers requestHeadersString = request.headers();
        Set<String> requestNames = requestHeadersString.names();
        StringBuilder sb = new StringBuilder();
        for (String name : requestNames) {
            sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
        }
        requestHeaders.setText(sb.toString());

        sb = new StringBuilder();
        for (BaseBean bean : beans) {
            sb.append(bean.toString()).append("\n");
        }
        responseData.setText(sb.toString());

        Headers responseHeadersString = response.headers();
        Set<String> names = responseHeadersString.names();
        sb = new StringBuilder();
        for (String name : names) {
            sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
        }
        responseHeader.setText(sb.toString());
    }

    private void handleError(boolean isFromCache, Call call, @Nullable Response response) {
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
