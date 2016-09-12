package com.lzy.okhttpgo.rx;

import com.lzy.okhttpgo.OkHttpGo;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/** 使用 addCallAdapterFactory(Factory)添加 */
public interface CallAdapter<T> {

    /** 返回值的类型 */
    Type responseType();

    /** call执行的代理方法 */
    <R> T adapt(Call<R> call);

    /** CallAdapter 的工厂类 */
    abstract class Factory {
        /** 根据返回值类型和注解参数创建 CallAdapter 对象 */
        public abstract CallAdapter<?> get(Type returnType, Annotation[] annotations, OkHttpGo go);

        /** 获取多个泛型参数的其中一个,比如 Map<String, ? extends Runnable> 获取第一个就是 Runnable */
        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return TypeUtils.getParameterUpperBound(index, type);
        }

        /** 根据泛型 type 获取对应的class,例如 List<? extends Runnable> 可以得到 List.class */
        protected static Class<?> getRawType(Type type) {
            return TypeUtils.getRawType(type);
        }
    }
}