package com.lzy.okhttpdemo.callback;

import com.google.gson.stream.JsonReader;
import com.lzy.okhttpdemo.model.LzyResponse;
import com.lzy.okhttpdemo.model.SimpleResponse;
import com.lzy.okhttpdemo.utils.Convert;
import com.lzy.okhttpgo.convert.Converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class JsonConvert<T> implements Converter<T> {

    public static <T> JsonConvert<T> create() {
        return new JsonConvert<>();
    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");

        Type[] args = ((ParameterizedType) type).getActualTypeArguments();
        if (args == null || args.length == 0) throw new IllegalStateException("没有填写泛型参数");

        JsonReader jsonReader = new JsonReader(response.body().charStream());

        //无数据类型
        if (args[0] == Void.class) {
            SimpleResponse baseWbgResponse = Convert.fromJson(jsonReader, SimpleResponse.class);
            return (T) baseWbgResponse.toLzyResponse();
        }

        //有数据类型
        if (args[0] == LzyResponse.class) {
            LzyResponse lzyResponse = Convert.fromJson(jsonReader, type);
            int code = lzyResponse.code;
            if (code == 0) {
                return (T) lzyResponse;
            } else if (code == 104) {
                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户授权信息无效");
            } else if (code == 105) {
                //比如：用户收取信息已过期，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户收取信息已过期");
            } else if (code == 106) {
                //比如：用户账户被禁用，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户账户被禁用");
            } else if (code == 300) {
                //比如：其他乱七八糟的等，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("其他乱七八糟的等");
            } else {
                throw new IllegalStateException("错误代码：" + code + "，错误信息：" + lzyResponse.msg);
            }
        }
        throw new IllegalStateException("基类错误无法解析!");
    }
}