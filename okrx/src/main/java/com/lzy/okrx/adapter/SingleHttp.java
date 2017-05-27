package com.lzy.okrx.adapter;

import com.lzy.okgo.adapter.AdapterParam;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;
import com.lzy.okgo.model.Response;

import rx.Single;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/26
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class SingleHttp<T> implements CallAdapter<T, Single<Response<T>>> {
    @Override
    public Single<Response<T>> adapt(Call<T> call, AdapterParam param) {
        ObservableHttp<T> body = new ObservableHttp<>();
        return body.adapt(call, param).toSingle();
    }
}
