package com.lzy.okrx;

import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;

import rx.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RxAdapter<T> implements CallAdapter<T> {
    public static <T> RxAdapter<Observable<T>> create() {
        return RxAdapter.ConvertHolder.convert;
    }

    private static class ConvertHolder {
        private static RxAdapter convert = new RxAdapter();
    }

    @Override
    public <R> T adapt(Call<R> call) {
        return null;
    }
}