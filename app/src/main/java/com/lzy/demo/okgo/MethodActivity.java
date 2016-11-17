package com.lzy.demo.okgo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.DialogCallback;
import com.lzy.demo.callback.StringDialogCallback;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.ColorUtils;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MethodActivity extends BaseDetailActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.gridView) GridView gridView;

    private String[] methods = {"GET", "HEAD\n只有请求头", "OPTIONS\n获取服务器支持的HTTP请求方式",//
                                "POST", "PUT\n用法同POST主要用于创建资源", "DELETE\n与PUT对应主要用于删除资源"};

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_method);
        ButterKnife.bind(this);

        setTitle("请求方法演示");
        gridView.setAdapter(new MyAdapter());
        gridView.setOnItemClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                OkGo.get(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
                                handleResponse(responseData.data, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
            case 1:
                OkGo.head(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new StringDialogCallback(this) {
                            @Override
                            public void onSuccess(String s, Call call, Response response) {
                                handleResponse(s, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
            case 2:
                OkGo.options(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
                                handleResponse(responseData.data, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
            case 3:
                OkGo.post(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .params("param2", "paramValue2")//
                        .params("param3", "paramValue3")//
                        .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
                                handleResponse(responseData.data, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
            case 4:
                OkGo.put(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
                                handleResponse(responseData.data, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
            case 5:
                OkGo.delete(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .requestBody(RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), "这是要上传的数据"))//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(LzyResponse<ServerModel> responseData, Call call, Response response) {
                                handleResponse(responseData.data, call, response);
                            }

                            @Override
                            public void onError(Call call, Response response, Exception e) {
                                super.onError(call, response, e);
                                handleError(call, response);
                            }
                        });
                break;
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
            textView.setBackgroundColor(ColorUtils.randomColor());
            return textView;
        }
    }
}