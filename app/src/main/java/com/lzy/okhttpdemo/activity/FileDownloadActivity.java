package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.os.Environment;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogFileCallBack;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.File;

import butterknife.ButterKnife;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownloadActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(5)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    private void downloadFile() {
        OkHttpUtils.get(Urls.DownloadFile)//
                .tag(this)//
                .params("ppppppp", "ppp")//
                .headers("hhhhhhh", "hhh")//
                .execute(new DialogFileCallBack(Environment.getExternalStorageDirectory() + "/video", "bbb.avi") {
                    @Override
                    public void onResponse(boolean isFromCache, File file, Request request, Response response) {
                        System.out.println("isFromCache:" + isFromCache + "  onResponse:" + file);
                    }
                });
    }
}
