package com.lzy.okgo.adapter;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：默认的工厂处理,不对返回值做任何操作
 * 修订历史：
 * ================================================
 */
public class DefaultCallAdapter<T> implements CallAdapter<Call<T>> {

    public static <T> DefaultCallAdapter<T> create() {
        return new DefaultCallAdapter<>();
    }

    @Override
    public <R> Call<T> adapt(Call<R> call) {
        return (Call<T>) call;
    }
}