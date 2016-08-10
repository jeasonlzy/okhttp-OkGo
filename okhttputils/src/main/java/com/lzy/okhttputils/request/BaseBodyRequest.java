package com.lzy.okhttputils.request;

import android.support.annotation.NonNull;

import com.lzy.okhttputils.model.HttpParams;
import com.lzy.okhttputils.utils.HttpUtils;

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

    protected RequestBody requestBody;

    public BaseBodyRequest(String url) {
        super(url);
    }

    @SuppressWarnings("unchecked")
    @Override
    public R requestBody(@NonNull RequestBody requestBody) {
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

    @Override
    protected RequestBody generateRequestBody() {
        if (requestBody != null) return requestBody;
        return HttpUtils.generateMultipartRequestBody(params);
    }
}