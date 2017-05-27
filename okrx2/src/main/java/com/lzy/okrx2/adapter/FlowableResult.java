package com.lzy.okrx2.adapter;

import com.lzy.okgo.adapter.AdapterParam;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;
import com.lzy.okgo.model.Result;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class FlowableResult<T> implements CallAdapter<T, Flowable<Result<T>>> {
    @Override
    public Flowable<Result<T>> adapt(Call<T> call, AdapterParam param) {
        ObservableResult<T> observable = new ObservableResult<>();
        return observable.adapt(call, param).toFlowable(BackpressureStrategy.LATEST);
    }
}
