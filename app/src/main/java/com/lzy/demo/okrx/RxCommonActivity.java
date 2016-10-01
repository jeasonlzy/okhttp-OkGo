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
import com.lzy.okrx.RxAdapter;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

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
        Subscription subscription = OkGo.post(Urls.URL_METHOD)//
                .headers("aaa", "111")//
                .params("bbb", "222")//
                .getCall(StringConvert.create(), RxAdapter.<String>create())//以上为产生请求事件,请求默认发生在IO线程
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();  //开始请求前显示对话框
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        dismissLoading();               //请求成功,关闭对话框
                        handleResponse(s, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        dismissLoading();       //请求失败
                        showToast("请求失败");
                        handleError(null, null);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.retrofitRequest)
    public void retrofitRequest(View view) {
        Subscription subscription = ServerApi.getServerModel("aaa", "bbb")//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();  //开始请求前显示对话框
                    }
                })//
                .map(new Func1<LzyResponse<ServerModel>, ServerModel>() {
                    @Override
                    public ServerModel call(LzyResponse<ServerModel> response) {
                        return response.data;
                    }
                })  //转换数据结果
                .observeOn(AndroidSchedulers.mainThread())  //切换到主线程
                .subscribe(new Action1<ServerModel>() {
                    @Override
                    public void call(ServerModel serverModel) {
                        dismissLoading();                   //请求成功
                        handleResponse(serverModel, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();            //请求失败
                        showToast("请求失败");
                        dismissLoading();
                        handleError(null, null);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.jsonRequest)
    public void jsonRequest(View view) {
        Subscription subscription = OkGo.post(Urls.URL_JSONOBJECT)//
                .headers("aaa", "111")//
                .params("bbb", "222")//一定要注意这里的写法,JsonConvert最后的大括号千万不能忘记
                .getCall(new JsonConvert<LzyResponse<ServerModel>>() {}, RxAdapter.<LzyResponse<ServerModel>>create())//
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
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Action1<ServerModel>() {
                    @Override
                    public void call(ServerModel serverModel) {
                        dismissLoading();                   //请求成功
                        handleResponse(serverModel, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();            //请求失败
                        showToast("请求失败");
                        dismissLoading();
                        handleError(null, null);
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
                .map(new Func1<LzyResponse<List<ServerModel>>, List<ServerModel>>() {
                    @Override
                    public List<ServerModel> call(LzyResponse<List<ServerModel>> response) {
                        return response.data;
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Action1<List<ServerModel>>() {
                    @Override
                    public void call(List<ServerModel> serverModels) {
                        dismissLoading();                   //请求成功
                        handleResponse(serverModels, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();            //请求失败
                        showToast("请求失败");
                        dismissLoading();
                        handleError(null, null);
                    }
                });
        addSubscribe(subscription);
    }

    @OnClick(R.id.upString)
    public void upString(View view) {
        Subscription subscription = OkGo.post(Urls.URL_TEXT_UPLOAD)//
                .headers("bbb", "222")//
                .upString("上传的文本。。。")//
                .getCall(StringConvert.create(), RxAdapter.<String>create())//以上为产生请求事件,请求默认发生在IO线程
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();  //开始请求前显示对话框
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        dismissLoading();               //请求成功,关闭对话框
                        handleResponse(s, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        dismissLoading();       //请求失败
                        showToast("请求失败");
                        handleError(null, null);
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

        Subscription subscription = OkGo.post(Urls.URL_TEXT_UPLOAD)//
                .headers("bbb", "222")//
                .upJson(jsonObject.toString())//
                .getCall(StringConvert.create(), RxAdapter.<String>create())//以上为产生请求事件,请求默认发生在IO线程
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();  //开始请求前显示对话框
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        dismissLoading();               //请求成功,关闭对话框
                        handleResponse(s, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        dismissLoading();       //请求失败
                        showToast("请求失败");
                        handleError(null, null);
                    }
                });
        addSubscribe(subscription);
    }
}
