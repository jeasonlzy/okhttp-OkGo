package com.lzy.okhttputils.request;

import com.lzy.okhttputils.model.RequestParams;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/16
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class PutRequest extends BaseRequest<PutRequest> {

    public PutRequest(String url) {
        super(url);
    }

    @Override
    public RequestBody generateRequestBody() {
        if (params.fileParamsMap.isEmpty()) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : params.urlParamsMap.keySet()) {
                bodyBuilder.add(key, params.urlParamsMap.get(key));
            }
            return bodyBuilder.build();
        } else {
            //表单提交，有文件
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
            //拼接键值对
            if (!params.urlParamsMap.isEmpty()) {
                for (Map.Entry<String, String> entry : params.urlParamsMap.entrySet()) {
                    multipartBodybuilder.addFormDataPart(entry.getKey(), entry.getValue());
                }
            }
            //拼接文件
            for (Map.Entry<String, RequestParams.FileWrapper> entry : params.fileParamsMap.entrySet()) {
                RequestBody fileBody = RequestBody.create(entry.getValue().contentType, entry.getValue().file);
                multipartBodybuilder.addFormDataPart(entry.getKey(), entry.getValue().fileName, fileBody);
            }
            return multipartBodybuilder.build();
        }
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder();
        try {
            headers.put("Content-Length", String.valueOf(requestBody.contentLength()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        appendHeaders(requestBuilder);
        return requestBuilder.put(requestBody).url(url).tag(tag).build();
    }
}
