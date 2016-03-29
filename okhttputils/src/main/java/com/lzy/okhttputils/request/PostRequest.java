package com.lzy.okhttputils.request;

import com.lzy.okhttputils.model.RequestParams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：Post请求的实现类，注意需要传入本类的泛型
 * 修订历史：
 * ================================================
 */
public class PostRequest extends BaseRequest<PostRequest> {

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");

    private String content;      //上传的文本内容
    private MediaType mediaType; //上传的MIME类型
    private String string;       //上传的文本内容
    private String json;         //上传的Json
    private byte[] bs;           //上传的字节数据

    public PostRequest(String url) {
        super(url);
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    public PostRequest content(String content) {
        this.content = content;
        return this;
    }

    public PostRequest mediaType(MediaType mediaType) {
        this.mediaType = mediaType;
        return this;
    }

    public PostRequest postString(String string) {
        this.string = string;
        this.mediaType = MEDIA_TYPE_PLAIN;
        return this;
    }

    public PostRequest postJson(String json) {
        this.json = json;
        this.mediaType = MEDIA_TYPE_JSON;
        return this;
    }

    public PostRequest postStream(byte[] bs) {
        this.bs = bs;
        this.mediaType = MEDIA_TYPE_JSON;
        return this;
    }

    @Override
    public RequestBody generateRequestBody() {
        if (content != null && mediaType != null)
            return RequestBody.create(mediaType, content);//post上传字符串数据
        if (string != null && mediaType != null)
            return RequestBody.create(mediaType, string);//post上传字符串数据
        if (json != null && mediaType != null)
            return RequestBody.create(mediaType, json); //post上传json数据
        if (bs != null && mediaType != null) return RequestBody.create(mediaType, bs);//post上传字节数组

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
        return requestBuilder.post(requestBody).url(url).tag(tag).build();
    }
}
