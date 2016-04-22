package com.lzy.okhttpdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lzy.okhttpdemo.R;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public abstract class BaseActivity extends AppCompatActivity {

    protected ActionBar actionBar;
    protected TextView requestState;
    protected TextView requestHeaders;
    protected TextView responseData;
    protected TextView responseHeader;
    protected FrameLayout rootContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        getDelegate().setContentView(R.layout.activity_base);
        Window window = getWindow();
        requestState = (TextView) window.findViewById(R.id.requestState);
        requestHeaders = (TextView) window.findViewById(R.id.requestHeaders);
        responseData = (TextView) window.findViewById(R.id.responseData);
        responseHeader = (TextView) window.findViewById(R.id.responseHeader);
        rootContent = (FrameLayout) window.findViewById(R.id.content);
        onActivityCreate(savedInstanceState);
    }

    protected abstract void onActivityCreate(Bundle savedInstanceState);

    @Override
    public void setTitle(CharSequence title) {
        if (actionBar != null) actionBar.setTitle(title);
    }

    @Override
    public void setTitle(int titleId) {
        if (actionBar != null) actionBar.setTitle(titleId);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int id) {
        return (T) rootContent.findViewById(id);
    }

    @Override
    public View findViewById(int id) {
        return rootContent.findViewById(id);
    }

    private void clearContentView() {
        rootContent.removeAllViews();
    }

    @Override
    public void setContentView(int layoutResID) {
        clearContentView();
        getLayoutInflater().inflate(layoutResID, rootContent, true);
    }

    @Override
    public void setContentView(View view) {
        clearContentView();
        rootContent.addView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        clearContentView();
        rootContent.addView(view, params);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected <T> void handleResponse(boolean isFromCache, T data, Request request, @Nullable Response response) {
        requestState.setText("请求成功  是否来自缓存：" + isFromCache + "  请求方式：" + request.method() + "\n" +
                "url：" + request.url());

        Headers requestHeadersString = request.headers();
        Set<String> requestNames = requestHeadersString.names();
        StringBuilder sb = new StringBuilder();
        for (String name : requestNames) {
            sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
        }
        requestHeaders.setText(sb.toString());

        if (data == null) {
            responseData.setText("--");
        } else {
            if (data instanceof String) {
                responseData.setText((String) data);
            } else if (data instanceof List) {
                sb = new StringBuilder();
                List list = (List) data;
                for (Object obj : list) {
                    sb.append(obj.toString()).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (data instanceof Set) {
                sb = new StringBuilder();
                Set set = (Set) data;
                for (Object obj : set) {
                    sb.append(obj.toString()).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (data instanceof Map) {
                sb = new StringBuilder();
                Map map = (Map) data;
                Set keySet = map.keySet();
                for (Object key : keySet) {
                    sb.append(key.toString()).append(" ： ").append(map.get(key)).append("\n");
                }
                responseData.setText(sb.toString());
            } else if (data instanceof File) {
                File file = (File) data;
                responseData.setText("数据内容即为文件内容\n下载文件路径：" + file.getAbsolutePath());
            } else if (data instanceof Bitmap) {
                responseData.setText("图片的内容即为数据");
            } else {
                responseData.setText(data.toString());
            }
        }

        if (response != null) {
            Headers responseHeadersString = response.headers();
            Set<String> names = responseHeadersString.names();
            sb = new StringBuilder();
            sb.append("url ： ").append(response.request().url()).append("\n\n");
            sb.append("stateCode ： ").append(response.code()).append("\n");
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
        } else if (isFromCache) {
            sb = new StringBuilder("响应头可以根据 cacheKey 获取到，在此不演示！");
        }
        responseHeader.setText(sb.toString());
    }

    protected void handleError(boolean isFromCache, Call call, @Nullable Response response) {
        Request request = call.request();
        requestState.setText("请求失败  是否来自缓存：" + isFromCache + "  请求方式：" + request.method() + "\n" +
                "url：" + request.url());

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
            sb.append("stateCode ： ").append(response.code()).append("\n");
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
            responseHeader.setText(sb.toString());
        } else {
            responseHeader.setText("--");
        }
    }
}
