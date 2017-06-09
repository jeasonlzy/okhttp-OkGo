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
package com.lzy.demo.okgo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.base.DividerItemDecoration;
import com.lzy.demo.model.ItemModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class CommonActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private List<ItemModel> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initToolBar(toolbar, true, "OkGo功能介绍");

        initData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(data));
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(new ItemModel("请求方法演示", "目前支持 GET，HEAD，OPTIONS，POST，PUT，DELETE, PATCH, TRACE"));
        data.add(new ItemModel("请求图片", "请求服务器返回bitmap对象"));
        data.add(new ItemModel("普通上传数据", "可以向服务器上传任意类型的文本数据，包括 String，JSONObject，JSONArray，byte[]，文件等"));
        data.add(new ItemModel("网络缓存基本用法", "默认提供了四种缓存模式，根据需要选择使用"));
        data.add(new ItemModel("支持https请求", "支持 cer,bks 证书，支持双向认证"));
        data.add(new ItemModel("cookie管理与session保持", "支持cookie的自动管理，也支持自己手动管理cookie，自动session保持"));
        data.add(new ItemModel("同步请求", "允许直接返回Response对象，会阻塞主线程，需要自行开启子线程"));
        data.add(new ItemModel("301重定向", "支持301重定向请求"));
        data.add(new ItemModel("测试页面", "用于测试特殊情况下的网络连接,可忽略"));
    }

    private class MainAdapter extends BaseQuickAdapter<ItemModel> {

        MainAdapter(List<ItemModel> data) {
            super(R.layout.item_main_list, data);
        }

        @Override
        protected void convert(final BaseViewHolder baseViewHolder, ItemModel itemModel) {
            baseViewHolder.setText(R.id.title, itemModel.title);
            baseViewHolder.setText(R.id.des, itemModel.des);
            baseViewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = baseViewHolder.getAdapterPosition();
                    if (position == 0) startActivity(new Intent(CommonActivity.this, MethodActivity.class));
                    if (position == 1) startActivity(new Intent(CommonActivity.this, BitmapRequestActivity.class));
                    if (position == 2) startActivity(new Intent(CommonActivity.this, UpActivity.class));
                    if (position == 3) startActivity(new Intent(CommonActivity.this, CacheActivity.class));
                    if (position == 4) startActivity(new Intent(CommonActivity.this, HttpsActivity.class));
                    if (position == 5) startActivity(new Intent(CommonActivity.this, CookieActivity.class));
                    if (position == 6) startActivity(new Intent(CommonActivity.this, SyncActivity.class));
                    if (position == 7) startActivity(new Intent(CommonActivity.this, RedirectActivity.class));
                    if (position == 8) startActivity(new Intent(CommonActivity.this, TestActivity.class));
                }
            });
        }
    }
}
