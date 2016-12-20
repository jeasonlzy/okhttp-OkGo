package com.lzy.demo.cache;

import com.google.gson.stream.JsonReader;
import com.lzy.demo.model.NewsResponse;
import com.lzy.demo.utils.Convert;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public abstract class NewsCallback<T> extends AbsCallback<T> {

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
    public T convertSuccess(Response response) throws Exception {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];
        if (!(type instanceof ParameterizedType)) throw new IllegalStateException("没有填写泛型参数");

        JsonReader jsonReader = new JsonReader(response.body().charStream());
        Type rawType = ((ParameterizedType) type).getRawType();
        if (rawType == NewsResponse.class) {
            NewsResponse newsResponse = Convert.fromJson(jsonReader, type);
            if (newsResponse.showapi_res_code == 0) {
                response.close();
                //noinspection unchecked
                return (T) newsResponse;
            } else {
                response.close();
                throw new IllegalStateException(newsResponse.showapi_res_error);
            }
        } else {
            response.close();
            throw new IllegalStateException("基类错误无法解析!");
        }
    }
}