package com.lzy.okgo.request;

import com.lzy.okgo.utils.HttpUtils;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：Get请求的实现类，注意需要传入本类的泛型
 * 修订历史：
 * ================================================
 */
public class GetRequest extends BaseRequest<GetRequest> {

    public GetRequest(String url) {
        super(url);
        method = "GET";
    }

    @Override
    public RequestBody generateRequestBody() {
        return null;
    }

    @Override
    public Request generateRequest(RequestBody requestBody) {
        Request.Builder requestBuilder = HttpUtils.appendHeaders(headers);
        url = HttpUtils.createUrlFromParams(baseUrl, params.urlParamsMap);
        return requestBuilder.get().url(url).tag(tag).build();
    }
}