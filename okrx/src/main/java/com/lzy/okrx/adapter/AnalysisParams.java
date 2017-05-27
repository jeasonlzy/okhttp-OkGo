package com.lzy.okrx.adapter;

import com.lzy.okgo.adapter.AdapterParam;
import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.model.Response;
import com.lzy.okrx.subscribe.CallEnqueueOnSubscribe;
import com.lzy.okrx.subscribe.CallExecuteOnSubscribe;

import rx.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/27
 * 描    述：
 * 修订历史：
 * ================================================
 */
class AnalysisParams {

    static <T> Observable.OnSubscribe<Response<T>> analysis(Call<T> call, AdapterParam param) {
        Observable.OnSubscribe<Response<T>> onSubscribe;
        if (param == null) param = new AdapterParam();
        if (param.isAsync) {
            onSubscribe = new CallEnqueueOnSubscribe<>(call);
        } else {
            onSubscribe = new CallExecuteOnSubscribe<>(call);
        }
        return onSubscribe;
    }
}
