package com.lzy.okhttpdemo.okhttpgo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseActivity;
import com.lzy.okhttpdemo.callback.JsonConvert;
import com.lzy.okhttpdemo.model.ServerModel;
import com.lzy.okhttpgo.OkHttpGo;
import com.lzy.okhttpgo.adapter.Call;
import com.lzy.okhttpgo.convert.BitmapConvert;
import com.lzy.okhttpgo.convert.FileConvert;
import com.lzy.okhttpgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import java.io.File;
import java.util.List;

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
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn1)
    public void btn1(View view) {

        try {

            Call<String> stringCall = OkHttpGo.get("").getCall(StringConvert.create());
            Call<Bitmap> bitmapCall = OkHttpGo.get("").getCall(BitmapConvert.create());
            Call<File> fileCall = OkHttpGo.get("").getCall(new FileConvert());
            Call<ServerModel> serverModelCall = OkHttpGo.get("").getCall(JsonConvert.<ServerModel>create());
            Call<List<ServerModel>> listCall = OkHttpGo.get("").getCall(JsonConvert.<List<ServerModel>>create());

            Observable<ServerModel> observable = OkHttpGo.get("").getCall(JsonConvert.<ServerModel>create(), RxAdapter.<ServerModel>create());

            OkHttpGo.post("")//
                    .headers("aaa", "bbb")//
                    .params("aaa", "bbb")//
                    .params("aaa", "bbb")//
                    .params("aaa", new File("sdf"))//
                    .getCall(JsonConvert.<ServerModel>create(), RxAdapter.<ServerModel>create())//
                    .map(new Func1<ServerModel, ServerModel>() {
                        @Override
                        public ServerModel call(ServerModel serverModel) {
                            return null;
                        }
                    })//
                    .doOnNext(new Action1<ServerModel>() {
                        @Override
                        public void call(ServerModel serverModel) {

                        }
                    })//
                    .subscribe(new Action1<ServerModel>() {
                        @Override
                        public void call(ServerModel serverModel) {

                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
    }
}
