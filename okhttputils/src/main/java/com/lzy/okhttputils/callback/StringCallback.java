package com.lzy.okhttputils.callback;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：返回字符串类型的数据，可以使用 BeanCallBack<String> 代替
 * 修订历史：
 * ================================================
 */
public abstract class StringCallback extends AbsCallback<String> {

    @Override
    public String parseNetworkResponse(Response response) throws IOException {
        return response.body().string();
    }
}
