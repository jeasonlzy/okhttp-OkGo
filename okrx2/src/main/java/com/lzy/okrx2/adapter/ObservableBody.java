package com.lzy.okrx2.adapter;

import com.lzy.okgo.adapter.AdapterParam;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;
import com.lzy.okgo.model.Response;
import com.lzy.okrx2.observable.BodyObservable;

import io.reactivex.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ObservableBody<T> implements CallAdapter<T, Observable<T>> {
    @Override
    public Observable<T> adapt(Call<T> call, AdapterParam param) {
        Observable<Response<T>> observable = AnalysisParams.analysis(call, param);
        return new BodyObservable<>(observable);
    }
}
