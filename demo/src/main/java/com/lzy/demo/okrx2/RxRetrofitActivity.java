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
package com.lzy.demo.okrx2;

import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseRxDetailActivity;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx.adapter.ObservableResponse;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RxRetrofitActivity extends BaseRxDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_rx_retrofit);
        ButterKnife.bind(this);
        setTitle("统一管理请求");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        unSubscribe();
    }

    @OnClick(R.id.retrofitRequest)
    public void retrofitRequest(View view) {
        Subscription subscription = ServerApi.getString("aaa", "bbb")//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())  //
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(String response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.jsonRequest)
    public void jsonRequest(View view) {
        Type type = new TypeToken<LzyResponse<ServerModel>>() {}.getType();
        Subscription subscription = ServerApi.<LzyResponse<ServerModel>>getData(type, Urls.URL_JSONOBJECT, "aaa", "bbb")//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .map(new Func1<LzyResponse<ServerModel>, ServerModel>() {
                    @Override
                    public ServerModel call(LzyResponse<ServerModel> response) {
                        return response.data;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())  //
                .subscribe(new Subscriber<ServerModel>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(ServerModel response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.jsonArrayRequest)
    public void jsonArrayRequest(View view) {
        Type type = new TypeToken<LzyResponse<List<ServerModel>>>() {}.getType();
        Subscription subscription = ServerApi.<LzyResponse<List<ServerModel>>>getData(type, Urls.URL_JSONARRAY, "aaa", "bbb")//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .map(new Func1<LzyResponse<List<ServerModel>>, List<ServerModel>>() {
                    @Override
                    public List<ServerModel> call(LzyResponse<List<ServerModel>> response) {
                        return response.data;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<List<ServerModel>>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(List<ServerModel> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }
}
