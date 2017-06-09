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

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SyncActivity extends BaseDetailActivity {

    private Handler handler = new InnerHandler();

    private class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String data = (String) msg.obj;
            System.out.println("同步请求的数据：" + data);
            Toast.makeText(getApplicationContext(), "同步请求成功" + data, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sync);
        ButterKnife.bind(this);
        setTitle("同步请求");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.sync)
    public void sync(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    //同步会阻塞主线程，必须开线程
//                    Response response = OkGo.get(Urls.URL_JSONOBJECT)//
//                            .tag(this)//
//                            .headers("header1", "headerValue1")//
//                            .params("param1", "paramValue1")//
//                            .execute();  //不传callback即为同步请求

                    Call<String> call = OkGo.<String>get(Urls.URL_JSONOBJECT)//
                            .tag(this)//
                            .headers("header1", "headerValue1")//
                            .params("param1", "paramValue1")//
                            .converter(new StringConvert())//
                            .adapt();
                    Response<String> response = call.execute();

                    Message message = Message.obtain();
                    message.obj = response.body();
                    handler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
