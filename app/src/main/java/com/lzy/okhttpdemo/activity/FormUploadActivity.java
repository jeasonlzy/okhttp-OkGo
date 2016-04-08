package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okhttpdemo.Bean.RequestInfo;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.JsonCallback;
import com.lzy.okhttpdemo.ui.NumberProgressBar;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.request.BaseRequest;
import com.lzy.okhttputils.request.PostRequest;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class FormUploadActivity extends BaseActivity {

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
        setTitle(Constant.getData().get(4)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
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
                        if (i == imageItems.size() - 1)
                            sb.append("图片").append(i + 1).append(" ： ").append(imageItems.get(i).path);
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
        //拼接参数
        PostRequest request = OkHttpUtils.post(Urls.URL_FORM_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .headers("header2", "headerValue2")//
                .params("param1", "paramValue1")//
                .params("param2", "paramValue2");//
        //拼接选中的文件参数（如果文件个数已知，可以链式调用到底，就不用这么断开了）
        if (imageItems != null && imageItems.size() > 0) {
            for (int i = 0; i < imageItems.size(); i++) {
                request.params("file" + (i + 1), new File(imageItems.get(i).path));
            }
        }
        //执行请求
        request.execute(new ProgressUpCallBack<>(this, RequestInfo.class));
    }

    private class ProgressUpCallBack<T> extends JsonCallback<T> {

        public ProgressUpCallBack(Activity activity, Class<T> clazz) {
            super(clazz);
        }

        @Override
        public void onBefore(BaseRequest request) {
            super.onBefore(request);
            btnFormUpload.setText("正在上传中...");
        }

        @Override
        public void onResponse(boolean isFromCache, T s, Request request, Response response) {
            handleResponse(isFromCache, s, request, response);
            btnFormUpload.setText("上传完成");
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
            btnFormUpload.setText("上传出错");
        }

        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            System.out.println("upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);

            String downloadLength = Formatter.formatFileSize(getApplicationContext(), currentSize);
            String totalLength = Formatter.formatFileSize(getApplicationContext(), totalSize);
            tvDownloadSize.setText(downloadLength + "/" + totalLength);
            String netSpeed = Formatter.formatFileSize(getApplicationContext(), networkSpeed);
            tvNetSpeed.setText(netSpeed + "/S");
            tvProgress.setText((Math.round(progress * 10000) * 1.0f / 100) + "%");
            pbProgress.setMax(100);
            pbProgress.setProgress((int) (progress * 100));
        }
    }
}
