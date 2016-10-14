package com.lzy.okgo.request;

import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.utils.HttpUtils;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class BaseBodyRequest<R extends BaseBodyRequest> extends BaseRequest<R> implements HasBody<R> {

    protected MediaType mediaType;      //上传的MIME类型
    protected String string;            //上传的文本内容
    protected String json;              //上传的Json
    protected byte[] bs;                //上传的字节数据

    protected RequestBody requestBody;

    public BaseBodyRequest(String url) {
        super(url);
    }

    @SuppressWarnings("unchecked")
    @Override
    public R requestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R params(String key, File file) {
        params.put(key, file);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R addFileParams(String key, List<File> files) {
        params.putFileParams(key, files);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers) {
        params.putFileWrapperParams(key, fileWrappers);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R params(String key, File file, String fileName) {
        params.put(key, file, fileName);
        return (R) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public R params(String key, File file, String fileName, MediaType contentType) {
        params.put(key, file, fileName, contentType);
        return (R) this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    @Override
    public R upString(String string) {
        this.string = string;
        this.mediaType = HttpParams.MEDIA_TYPE_PLAIN;
        return (R) this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    @Override
    public R upJson(String json) {
        this.json = json;
        this.mediaType = HttpParams.MEDIA_TYPE_JSON;
        return (R) this;
    }

    /** 注意使用该方法上传字符串会清空实体中其他所有的参数，头信息不清除 */
    @SuppressWarnings("unchecked")
    @Override
    public R upBytes(byte[] bs) {
        this.bs = bs;
        this.mediaType = HttpParams.MEDIA_TYPE_STREAM;
        return (R) this;
    }

    @Override
    public RequestBody generateRequestBody() {
        if (requestBody != null) return requestBody;                                           //自定义的请求体
        if (string != null && mediaType != null) return RequestBody.create(mediaType, string); //post上传字符串数据
        if (json != null && mediaType != null) return RequestBody.create(mediaType, json);     //post上传json数据
        if (bs != null && mediaType != null) return RequestBody.create(mediaType, bs);         //post上传字节数组
        return HttpUtils.generateMultipartRequestBody(params);
    }
}