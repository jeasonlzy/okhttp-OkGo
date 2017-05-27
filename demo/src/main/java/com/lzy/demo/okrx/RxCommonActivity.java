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
package com.lzy.demo.okrx;

import android.os.Bundle;
import android.view.View;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseRxDetailActivity;
import com.lzy.demo.callback.JsonConvert;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Response;
import com.lzy.okrx.adapter.ObservableHttp;

import org.json.JSONObject;

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
public class RxCommonActivity extends BaseRxDetailActivity {

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_rx_common);
        ButterKnife.bind(this);
        setTitle("OkRx基本请求");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        unSubscribe();
    }

    @OnClick(R.id.commonRequest)
    public void commonRequest(View view) {
        Subscription subscription = OkGo.<String>post(Urls.URL_METHOD)//
                .headers("aaa", "111")//
                .params("bbb", "222")//
                .converter(new StringConvert())//
                .adapt(new ObservableHttp<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(Response<String> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.retrofitRequest)
    public void retrofitRequest(View view) {
        Subscription subscription = ServerApi.getServerModel("aaa", "bbb")//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .map(new Func1<Response<LzyResponse<ServerModel>>, Response<ServerModel>>() {
                    @Override
                    public Response<ServerModel> call(Response<LzyResponse<ServerModel>> response) {
                        Response<ServerModel> httpResponse = new Response<>();
                        httpResponse.setException(response.getException());
                        httpResponse.setFromCache(response.isFromCache());
                        httpResponse.setRawResponse(response.getRawResponse());
                        httpResponse.setRawCall(response.getRawCall());
                        httpResponse.setBody(response.body().data);
                        return httpResponse;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())  //
                .subscribe(new Subscriber<Response<ServerModel>>() {
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
                    public void onNext(Response<ServerModel> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.jsonRequest)
    public void jsonRequest(View view) {
        Subscription subscription = OkGo.<LzyResponse<ServerModel>>post(Urls.URL_JSONOBJECT)//
                .headers("aaa", "111")//
                .params("bbb", "222")//
                .converter(new JsonConvert<LzyResponse<ServerModel>>())//
                .adapt(new ObservableHttp<LzyResponse<ServerModel>>())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .map(new Func1<Response<LzyResponse<ServerModel>>, Response<ServerModel>>() {
                    @Override
                    public Response<ServerModel> call(Response<LzyResponse<ServerModel>> response) {
                        Response<ServerModel> httpResponse = new Response<>();
                        httpResponse.setException(response.getException());
                        httpResponse.setFromCache(response.isFromCache());
                        httpResponse.setRawResponse(response.getRawResponse());
                        httpResponse.setRawCall(response.getRawCall());
                        httpResponse.setBody(response.body().data);
                        return httpResponse;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<Response<ServerModel>>() {
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
                    public void onNext(Response<ServerModel> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.jsonArrayRequest)
    public void jsonArrayRequest(View view) {
        Subscription subscription = ServerApi.getServerListModel("aaa", "bbb")//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .map(new Func1<Response<LzyResponse<List<ServerModel>>>, Response<List<ServerModel>>>() {
                    @Override
                    public Response<List<ServerModel>> call(Response<LzyResponse<List<ServerModel>>> response) {
                        Response<List<ServerModel>> httpResponse = new Response<>();
                        httpResponse.setException(response.getException());
                        httpResponse.setFromCache(response.isFromCache());
                        httpResponse.setRawResponse(response.getRawResponse());
                        httpResponse.setRawCall(response.getRawCall());
                        httpResponse.setBody(response.body().data);
                        return httpResponse;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<Response<List<ServerModel>>>() {
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
                    public void onNext(Response<List<ServerModel>> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.upString)
    public void upString(View view) {
        Subscription subscription = OkGo.<String>post(Urls.URL_TEXT_UPLOAD)//
                .headers("bbb", "222")//
                .upString("上传的文本。。。")//
                .converter(new StringConvert())//
                .adapt(new ObservableHttp<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(Response<String> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.upJson)
    public void upJson(View view) {
        HashMap<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "这里是需要提交的json格式数据");
        params.put("key3", "也可以使用三方工具将对象转成json字符串");
        params.put("key4", "其实你怎么高兴怎么写都行");
        JSONObject jsonObject = new JSONObject(params);

        Subscription subscription = OkGo.<String>post(Urls.URL_TEXT_UPLOAD)//
                .headers("bbb", "222")//
                .upJson(jsonObject.toString())//
                .converter(new StringConvert())//
                .adapt(new ObservableHttp<String>())//
                .subscribeOn(Schedulers.io())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Subscriber<Response<String>>() {
                    @Override
                    public void onCompleted() {
                        dismissLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        showToast("请求失败");
                        handleError(null);
                    }

                    @Override
                    public void onNext(Response<String> response) {
                        handleResponse(response);
                    }
                });
        addSubscribe(subscription);
    }
}
