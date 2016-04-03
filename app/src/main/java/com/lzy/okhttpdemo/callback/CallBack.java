package com.lzy.okhttpdemo.callback;

import android.util.Log;

import com.google.gson.Gson;
import com.lzy.okhttputils.callback.AbsCallback;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：默认将返回的数据解析成需要的Bean
 * 修订历史：
 * ================================================
 */
public abstract class CallBack<T> extends AbsCallback<T> {

    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        String data = response.body().string();
        Log.i("CallBack", "请求网络返回数据: ------    " + data);
        JSONObject jsonObject = new JSONObject(data);
        boolean success = false;
        String resultMsg = "";
        String code = jsonObject.optString("code", null);
        if ("0".equals(code)) {
            success = true;  //请求数据成功
        } else if ("104".equals(code)) {
            resultMsg = "用户授权信息无效";
        } else if ("105".equals(code)) {
            resultMsg = "用户收取信息已过期";
        } else if ("106".equals(code)) {
            resultMsg = "用户账户被禁用";
        } else {
            resultMsg = "错误代码：" + code + "，错误信息：" + jsonObject.optString("message", null);
        }
        if (success) {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                //如果用户写了泛型，就会进入这里，否者不会执行
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class beanClass = (Class) parameterizedType.getActualTypeArguments()[0];
                Gson gson = new Gson();
                return (T) gson.fromJson(response.body().string(), beanClass);
            }
        } else {
            Log.i("CallBack", resultMsg);
        }
        return (T) response;
    }
}
