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
package com.lzy.demo.okrx2;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseRxDetailActivity;
import com.lzy.demo.ui.NumberProgressBar;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.demo.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.adapter.ObservableResponse;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RxFormUploadActivity extends BaseRxDetailActivity {

    @Bind(R.id.formUpload1) Button btnFormUpload1;
    @Bind(R.id.formUpload2) Button btnFormUpload2;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;
    @Bind(R.id.images) TextView tvImages;

    private List<ImageItem> imageItems;
    private NumberFormat numberFormat;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_rx_form_upload);
        ButterKnife.bind(this);
        setTitle("文件上传");

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        dispose();
    }

    @OnClick(R.id.selectImage)
    public void selectImage(View view) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(9);    //最多选择9张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                //noinspection unchecked
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItems != null && imageItems.size() > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < imageItems.size(); i++) {
                        if (i == imageItems.size() - 1) sb.append("图片").append(i + 1).append(" ： ").append(imageItems.get(i).path);
                        else sb.append("图片").append(i + 1).append(" ： ").append(imageItems.get(i).path).append("\n");
                    }
                    tvImages.setText(sb.toString());
                } else {
                    tvImages.setText("--");
                }
            } else {
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
                tvImages.setText("--");
            }
        }
    }

    @OnClick(R.id.formUpload1)
    public void formUpload1(View view) {
        ArrayList<File> files = new ArrayList<>();
        if (imageItems != null && imageItems.size() > 0) {
            for (int i = 0; i < imageItems.size(); i++) {
                files.add(new File(imageItems.get(i).path));
            }
        }
        //拼接参数
        OkGo.<String>post(Urls.URL_FORM_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .headers("header2", "headerValue2")//
                .params("param1", "paramValue1")//
                .params("param2", "paramValue2")//
//                .params("file1",new File("文件路径"))
//                .params("file2",new File("文件路径"))
//                .params("file3",new File("文件路径"))
                .addFileParams("file", files)//
                .converter(new StringConvert())//
                .adapt(new ObservableResponse<String>())//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        btnFormUpload1.setText("正在上传中...\n使用Rx方式做进度监听稍显麻烦,推荐使用方式2");
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Response<String> response) {
                        btnFormUpload1.setText("上传完成");
                        handleResponse(response);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        btnFormUpload1.setText("上传出错");
                        showToast(e.getMessage());
                        handleError(null);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @OnClick(R.id.formUpload2)
    public void formUpload2(View view) {
        final ArrayList<File> files = new ArrayList<>();
        if (imageItems != null && imageItems.size() > 0) {
            for (int i = 0; i < imageItems.size(); i++) {
                files.add(new File(imageItems.get(i).path));
            }
        }

        Observable.create(new ObservableOnSubscribe<Progress>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<Progress> e) throws Exception {
                OkGo.<String>post(Urls.URL_FORM_UPLOAD)//
                        .tag(this)//
                        .headers("header1", "headerValue1")//
                        .headers("header2", "headerValue2")//
                        .params("param1", "paramValue1")//
                        .params("param2", "paramValue2")//
                        //.params("file1",new File("文件路径"))
                        //.params("file2",new File("文件路径"))
                        //.params("file3",new File("文件路径"))
                        .addFileParams("file", files)//
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                e.onComplete();
                            }

                            @Override
                            public void onError(Response<String> response) {
                                e.onError(response.getException());
                            }

                            @Override
                            public void uploadProgress(Progress progress) {
                                e.onNext(progress);
                            }
                        });
            }
        })//
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(@NonNull Disposable disposable) throws Exception {
                        btnFormUpload2.setText("正在上传中...");
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//
                .subscribe(new Observer<Progress>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        addDisposable(d);
                    }

                    @Override
                    public void onNext(@NonNull Progress progress) {
                        System.out.println("uploadProgress: " + progress);

                        String downloadLength = Formatter.formatFileSize(getApplicationContext(), progress.currentSize);
                        String totalLength = Formatter.formatFileSize(getApplicationContext(), progress.totalSize);
                        tvDownloadSize.setText(downloadLength + "/" + totalLength);
                        String speed = Formatter.formatFileSize(getApplicationContext(), progress.speed);
                        tvNetSpeed.setText(String.format("%s/s", speed));
                        tvProgress.setText(numberFormat.format(progress.fraction));
                        pbProgress.setMax(10000);
                        pbProgress.setProgress((int) (progress.fraction * 10000));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        e.printStackTrace();
                        btnFormUpload2.setText("上传出错");
                        showToast(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        btnFormUpload2.setText("上传完成");
                    }
                });
    }
}
