package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.JsonCallBack;
import com.lzy.okhttputils.request.BaseRequest;
import com.lzy.okhttputils.request.PostRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.Response;

public class FormUploadActivity extends BaseActivity {

    @Bind(R.id.requestState) TextView requestState;
    @Bind(R.id.requestHeaders) TextView requestHeaders;
    @Bind(R.id.responseData) TextView responseData;
    @Bind(R.id.responseHeader) TextView responseHeader;
    @Bind(R.id.images) TextView images;

    private ArrayList<ImageItem> imageItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_upload);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(4)[0]);
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
                            sb.append("图片").append(i).append(" ： ").append(imageItems.get(i).path);
                        else sb.append("图片").append(i).append(" ： ").append(imageItems.get(i).path).append("\n");
                    }
                    images.setText(sb.toString());
                } else {
                    images.setText("--");
                }
            } else {
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
                images.setText("--");
            }
        }
    }

    @OnClick(R.id.formUpload)
    public void formUpload(View view) {
        //拼接参数
        PostRequest request = OkHttpUtils.post(Urls.URL_NOHTTP_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .headers("header2", "headerValue2")//
                .params("param1", "paramValue1")//
                .params("param2", "paramValue2");//
        //拼接选中的文件参数（如果文件个数已知，可以链式调用到底，就不用这么断开了）
        if (imageItems != null && imageItems.size() > 0) {
            for (ImageItem item : imageItems) {
                request.params("file1", new File(item.path));
            }
        }
        //执行请求
        request.execute(new ProgressUpCallBack(this));
    }

    private class ProgressUpCallBack extends JsonCallBack<String> {
        private ProgressDialog dialog;

        public ProgressUpCallBack(Activity activity) {
            dialog = new ProgressDialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setMax(100);
            dialog.setMessage("文件上传中...");
        }

        @Override
        public void onBefore(BaseRequest request) {
            System.out.println("onBefore");
            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, Response response) {
            handleJsonResponse(isFromCache, s, request, response);
        }

        @Override
        public void onAfter(boolean isFromCache, @Nullable String t, Call call, Response response, @Nullable Exception e) {
            System.out.println("onAfter");
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            System.out.println("onError");
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
        }

        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            System.out.println("upProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
            dialog.setProgress((int) (progress * 100));
        }

        @Override
        public void downloadProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            System.out.println("downloadProgress -- " + totalSize + "  " + currentSize + "  " + progress + "  " + networkSpeed);
        }
    }

    private void handleJsonResponse(boolean isFromCache, String s, Request request, Response response) {
        requestState.setText("请求成功  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

        Headers requestHeadersString = request.headers();
        Set<String> requestNames = requestHeadersString.names();
        StringBuilder sb = new StringBuilder();
        for (String name : requestNames) {
            sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
        }
        requestHeaders.setText(sb.toString());

        responseData.setText(s);

        Headers responseHeadersString = response.headers();
        Set<String> names = responseHeadersString.names();
        sb = new StringBuilder();
        for (String name : names) {
            sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
        }
        responseHeader.setText(sb.toString());
    }

    private void handleError(boolean isFromCache, Call call, @Nullable Response response) {
        Request request = call.request();
        requestState.setText("请求失败  是否来自缓存：" + isFromCache + "  请求方式：" + request.method());

        Headers requestHeadersString = request.headers();
        Set<String> requestNames = requestHeadersString.names();
        StringBuilder sb = new StringBuilder();
        for (String name : requestNames) {
            sb.append(name).append(" ： ").append(requestHeadersString.get(name)).append("\n");
        }
        requestHeaders.setText(sb.toString());

        responseData.setText("--");
        if (response != null) {
            Headers responseHeadersString = response.headers();
            Set<String> names = responseHeadersString.names();
            sb = new StringBuilder();
            for (String name : names) {
                sb.append(name).append(" ： ").append(responseHeadersString.get(name)).append("\n");
            }
            responseHeader.setText(sb.toString());
        } else {
            responseHeader.setText("--");
        }
    }
}
