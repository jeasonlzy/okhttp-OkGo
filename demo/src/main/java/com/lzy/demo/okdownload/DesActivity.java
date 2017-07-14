/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.demo.okdownload;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.model.ApkModel;
import com.lzy.demo.utils.ApkUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;
import java.text.NumberFormat;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DesActivity extends BaseActivity {

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

    private NumberFormat numberFormat;
    private DownloadTask task;
    private ApkModel apk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_details);
        initToolBar(toolbar, true, "下载管理");

        apk = (ApkModel) getIntent().getSerializableExtra("apk");
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        // 写法一：从内存中获取
        if (OkDownload.getInstance().hasTask(apk.url)) {
            task = OkDownload.getInstance().getTask(apk.url)//
                    .register(new DesListener("DesListener"))//
                    .register(new LogDownloadListener());
        }

        //写法二：从数据库中恢复
//        Progress progress = DownloadManager.getInstance().get(apk.getUrl());
//        if (progress != null) {
//            task = OkDownload.restore(progress)//
//                    .register(new DesListener("DesListener"))//
//                    .register(new LogDownloadListener());
//        }

        displayImage(apk.iconUrl, icon);
        name.setText(apk.name);
        if (task != null) refreshUi(task.progress);
    }

    private void refreshUi(Progress progress) {
        String currentSize = Formatter.formatFileSize(this, progress.currentSize);
        String totalSize = Formatter.formatFileSize(this, progress.totalSize);
        downloadSize.setText(currentSize + "/" + totalSize);
        String speed = Formatter.formatFileSize(this, progress.speed);
        netSpeed.setText(String.format("%s/s", speed));
        tvProgress.setText(numberFormat.format(progress.fraction));
        pbProgress.setMax(10000);
        pbProgress.setProgress((int) (progress.fraction * 10000));
        switch (progress.status) {
            case Progress.NONE:
                download.setText("下载");
                break;
            case Progress.LOADING:
                download.setText("暂停");
                break;
            case Progress.PAUSE:
                download.setText("继续");
                break;
            case Progress.WAITING:
                download.setText("等待");
                break;
            case Progress.ERROR:
                download.setText("出错");
                break;
            case Progress.FINISH:
                if (ApkUtils.isAvailable(this, new File(progress.filePath))) {
                    download.setText("卸载");
                } else {
                    download.setText("安装");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (task != null) {
            task.unRegister("DesListener");
        }
    }

    @OnClick(R.id.start)
    public void start() {
        if (task == null) {

            //这里只是演示，表示请求可以传参，怎么传都行，和okgo使用方法一样
            GetRequest<File> request = OkGo.<File>get(apk.url)//
                    .headers("aaa", "111")//
                    .params("bbb", "222");

            task = OkDownload.request(apk.url, request)//
                    .priority(apk.priority)//
                    .extra1(apk)//
                    .save()//
                    .register(new DesListener("DesListener"))//
                    .register(new LogDownloadListener());
        }
        switch (task.progress.status) {
            case Progress.PAUSE:
            case Progress.NONE:
            case Progress.ERROR:
                task.start();
                break;
            case Progress.LOADING:
                task.pause();
                break;
            case Progress.FINISH:
                File file = new File(task.progress.filePath);
                if (ApkUtils.isAvailable(this, file)) {
                    ApkUtils.uninstall(this, ApkUtils.getPackageName(this, file.getAbsolutePath()));
                } else {
                    ApkUtils.install(this, file);
                }
                break;
        }
    }

    @OnClick(R.id.remove)
    public void remove() {
        if (task != null) {
            task.remove();
            task = null;
        }
        downloadSize.setText("--M/--M");
        netSpeed.setText("---/s");
        tvProgress.setText("--.--%");
        pbProgress.setProgress(0);
        download.setText("下载");
    }

    @OnClick(R.id.restart)
    public void restart() {
        if (task != null) task.restart();
    }

    private class DesListener extends DownloadListener {

        DesListener(String tag) {
            super(tag);
        }

        @Override
        public void onStart(Progress progress) {
        }

        @Override
        public void onProgress(Progress progress) {
            refreshUi(progress);
        }

        @Override
        public void onFinish(File file, Progress progress) {
        }

        @Override
        public void onRemove(Progress progress) {
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
        }
    }
}
