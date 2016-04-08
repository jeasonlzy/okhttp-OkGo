package com.lzy.okhttpdemo.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.ui.NumberProgressBar;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.FileCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class FileDownloadActivity extends BaseActivity {

    @Bind(R.id.fileDownload) Button btnFileDownload;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_file_download);
        ButterKnife.bind(this);
        setTitle(Constant.getData().get(5)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @OnClick(R.id.fileDownload)
    public void fileDownload(View view) {
        OkHttpUtils.get(Urls.URL_DOWNLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new DownloadFileCallBack(Environment.getExternalStorageDirectory() + "/temp", "OkHttpUtils.apk"));
    }

    private class DownloadFileCallBack extends FileCallback {

        public DownloadFileCallBack(String destFileDir, String destFileName) {
            super(destFileDir, destFileName);
        }

        @Override
        public void onBefore(BaseRequest request) {
            btnFileDownload.setText("正在下载中");
        }

        @Override
        public void onResponse(boolean isFromCache, File file, Request request, Response response) {
            handleResponse(isFromCache, file, request, response);
            btnFileDownload.setText("下载完成");
        }

        @Override
        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            System.out.println("downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);

            String downloadLength = Formatter.formatFileSize(getApplicationContext(), currentSize);
            String totalLength = Formatter.formatFileSize(getApplicationContext(), totalSize);
            tvDownloadSize.setText(downloadLength + "/" + totalLength);
            String netSpeed = Formatter.formatFileSize(getApplicationContext(), networkSpeed);
            tvNetSpeed.setText(netSpeed + "/S");
            tvProgress.setText((Math.round(progress * 10000) * 1.0f / 100) + "%");
            pbProgress.setMax(100);
            pbProgress.setProgress((int) (progress * 100));
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
            btnFileDownload.setText("下载出错");
        }
    }
}
