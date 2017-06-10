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

import java.lang.reflect.Type;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
        dispose();
    }

    @OnClick(R.id.retrofitRequest)
    public void retrofitRequest(View view) {
        ServerApi.getString("aaa", "bbb")//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())  //
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        handleResponse(s);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onComplete() {
                        dismissLoading();
                    }
                });
    }

    @OnClick(R.id.jsonRequest)
    public void jsonRequest(View view) {
        Type type = new TypeToken<LzyResponse<ServerModel>>() {}.getType();
        ServerApi.<LzyResponse<ServerModel>>getData(type, Urls.URL_JSONOBJECT, "aaa", "bbb")//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        showLoading();
                    }
                })//
                .map(new Function<LzyResponse<ServerModel>, ServerModel>() {
                    @Override
                    public ServerModel apply(@NonNull LzyResponse<ServerModel> response) throws Exception {
                        return response.data;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())  //
                .subscribe(new Observer<ServerModel>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull ServerModel response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onComplete() {
                        dismissLoading();
                    }
                });
    }

    @OnClick(R.id.jsonArrayRequest)
    public void jsonArrayRequest(View view) {
        Type type = new TypeToken<LzyResponse<List<ServerModel>>>() {}.getType();
        ServerApi.<LzyResponse<List<ServerModel>>>getData(type, Urls.URL_JSONARRAY, "aaa", "bbb")//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        showLoading();
                    }
                })//
                .map(new Function<LzyResponse<List<ServerModel>>, List<ServerModel>>() {
                    @Override
                    public List<ServerModel> apply(@NonNull LzyResponse<List<ServerModel>> response) throws Exception {
                        return response.data;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<List<ServerModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull List<ServerModel> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();            //请求失败
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onComplete() {
                        dismissLoading();
                    }
                });
    }
}
