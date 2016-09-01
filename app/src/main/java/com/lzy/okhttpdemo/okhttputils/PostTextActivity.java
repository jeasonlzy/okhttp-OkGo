package com.lzy.okhttpdemo.okhttputils;

import android.os.Bundle;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseDetailActivity;
import com.lzy.okhttpdemo.callback.DialogCallback;
import com.lzy.okhttpdemo.model.ServerModel;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class PostTextActivity extends BaseDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_post_text);
        ButterKnife.bind(this);
        setTitle("自动解析Json对象");
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
                .upJson(jsonObject.toString())//
                .execute(new DialogCallback<ServerModel>(this, ServerModel.class) {
                    @Override
                    public void onSuccess(ServerModel serverModel, Call call, Response response) {
                        handleResponse(serverModel, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }

                });
    }

    @OnClick(R.id.postString)
    public void postString(View view) {
        OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .upString("这是要上传的长文本数据！")//
                .execute(new DialogCallback<ServerModel>(this, ServerModel.class) {
                    @Override
                    public void onSuccess(ServerModel serverModel, Call call, Response response) {
                        handleResponse(serverModel, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }

                });
    }

    @OnClick(R.id.postBytes)
    public void postBytes(View view) {
        OkHttpUtils.post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .upBytes("这是字节数据".getBytes())//
                .execute(new DialogCallback<ServerModel>(this, ServerModel.class) {
                    @Override
                    public void onSuccess(ServerModel serverModel, Call call, Response response) {
                        handleResponse(serverModel, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }

                });
    }
}