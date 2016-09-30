package com.lzy.demo.okgo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.callback.JsonConvert;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.convert.BitmapConvert;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class TestActivity extends BaseActivity {

    @Bind(R.id.image) ImageView imageView;
    @Bind(R.id.edit) EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setTitle("测试页面");
    }

    @OnClick(R.id.btn1)
    public void btn1(View view) {
        Call<String> stringCall = OkGo.get("").getCall(StringConvert.create());
        Call<Bitmap> bitmapCall = OkGo.get("").getCall(BitmapConvert.create());
        Call<File> fileCall = OkGo.get("").getCall(new FileConvert());
        Call<LzyResponse<ServerModel>> call = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {});
        Call<LzyResponse<ServerModel>> listCall = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {});

        Observable<String> stringObservable = OkGo.get("").getCall(StringConvert.create(), RxAdapter.<String>create());
        Observable<LzyResponse<ServerModel>> observable = OkGo.get("").getCall(new JsonConvert<LzyResponse<ServerModel>>() {}, RxAdapter.<LzyResponse<ServerModel>>create());
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
        OkGo.post(Urls.URL_METHOD)//
                .headers("aaa", "111")//
                .params("bbb", "222")//
                .getCall(new JsonConvert<LzyResponse<ServerModel>>() {}, RxAdapter.<LzyResponse<ServerModel>>create())//
                .map(new Func1<LzyResponse<ServerModel>, ServerModel>() {
                    @Override
                    public ServerModel call(LzyResponse<ServerModel> response) {
                        return response.data;
                    }
                })//
                .doOnNext(new Action1<ServerModel>() {
                    @Override
                    public void call(ServerModel serverModel) {
                        System.out.println("------" + serverModel);
                    }
                })//
                .subscribe(new Action1<ServerModel>() {
                    @Override
                    public void call(ServerModel serverModel) {
                        System.out.println("======成功");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        System.out.println("======失败");
                    }
                });
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
    }
}
