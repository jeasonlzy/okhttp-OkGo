package com.lzy.demo.okgo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.callback.JsonConvert;
import com.lzy.demo.model.ServerModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.convert.BitmapConvert;
import com.lzy.okgo.convert.FileConvert;
import com.lzy.okgo.convert.StringConvert;
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

            Call<String> stringCall = OkGo.get("").getCall(StringConvert.create());
            Call<Bitmap> bitmapCall = OkGo.get("").getCall(BitmapConvert.create());
            Call<File> fileCall = OkGo.get("").getCall(new FileConvert());
            Call<ServerModel> serverModelCall = OkGo.get("").getCall(JsonConvert.<ServerModel>create());
            Call<List<ServerModel>> listCall = OkGo.get("").getCall(JsonConvert.<List<ServerModel>>create());

            Observable<ServerModel> observable = OkGo.get("").getCall(JsonConvert.<ServerModel>create(), RxAdapter.<ServerModel>create());

            OkGo.post("")//
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
