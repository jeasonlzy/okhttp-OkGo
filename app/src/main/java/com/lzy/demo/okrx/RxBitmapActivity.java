package com.lzy.demo.okrx;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseRxDetailActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class RxBitmapActivity extends BaseRxDetailActivity {

    @Bind(R.id.imageView) ImageView imageView;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_bitmap_request);
        ButterKnife.bind(this);
        setTitle("请求图片");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        unSubscribe();
    }

    @OnClick(R.id.requestImage)
    public void requestImage(View view) {
        Subscription subscription = ServerApi.getBitmap("aaa", "bbb")//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        showLoading();
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        dismissLoading();                   //请求成功
                        handleResponse(bitmap, null, null);
                        imageView.setImageBitmap(bitmap);
                        System.out.println("---------");
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
}
