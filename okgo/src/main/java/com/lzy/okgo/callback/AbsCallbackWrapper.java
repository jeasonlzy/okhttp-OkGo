package com.lzy.okgo.callback;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：所有回调的包装类,空实现
 * 修订历史：
 * ================================================
 */
public class AbsCallbackWrapper<T> extends AbsCallback<T> {
    @Override
    public T convertSuccess(Response value) throws Exception {
        value.close();
        return (T) value;
    }

    @Override
    public void onSuccess(T t, Call call, Response response) {
    }
}