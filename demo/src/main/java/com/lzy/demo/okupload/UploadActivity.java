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
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.demo.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.XExecutor;
import com.lzy.okserver.upload.UploadListener;

import java.io.File;
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
public class UploadActivity extends BaseActivity implements XExecutor.OnAllTaskEndListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.gridView) GridView gridView;

    private List<ImageItem> images;
    private OkUpload okUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initToolBar(toolbar, true, "上传管理");

        okUpload = OkUpload.getInstance();
        okUpload.getThreadPool().setCorePoolSize(1);
        okUpload.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        okUpload.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    @Override
    public void onAllTaskEnd() {
        showToast("所有上传任务完成");
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
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                PostRequest<String> postRequest = OkGo.<String>post(Urls.URL_FORM_UPLOAD)//
                        .params("fileKey" + i, new File(images.get(i).path))//
                        .converter(new StringConvert());
                OkUpload.request(images.get(i).path, postRequest)//
                        .register(new ListUploadListener(gridView.getChildAt(i)))//
                        .register(new LogUploadListener<String>("UploadActivity"))//
                        .start();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                //noinspection unchecked
                images = (List<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                UploadAdapter adapter = new UploadAdapter(this, images, gridView.getWidth() / 3);
                gridView.setAdapter(adapter);
            } else {
                showToast("没有数据");
            }
        }
    }

    private class ListUploadListener extends UploadListener<String> {

        private UploadAdapter.ViewHolder holder;

        ListUploadListener(View tag) {
            super(tag);
            holder = (UploadAdapter.ViewHolder) tag.getTag();
        }

        @Override
        public void onStart(Progress progress) {
        }

        @Override
        public void onProgress(Progress progress) {
            holder.refresh(progress);
        }

        @Override
        public void onFinish(String s, Progress progress) {
            holder.finish();
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
