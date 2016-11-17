package com.lzy.okgo.request;

import com.lzy.okgo.model.HttpParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：表示当前请求是否具有请求体
 * 修订历史：
 * ================================================
 */
public interface HasBody<R> {

    R isMultipart(boolean isMultipart);

    R requestBody(RequestBody requestBody);

    R params(String key, File file);

    R addFileParams(String key, List<File> files);

    R addFileWrapperParams(String key, List<HttpParams.FileWrapper> fileWrappers);

    R params(String key, File file, String fileName);

    R params(String key, File file, String fileName, MediaType contentType);

    R upString(String string);

    R upJson(String json);

    R upJson(JSONObject jsonObject);

    R upJson(JSONArray jsonArray);

    R upBytes(byte[] bs);
}