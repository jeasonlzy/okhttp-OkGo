package com.lzy.okrx2.adapter;

import com.lzy.okgo.adapter.AdapterParam;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;

import io.reactivex.Maybe;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MaybeBody<T> implements CallAdapter<T, Maybe<T>> {
    @Override
    public Maybe<T> adapt(Call<T> call, AdapterParam param) {
        ObservableBody<T> observable = new ObservableBody<>();
        return observable.adapt(call, param).singleElement();
    }
}
