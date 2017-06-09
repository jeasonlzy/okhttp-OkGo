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
package com.lzy.demo.okupload;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.XExecutor;

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
public class UploadingActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.select) Button select;
    @Bind(R.id.upload) Button upload;

    private OkUpload okUpload;
    private UploadAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);
        initToolBar(toolbar, true, "上传中任务");

        select.setVisibility(View.GONE);
        upload.setVisibility(View.GONE);

        okUpload = OkUpload.getInstance();
        okUpload.getThreadPool().setCorePoolSize(1);

        adapter = new UploadAdapter(this);
        adapter.updateData(UploadAdapter.TYPE_ING);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        okUpload.addOnAllTaskEndListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okUpload.removeOnAllTaskEndListener(this);
        adapter.unRegister();
    }

    @Override
    public void onAllTaskEnd() {
        showToast("所有上传任务已结束");
    }
}
