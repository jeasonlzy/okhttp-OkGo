package com.lzy.okhttpdemo.cache;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.BaseRequest;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/9/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class NewsCallback<T> extends AbsCallback<T> {

    private Class<T> clazz;
    private Type type;

    /**
     * 传class,直接返回解析生成的对象
     */
    public NewsCallback(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 对于需要返回集合类型的,可以传type
     * type = new TypeToken<List<你的数据类型>>(){}.getType()
     */
    public NewsCallback(Type type) {
        this.type = type;
    }

    @Override
    public void onBefore(BaseRequest request) {
        //缓存演示代码所有请求需要添加 apikey
        request.headers("apikey", Urls.APIKEY);
    }

    /**
     * 这里的数据解析是根据 http://apistore.baidu.com/apiworks/servicedetail/688.html 返回的数据来写的
     * 实际使用中,自己服务器返回的数据格式和上面网站肯定不一样,所以以下是参考代码,根据实际情况自己改写
     */
    @Override
    public T parseNetworkResponse(Response response) throws Exception {
        String responseData = response.body().string();
        if (TextUtils.isEmpty(responseData)) return null;

        /**
         * 一般来说，服务器返回的响应码都包含 code，msg，data 三部分，在此根据自己的业务需要完成相应的逻辑判断
         * 以下只是一个示例，具体业务具体实现
         */
        JSONObject jsonObject = new JSONObject(responseData);
        final String msg = jsonObject.optString("showapi_res_error", "");
        final int code = jsonObject.optInt("showapi_res_code", 0);
        String data = jsonObject.optString("showapi_res_body", "");
        if (code == 0) {
            if (clazz == String.class) return (T) data;
            if (clazz != null) return new Gson().fromJson(data, clazz);
            if (type != null) return new Gson().fromJson(data, type);
        }
        throw new IllegalStateException(msg);
    }
}
