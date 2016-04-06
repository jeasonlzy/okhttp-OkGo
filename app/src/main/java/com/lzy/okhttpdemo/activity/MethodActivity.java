package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallBack;
import com.lzy.okhttpdemo.fragment.OkhttpFragment;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.utils.ColorUtil;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class MethodActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.gridView) GridView gridView;
    @Bind(R.id.requestState) TextView requestState;
    @Bind(R.id.requestHeaders) TextView requestHeaders;
    @Bind(R.id.responseData) TextView responseData;
    @Bind(R.id.responseHeader) TextView responseHeader;

    private String[] methods = {"GET", "POST", "PUT", "HEAD", "DELETE", "PATCH"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_method);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(0)[0]);
        gridView.setAdapter(new MyAdapter());
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                OkHttpUtils.get(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new MethodCallBack(this));
                break;
            case 1:
                OkHttpUtils.post(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new MethodCallBack(this));
                break;
            case 2:
                OkHttpUtils.put(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new MethodCallBack(this));
                break;
            case 3:
                OkHttpUtils.head(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new MethodCallBack(this));
                break;
            case 4:
                OkHttpUtils.delete(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .content("aaa这是请求的数据ccc")//
                        .execute(new MethodCallBack(this));
                break;
            case 5:
                OkHttpUtils.patch(Urls.URL_NOHTTP_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new MethodCallBack(this));
                break;
        }
    }

    private class MethodCallBack extends DialogCallBack<String> {

        public MethodCallBack(Activity activity) {
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

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return methods.length;
        }

        @Override
        public String getItem(int position) {
            return methods[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getApplicationContext());
            }
            TextView textView = (TextView) convertView;
            textView.setGravity(Gravity.CENTER);
            textView.setHeight(200);
            textView.setText(getItem(position));
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
            textView.setBackgroundColor(ColorUtil.generateBeautifulColor());
            return textView;
        }
    }
}
