package com.lzy.okhttputils.request;

import android.text.TextUtils;

import okhttp3.MediaType;
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
public class DeleteRequest extends BaseRequest<DeleteRequest> {

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");

    private String content;
    private MediaType mediaType;

    public DeleteRequest(String url) {
        super(url);
    }

    public DeleteRequest content(String content) {
        this.content = content;
        this.mediaType = MEDIA_TYPE_PLAIN;
        return this;
    }

    @Override
    protected RequestBody generateRequestBody() {
        if (TextUtils.isEmpty(content)) {
            throw new IllegalStateException("必须设置delete请求的 content，请调用content(String content) 方法");
        }
        return RequestBody.create(mediaType, content);
    }

    @Override
    protected Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = new Request.Builder();
        appendHeaders(requestBuilder);
        url = createUrlFromParams(url, params.urlParamsMap);
        return requestBuilder.delete(requestBody).url(url).tag(tag).build();
    }
}
