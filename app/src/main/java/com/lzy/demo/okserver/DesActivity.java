package com.lzy.demo.okserver;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.model.ApkModel;
import com.lzy.demo.utils.ApkUtils;
import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;
import com.lzy.okserver.listener.DownloadListener;
import com.lzy.okgo.request.GetRequest;

import java.io.File;

import butterknife.Bind;

public class DesActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.icon) ImageView icon;
    @Bind(R.id.name) TextView name;
    @Bind(R.id.downloadSize) TextView downloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView netSpeed;
    @Bind(R.id.pbProgress) ProgressBar pbProgress;
    @Bind(R.id.start) Button download;
    @Bind(R.id.remove) Button remove;
    @Bind(R.id.restart) Button restart;

    private MyListener listener;
    private DownloadInfo downloadInfo;
    private ApkModel apk;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_details);
        initToolBar(toolbar, true, "下载管理");

        apk = (ApkModel) getIntent().getSerializableExtra("apk");
        downloadManager = DownloadService.getDownloadManager();

        Glide.with(this).load(apk.getIconUrl()).error(R.mipmap.ic_launcher).into(icon);
        name.setText(apk.getName());
        download.setOnClickListener(this);
        remove.setOnClickListener(this);
        restart.setOnClickListener(this);
        listener = new MyListener();

        downloadInfo = downloadManager.getDownloadInfo(apk.getUrl());
        if (downloadInfo != null) {
            //如果任务存在，把任务的监听换成当前页面需要的监听
            downloadInfo.setListener(listener);
            //需要第一次手动刷一次，因为任务可能处于下载完成，暂停，等待状态，此时是不会回调进度方法的
            refreshUi(downloadInfo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (downloadInfo != null) refreshUi(downloadInfo);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (downloadInfo != null) downloadInfo.removeListener();
    }

    @Override
    public void onClick(View v) {
        //每次点击的时候，需要更新当前对象
        downloadInfo = downloadManager.getDownloadInfo(apk.getUrl());
        if (v.getId() == download.getId()) {
            if (downloadInfo == null) {
                GetRequest request = OkGo.get(apk.getUrl())//
                        .headers("headerKey1", "headerValue1")//
                        .headers("headerKey2", "headerValue2")//
                        .params("paramKey1", "paramValue1")//
                        .params("paramKey2", "paramValue2");
                downloadManager.addTask(apk.getUrl(), request, listener);
                return;
            }
            switch (downloadInfo.getState()) {
                case DownloadManager.PAUSE:
                case DownloadManager.NONE:
                case DownloadManager.ERROR:
                    downloadManager.addTask(downloadInfo.getUrl(), downloadInfo.getRequest(), listener);
                    break;
                case DownloadManager.DOWNLOADING:
                    downloadManager.pauseTask(downloadInfo.getUrl());
                    break;
                case DownloadManager.FINISH:
                    if (ApkUtils.isAvailable(this, new File(downloadInfo.getTargetPath()))) {
                        ApkUtils.uninstall(this, ApkUtils.getPackageName(this, downloadInfo.getTargetPath()));
                    } else {
                        ApkUtils.install(this, new File(downloadInfo.getTargetPath()));
                    }
                    break;
            }
        } else if (v.getId() == remove.getId()) {
            if (downloadInfo == null) {
                Toast.makeText(this, "请先下载任务", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadManager.removeTask(downloadInfo.getUrl());
            downloadSize.setText("--M/--M");
            netSpeed.setText("---/s");
            tvProgress.setText("--.--%");
            pbProgress.setProgress(0);
            download.setText("下载");
        } else if (v.getId() == restart.getId()) {
            if (downloadInfo == null) {
                Toast.makeText(this, "请先下载任务", Toast.LENGTH_SHORT).show();
                return;
            }
            downloadManager.restartTask(downloadInfo.getUrl());
        }
    }

    private class MyListener extends DownloadListener {

        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            refreshUi(downloadInfo);
        }

        @Override
        public void onFinish(DownloadInfo downloadInfo) {
            System.out.println("onFinish");
            Toast.makeText(DesActivity.this, "下载完成:" + downloadInfo.getTargetPath(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(DownloadInfo downloadInfo, String errorMsg, Exception e) {
            System.out.println("onError");
            if (errorMsg != null) Toast.makeText(DesActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    private void refreshUi(DownloadInfo downloadInfo) {
        String downloadLength = Formatter.formatFileSize(DesActivity.this, downloadInfo.getDownloadLength());
        String totalLength = Formatter.formatFileSize(DesActivity.this, downloadInfo.getTotalLength());
        downloadSize.setText(downloadLength + "/" + totalLength);
        String networkSpeed = Formatter.formatFileSize(DesActivity.this, downloadInfo.getNetworkSpeed());
        netSpeed.setText(networkSpeed + "/s");
        tvProgress.setText((Math.round(downloadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
        pbProgress.setMax((int) downloadInfo.getTotalLength());
        pbProgress.setProgress((int) downloadInfo.getDownloadLength());
        switch (downloadInfo.getState()) {
            case DownloadManager.NONE:
                download.setText("下载");
                break;
            case DownloadManager.DOWNLOADING:
                download.setText("暂停");
                break;
            case DownloadManager.PAUSE:
                download.setText("继续");
                break;
            case DownloadManager.WAITING:
                download.setText("等待");
                break;
            case DownloadManager.ERROR:
                download.setText("出错");
                break;
            case DownloadManager.FINISH:
                if (ApkUtils.isAvailable(DesActivity.this, new File(downloadInfo.getTargetPath()))) {
                    download.setText("卸载");
                } else {
                    download.setText("安装");
                }
                break;
        }
    }
}
