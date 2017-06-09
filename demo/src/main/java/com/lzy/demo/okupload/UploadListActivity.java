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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.XExecutor;
import com.lzy.okserver.upload.UploadTask;

import java.util.List;

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
public class UploadListActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private UploadAdapter adapter;
    private OkUpload okUpload;
    private List<UploadTask<?>> tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_list);
        initToolBar(toolbar, true, "开始上传");

        okUpload = OkUpload.getInstance();
        okUpload.getThreadPool().setCorePoolSize(1);

        adapter = new UploadAdapter(this);
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

    @OnClick(R.id.select)
    public void select(View view) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setSelectLimit(9);
        imagePicker.setCrop(false);
        Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.upload)
    public void upload(View view) {
        if (tasks == null) {
            showToast("请先选择图片");
            return;
        }
        for (UploadTask<?> task : tasks) {
            task.start();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                //noinspection unchecked
                List<ImageItem> images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                tasks = adapter.updateData(images);
            } else {
                showToast("没有数据");
            }
        }
    }
}
