package com.lzy.okhttpgo.rx;

import com.lzy.okhttpgo.OkHttpGo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

/** 带有Executor的工厂类*/
public class ExecutorCallAdapterFactory extends CallAdapter.Factory {
    final Executor callbackExecutor;

    ExecutorCallAdapterFactory(Executor callbackExecutor) {
        this.callbackExecutor = callbackExecutor;
    }

    @Override
    public CallAdapter<Call<?>> get(Type returnType, Annotation[] annotations, OkHttpGo go) {
        if (getRawType(returnType) != Call.class) {
            return null;
        }
        final Type responseType = TypeUtils.getCallResponseType(returnType);
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Call<R> adapt(Call<R> call) {
                return new DefaultCall<>(callbackExecutor, call);
            }
        };
    }
}