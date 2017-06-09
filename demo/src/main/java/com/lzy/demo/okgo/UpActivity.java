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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseDetailActivity;
import com.lzy.demo.callback.DialogCallback;
import com.lzy.demo.model.LzyResponse;
import com.lzy.demo.model.ServerModel;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.demo.utils.Urls;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
public class UpActivity extends BaseDetailActivity {

    @Bind(R.id.images) TextView tvImages;

    private ImageItem imageItem;

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_up_text);
        ButterKnife.bind(this);
        setTitle("普通上传数据");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkGo.getInstance().cancelTag(this);
    }

    @OnClick(R.id.selectImage)
    public void selectImage(View view) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setMultiMode(false);   //单选
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
                List<ImageItem> imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imageItems != null && imageItems.size() > 0) {
                    imageItem = imageItems.get(0);
                    tvImages.setText(imageItem.path);
                } else {
                    tvImages.setText("--");
                }
            } else {
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
                tvImages.setText("--");
            }
        }
    }

    @OnClick(R.id.upJson)
    public void upJson(View view) {

        HashMap<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "这里是需要提交的json格式数据");
        params.put("key3", "也可以使用三方工具将对象转成json字符串");
        params.put("key4", "其实你怎么高兴怎么写都行");
        JSONObject jsonObject = new JSONObject(params);

        OkGo.<LzyResponse<ServerModel>>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
//                .params("param1", "paramValue1")//  这里不要使用params，upJson 与 params 是互斥的，只有 upJson 的数据会被上传
                .upJson(jsonObject)//
                .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        handleError(response);
                    }
                });
    }

    @OnClick(R.id.upString)
    public void upString(View view) {
        OkGo.<LzyResponse<ServerModel>>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
//                .params("param1", "paramValue1")// 这里不要使用params，upString 与 params 是互斥的，只有 upString 的数据会被上传
                .upString("这是要上传的长文本数据！")//
//                .upString("这是要上传的长文本数据！", MediaType.parse("application/xml"))// 比如上传xml数据，这里就可以自己指定请求头
                .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        handleError(response);
                    }
                });
    }

    @OnClick(R.id.upBytes)
    public void upBytes(View view) {
        OkGo.<LzyResponse<ServerModel>>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
//                .params("param1", "paramValue1")// 这里不要使用params，upBytes 与 params 是互斥的，只有 upBytes 的数据会被上传
                .upBytes("这是字节数据".getBytes())//
                .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        handleError(response);
                    }
                });
    }

    @OnClick(R.id.upFile)
    public void upFile(View view) {
        if (imageItem == null) {
            showToast("请先选择文件！");
            return;
        }
        OkGo.<LzyResponse<ServerModel>>post(Urls.URL_TEXT_UPLOAD)//
                .tag(this)//
                .headers("header1", "headerValue1")//
//                .params("param1", "paramValue1")// 这里不要使用params，upBytes 与 params 是互斥的，只有 upBytes 的数据会被上传
                .upFile(new File(imageItem.path))//
                .execute(new DialogCallback<LzyResponse<ServerModel>>(this) {
                    @Override
                    public void onSuccess(Response<LzyResponse<ServerModel>> response) {
                        handleResponse(response);
                    }

                    @Override
                    public void onError(Response<LzyResponse<ServerModel>> response) {
                        handleError(response);
                    }
                });
    }
}
