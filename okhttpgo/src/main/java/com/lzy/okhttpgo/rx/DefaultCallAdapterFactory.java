package com.lzy.okhttpgo.rx;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/** 默认的工厂处理,不对返回值做任何操作*/
final class DefaultCallAdapterFactory extends CallAdapter.Factory {
    static final CallAdapter.Factory INSTANCE = new DefaultCallAdapterFactory();

    @Override
    public CallAdapter<?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        //检查返回值是否是 call 对象
        if (getRawType(returnType) != Call.class) {
            return null;
        }

        //获取返回值对象的泛型参数
        final Type responseType = TypeUtils.getCallResponseType(returnType);
        //生成对象
        return new CallAdapter<Call<?>>() {
            @Override
            public Type responseType() {
                return responseType;
            }

            @Override
            public <R> Call<R> adapt(Call<R> call) {
                return call;
            }
        };
    }
}
