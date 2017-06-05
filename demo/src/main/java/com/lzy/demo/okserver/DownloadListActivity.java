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

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.base.BaseRecyclerAdapter;
import com.lzy.demo.base.DividerItemDecoration;
import com.lzy.demo.model.ApkModel;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okserver.OkDownload;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
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
public class DownloadListActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.targetFolder) TextView folder;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.openManager) Button openManager;

    private ArrayList<ApkModel> apks;
    private OkDownload okDownload;
    private MainAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initToolBar(toolbar, true, "下载管理");

        initData();
        okDownload = OkDownload.getInstance();
        okDownload.setFolder(Environment.getExternalStorageDirectory().getAbsolutePath() + "/aaa/");
        okDownload.getThreadPool().setCorePoolSize(3);

        folder.setText("下载路径: " + okDownload.getFolder());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new MainAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.openManager)
    public void openManager(View view) {
        startActivity(new Intent(this, DownloadActivity.class));
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

        private ApkModel apk;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ApkModel apkModel) {
            this.apk = apkModel;
            if (okDownload.getTaskMap().containsKey(apkModel.getUrl())) {
                download.setText("已在队列");
                download.setEnabled(false);
            } else {
                download.setText("下载");
                download.setEnabled(true);
            }
            name.setText(apkModel.getName());
            displayImage(apkModel.getIconUrl(), icon);
            itemView.setOnClickListener(this);
        }

        @OnClick(R.id.download)
        public void download() {
            if (okDownload.getTaskMap().containsKey(apk.getUrl())) {
                showToast("任务已经在下载列表中");
            } else {
                GetRequest<File> request = OkGo.get(apk.getUrl());
                //这里第一个参数是tag，代表下载任务的唯一标识，传任意字符串都行，需要保证唯一
                OkDownload.request(apk.getUrl(), request)//
                        .extra1(apk)//
                        .register(null)//
                        .start();

                download.setText("已在队列");
                download.setEnabled(false);
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), DesActivity.class);
            intent.putExtra("apk", apk);
            startActivity(intent);
        }
    }

    private void initData() {
        apks = new ArrayList<>();
        ApkModel apk1 = new ApkModel();
        apk1.setName("爱奇艺");
        apk1.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0c10c4c0155c9adf1282af008ed329378d54112ac");
        apk1.setUrl("http://121.29.10.1/f5.market.mi-img.com/download/AppStore/0b8b552a1df0a8bc417a5afae3a26b2fb1342a909/com.qiyi.video.apk");
        apks.add(apk1);
        ApkModel apk2 = new ApkModel();
        apk2.setName("微信");
        apk2.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/00814b5dad9b54cc804466369c8cb18f23e23823f");
        apk2.setUrl("http://116.117.158.129/f2.market.xiaomi.com/download/AppStore/04275951df2d94fee0a8210a3b51ae624cc34483a/com.tencent.mm.apk");
        apks.add(apk2);
        ApkModel apk3 = new ApkModel();
        apk3.setName("新浪微博");
        apk3.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/01db44d7f809430661da4fff4d42e703007430f38");
        apk3.setUrl("http://60.28.125.129/f1.market.xiaomi.com/download/AppStore/0ff41344f280f40c83a1bbf7f14279fb6542ebd2a/com.sina.weibo.apk");
        apks.add(apk3);
        ApkModel apk4 = new ApkModel();
        apk4.setName("QQ");
        apk4.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/072725ca573700292b92e636ec126f51ba4429a50");
        apk4.setUrl("http://121.29.10.1/f3.market.xiaomi.com/download/AppStore/0ff0604fd770f481927d1edfad35675a3568ba656/com.tencent.mobileqq.apk");
        apks.add(apk4);
        ApkModel apk5 = new ApkModel();
        apk5.setName("陌陌");
        apk5.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/06006948e655c4dd11862d060bd055b4fd2b5c41b");
        apk5.setUrl("http://121.18.239.1/f4.market.xiaomi.com/download/AppStore/096f34dec955dbde0597f4e701d1406000d432064/com.immomo.momo.apk");
        apks.add(apk5);
        ApkModel apk6 = new ApkModel();
        apk6.setName("手机淘宝");
        apk6.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/017a859792d09d7394108e0a618411675ec43f220");
        apk6.setUrl("http://121.29.10.1/f3.market.xiaomi.com/download/AppStore/0afc00452eb1a4dc42b20c9351eacacab4692a953/com.taobao.taobao.apk");
        apks.add(apk6);
        ApkModel apk7 = new ApkModel();
        apk7.setName("酷狗音乐");
        apk7.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0f2f050e21e42f75c7ecca55d01ac4e5e4e40ca8d");
        apk7.setUrl("http://121.18.239.1/f5.market.xiaomi.com/download/AppStore/053ed49c1545c6eec3e3e23b31568c731f940934f/com.kugou.android.apk");
        apks.add(apk7);
        ApkModel apk8 = new ApkModel();
        apk8.setName("网易云音乐");
        apk8.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/02374548ac39f3b7cdbf5bea4b0535b5d1f432f23");
        apk8.setUrl("http://121.18.239.1/f4.market.xiaomi.com/download/AppStore/0f458c5661acb492e30b808a2e3e4c8672e6b55e2/com.netease.cloudmusic.apk");
        apks.add(apk8);
        ApkModel apk9 = new ApkModel();
        apk9.setName("ofo共享单车");
        apk9.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0fe1a5c6092f3d9fa5c4c1e3158e6ff33f6418152");
        apk9.setUrl("http://60.28.125.1/f4.market.mi-img.com/download/AppStore/06954949fcd48414c16f726620cf2d52200550f56/so.ofo.labofo.apk");
        apks.add(apk9);
        ApkModel apk10 = new ApkModel();
        apk10.setName("摩拜单车");
        apk10.setIconUrl("http://file.market.xiaomi.com/thumbnail/PNG/l114/AppStore/0863a058a811148a5174d9784b7be2f1114191f83");
        apk10.setUrl("http://60.28.125.1/f4.market.xiaomi.com/download/AppStore/00cdeb4865c5a4a7d350fe30b9f812908a569cc8a/com.mobike.mobikeapp.apk");
        apks.add(apk10);
    }
}
