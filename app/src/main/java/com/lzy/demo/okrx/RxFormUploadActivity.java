package com.lzy.demo.okrx;

import android.content.Intent;
import android.os.Bundle;
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
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okrx.RxAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class RxFormUploadActivity extends BaseRxDetailActivity {

    @Bind(R.id.formUpload) Button btnFormUpload;
    @Bind(R.id.downloadSize) TextView tvDownloadSize;
    @Bind(R.id.tvProgress) TextView tvProgress;
    @Bind(R.id.netSpeed) TextView tvNetSpeed;
    @Bind(R.id.pbProgress) NumberProgressBar pbProgress;
    @Bind(R.id.images) TextView tvImages;

    private ArrayList<ImageItem> imageItems;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_form_upload);
        ButterKnife.bind(this);
        setTitle("文件上传");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        unSubscribe();
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

    @OnClick(R.id.formUpload)
    public void formUpload(View view) {
        ArrayList<File> files = new ArrayList<>();
        if (imageItems != null && imageItems.size() > 0) {
            for (int i = 0; i < imageItems.size(); i++) {
                files.add(new File(imageItems.get(i).path));
            }
        }
        //拼接参数
        OkGo.post(Urls.URL_FORM_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .headers("header2", "headerValue2")//
                .params("param1", "paramValue1")//
                .params("param2", "paramValue2")//
//                .params("file1",new File("文件路径"))   //这种方式为一个key，对应一个文件
//                .params("file2",new File("文件路径"))
//                .params("file3",new File("文件路径"))
                .addFileParams("file", files)           // 这种方式为同一个key，上传多个文件
                .getCall(StringConvert.create(), RxAdapter.<String>create())//
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        btnFormUpload.setText("正在上传中...\n使用Rx方式做进度监听稍显麻烦,推荐使用回调方式");
                    }
                })//
                .observeOn(AndroidSchedulers.mainThread())//切换到主线程
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        btnFormUpload.setText("上传完成");
                        handleResponse(s, null, null);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        btnFormUpload.setText("上传出错");
                        showToast("请求失败");
                        handleError(null, null);
                    }
                });
    }
}