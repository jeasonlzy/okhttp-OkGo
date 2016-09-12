package com.lzy.okhttpdemo.okhttpgo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseActivity;
import com.lzy.okhttpdemo.callback.JsonCallback;
import com.lzy.okhttpdemo.callback.JsonConvert;
import com.lzy.okhttpdemo.model.ServerModel;
import com.lzy.okhttpgo.OkHttpGo;
import com.lzy.okhttpgo.callback.AbsCallback;
import com.lzy.okhttpgo.callback.BitmapCallback;
import com.lzy.okhttpgo.convert.BitmapConvert;
import com.lzy.okhttpgo.rx.Call;
import com.lzy.okhttpgo.rx.Response;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
            Call<ServerModel> call = OkHttpGo.get("").getCall(new JsonConvert<>(ServerModel.class));

            Call<Object> call1 = OkHttpGo.get("").getCall(new JsonConvert<>(new TypeToken<List<ServerModel>>() {}.getType()));

        } catch (Exception e) {
            e.printStackTrace();
        }

        Call<ServerModel> call = OkHttpGo.get("")//
                .tag(this)//
                .params("", "")//
                .createAdapter(ServerModel.class);
        call.enqueue(new CacheCallback() {

        });
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
    }
}
