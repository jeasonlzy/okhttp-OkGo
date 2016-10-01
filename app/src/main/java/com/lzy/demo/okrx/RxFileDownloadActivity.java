package com.lzy.demo.okrx;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseRxDetailActivity;
import com.lzy.demo.ui.NumberProgressBar;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class RxFileDownloadActivity extends BaseRxDetailActivity {

    @Bind(R.id.fileDownload) Button btnFileDownload;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        setTitle("文件下载");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        unSubscribe();
    }

    @OnClick(R.id.fileDownload)
    public void fileDownload(View view) {
        ServerApi.getFile("aaa", "bbb")//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        btnFileDownload.setText("正在下载中...\n使用Rx方式做进度监听稍显麻烦,推荐使用回调方式");
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Action1<File>() {
                    @Override
                    public void call(File file) {
                        btnFileDownload.setText("下载完成");
                        handleResponse(file, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        btnFileDownload.setText("下载出错");
                        showToast("请求失败");
                        handleError(null, null);
                    }
                });
    }
}