package com.lzy.okhttpgo.adapter;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/9/11
 * 描    述：默认的工厂处理,不对返回值做任何操作
 * 修订历史：
 * ================================================
 */
public class DefaultCallAdapter<T> implements CallAdapter<T> {

    public static <T> DefaultCallAdapter<T> create() {
        return DefaultCallAdapter.ConvertHolder.convert;
    }

    private static class ConvertHolder {
        private static DefaultCallAdapter convert = new DefaultCallAdapter();
    }

    @Override
    public <R> T adapt(Call<R> call) {
        return (T) call;
    }
}