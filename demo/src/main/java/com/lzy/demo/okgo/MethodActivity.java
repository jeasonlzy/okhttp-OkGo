/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import com.lzy.okgo.model.Response;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MethodActivity extends BaseDetailActivity implements AdapterView.OnItemClickListener {

    @Bind(R.id.gridView) GridView gridView;

    private String[] methods = {"GET", "HEAD", "OPTIONS", "POST", "PUT", "DELETE", "PATCH", "TRACE"};

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
                OkGo.<LzyResponse<ServerModel>>get(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 1:
                OkGo.<String>head(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new StringDialogCallback(this) {
                            @Override
                            public void onSuccess(Response<String> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<String> response) {
                                handleError(response);
                            }
                        });
                break;
            case 2:
                OkGo.<LzyResponse<ServerModel>>options(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 3:
                OkGo.<LzyResponse<ServerModel>>post(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .params("param2", "paramValue2")//
                        .params("param3", "paramValue3")//
                        .isMultipart(true)         //强制使用 multipart/form-data 表单上传（只是演示，不需要的话不要设置。默认就是false）
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 4:
                OkGo.<LzyResponse<ServerModel>>put(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 5:
                OkGo.<LzyResponse<ServerModel>>delete(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .upString("这是要上传的数据")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 6:
                OkGo.<LzyResponse<ServerModel>>patch(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .upString("这是要上传的数据")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
                            }
                        });
                break;
            case 7:
                OkGo.<LzyResponse<ServerModel>>trace(Urls.URL_METHOD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .params("param1", "paramValue1")//
                        .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                            @Override
                            public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                                handleResponse(response);
                            }

                            @Override
                            public void onError(Response<LzyResponse<ServerModel>> response) {
                                handleError(response);
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
