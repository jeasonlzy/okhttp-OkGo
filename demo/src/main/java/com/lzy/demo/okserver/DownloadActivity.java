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
package com.lzy.demo.okserver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.base.BaseRecyclerAdapter;
import com.lzy.demo.base.DividerItemDecoration;
import com.lzy.demo.model.ApkModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.download.DownloadService;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DownloadActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.targetFolder) TextView targetFolder;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.openManager) Button openManager;

    private ArrayList<ApkModel> apks;
    private DownloadManager downloadManager;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initToolBar(toolbar, true, "下载管理");

        initData();
        downloadManager = DownloadService.getDownloadManager();
        downloadManager.setTargetFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa/");

        targetFolder.setText("下载路径: " + downloadManager.getTargetFolder());
        sbCorePoolSize.setMax(5);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                downloadManager.getThreadPool().setCorePoolSize(progress);
                tvCorePoolSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbCorePoolSize.setProgress(3);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new MainAdapter(this);
        recyclerView.setAdapter(adapter);
        openManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DownloadManagerActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private class MainAdapter extends BaseRecyclerAdapter<ApkModel, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, apks);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_download_details, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ApkModel apkModel = mDatas.get(position);
            holder.bind(apkModel);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.name) TextView name;
        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.download) Button download;

        private ApkModel apkModel;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ApkModel apkModel) {
            this.apkModel = apkModel;
            if (downloadManager.getDownloadInfo(apkModel.getUrl()) != null) {
                download.setText("已在队列");
                download.setEnabled(false);
            } else {
                download.setText("下载");
                download.setEnabled(true);
            }
            name.setText(apkModel.getName());
            Glide.with(getApplicationContext()).load(apkModel.getIconUrl()).error(R.mipmap.ic_launcher).into(icon);
            download.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.download) {
                if (downloadManager.getDownloadInfo(apkModel.getUrl()) != null) {
                    Toast.makeText(getApplicationContext(), "任务已经在下载列表中", Toast.LENGTH_SHORT).show();
                } else {
                    GetRequest request = OkGo.get(apkModel.getUrl())//
                            .headers("headerKey1", "headerValue1")//
                            .headers("headerKey2", "headerValue2")//
                            .params("paramKey1", "paramValue1")//
                            .params("paramKey2", "paramValue2");
                    downloadManager.addTask(apkModel.getUrl(), apkModel, request, null);
                    download.setText("已在队列");
                    download.setEnabled(false);
                }
            } else {
                Intent intent = new Intent(getApplicationContext(), DesActivity.class);
                intent.putExtra("apk", apkModel);
                startActivity(intent);
            }
        }
    }

    private void initData() {
        apks = new ArrayList<>();
        ApkModel apkInfo1 = new ApkModel();
        apkInfo1.setName("爱奇艺");
        apkInfo1.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0c10c4c0155c9adf1282af008ed329378d54112ac");
        apkInfo1.setUrl("http://121.29.10.1/f5.market.mi-img.com/download/AppStore/0b8b552a1df0a8bc417a5afae3a26b2fb1342a909/com.qiyi.video.apk");
        apks.add(apkInfo1);
        ApkModel apkInfo2 = new ApkModel();
        apkInfo2.setName("微信");
        apkInfo2.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/00814b5dad9b54cc804466369c8cb18f23e23823f");
        apkInfo2.setUrl("http://116.117.158.129/f2.market.xiaomi.com/download/AppStore/04275951df2d94fee0a8210a3b51ae624cc34483a/com.tencent.mm.apk");
        apks.add(apkInfo2);
        ApkModel apkInfo3 = new ApkModel();
        apkInfo3.setName("新浪微博");
        apkInfo3.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/01db44d7f809430661da4fff4d42e703007430f38");
        apkInfo3.setUrl("http://60.28.125.129/f1.market.xiaomi.com/download/AppStore/0ff41344f280f40c83a1bbf7f14279fb6542ebd2a/com.sina.weibo.apk");
        apks.add(apkInfo3);
        ApkModel apkInfo4 = new ApkModel();
        apkInfo4.setName("QQ");
        apkInfo4.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/072725ca573700292b92e636ec126f51ba4429a50");
        apkInfo4.setUrl("http://121.29.10.1/f3.market.xiaomi.com/download/AppStore/0ff0604fd770f481927d1edfad35675a3568ba656/com.tencent.mobileqq.apk");
        apks.add(apkInfo4);
        ApkModel apkInfo5 = new ApkModel();
        apkInfo5.setName("陌陌");
        apkInfo5.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/06006948e655c4dd11862d060bd055b4fd2b5c41b");
        apkInfo5.setUrl("http://121.18.239.1/f4.market.xiaomi.com/download/AppStore/096f34dec955dbde0597f4e701d1406000d432064/com.immomo.momo.apk");
        apks.add(apkInfo5);
        ApkModel apkInfo6 = new ApkModel();
        apkInfo6.setName("手机淘宝");
        apkInfo6.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/017a859792d09d7394108e0a618411675ec43f220");
        apkInfo6.setUrl("http://121.29.10.1/f3.market.xiaomi.com/download/AppStore/0afc00452eb1a4dc42b20c9351eacacab4692a953/com.taobao.taobao.apk");
        apks.add(apkInfo6);
        ApkModel apkInfo7 = new ApkModel();
        apkInfo7.setName("酷狗音乐");
        apkInfo7.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0f2f050e21e42f75c7ecca55d01ac4e5e4e40ca8d");
        apkInfo7.setUrl("http://121.18.239.1/f5.market.xiaomi.com/download/AppStore/053ed49c1545c6eec3e3e23b31568c731f940934f/com.kugou.android.apk");
        apks.add(apkInfo7);
        ApkModel apkInfo8 = new ApkModel();
        apkInfo8.setName("网易云音乐");
        apkInfo8.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/02374548ac39f3b7cdbf5bea4b0535b5d1f432f23");
        apkInfo8.setUrl("http://121.18.239.1/f4.market.xiaomi.com/download/AppStore/0f458c5661acb492e30b808a2e3e4c8672e6b55e2/com.netease.cloudmusic.apk");
        apks.add(apkInfo8);
        ApkModel apkInfo9 = new ApkModel();
        apkInfo9.setName("ofo共享单车");
        apkInfo9.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0fe1a5c6092f3d9fa5c4c1e3158e6ff33f6418152");
        apkInfo9.setUrl("http://60.28.125.1/f4.market.mi-img.com/download/AppStore/06954949fcd48414c16f726620cf2d52200550f56/so.ofo.labofo.apk");
        apks.add(apkInfo9);
        ApkModel apkInfo10 = new ApkModel();
        apkInfo10.setName("摩拜单车");
        apkInfo10.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0863a058a811148a5174d9784b7be2f1114191f83");
        apkInfo10.setUrl("http://60.28.125.1/f4.market.xiaomi.com/download/AppStore/00cdeb4865c5a4a7d350fe30b9f812908a569cc8a/com.mobike.mobikeapp.apk");
        apks.add(apkInfo10);
    }
}
