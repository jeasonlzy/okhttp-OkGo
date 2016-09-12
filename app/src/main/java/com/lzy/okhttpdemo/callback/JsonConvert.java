package com.lzy.okhttpdemo.callback;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okhttpgo.convert.Converter;

import org.json.JSONObject;

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

    public Class<T> clazz;
    public Type type;

    public static <T> JsonConvert<T> create(Class<T> clazz) {
        return new JsonConvert<>(clazz);
    }

    public static <T> JsonConvert<T> create(Type type) {
        return new JsonConvert<>(type);
    }

    /** 传class,直接返回解析生成的对象 */
    public JsonConvert(Class<T> clazz) {
        this.clazz = clazz;
    }

    /** 对于需要返回集合类型的,可以传type     type = new TypeToken<List<你的数据类型>>(){}.getType() */
    public JsonConvert(Type type) {
        this.type = type;
    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        String responseData = response.body().string();
        if (TextUtils.isEmpty(responseData)) return null;

        /**
         * 一般来说，服务器返回的响应码都包含 code，msg，data 三部分，在此根据自己的业务需要完成相应的逻辑判断
         * 以下只是一个示例，具体业务具体实现
         */
        JSONObject jsonObject = new JSONObject(responseData);
        final String msg = jsonObject.optString("msg", "");
        final int code = jsonObject.optInt("code", 0);
        String data = jsonObject.optString("data", "");
        switch (code) {
            case 0:
                /**
                 * 假如 code = 0 代表成功，这里默认实现了Gson解析,可以自己替换成fastjson等
                 * clazz类型就是解析javaBean
                 * type类型就是解析List<javaBean>
                 */
                if (clazz == String.class) return (T) data;
                if (clazz != null) return new Gson().fromJson(data, clazz);
                if (type != null) return new Gson().fromJson(data, type);
                break;
            case 104:
                //比如：用户授权信息无效，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户授权信息无效");
            case 105:
                //比如：用户收取信息已过期，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户收取信息已过期");
            case 106:
                //比如：用户账户被禁用，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("用户账户被禁用");
            case 300:
                //比如：其他乱七八糟的等，在此实现相应的逻辑，弹出对话或者跳转到其他页面等,该抛出错误，会在onError中回调。
                throw new IllegalStateException("其他乱七八糟的等");
            default:
                throw new IllegalStateException("错误代码：" + code + "，错误信息：" + msg);
        }
        throw new IllegalStateException("数据解析错误");
    }
}
