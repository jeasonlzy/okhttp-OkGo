/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.demo.okrx2;

import com.lzy.demo.callback.JsonConvert;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpMethod;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.base.Request;
import com.lzy.okrx2.adapter.ObservableBody;

import java.lang.reflect.Type;

import io.reactivex.Observable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RxUtils {

    public static <T> Observable<T> request(HttpMethod method, String url, Type type) {
        return request(method, url, type, null);
    }

    public static <T> Observable<T> request(HttpMethod method, String url, Type type, HttpParams params) {
        return request(method, url, type, params, null);
    }

    public static <T> Observable<T> request(HttpMethod method, String url, Type type, HttpParams params, HttpHeaders headers) {
        return request(method, url, type, null, params, headers);
    }

    public static <T> Observable<T> request(HttpMethod method, String url, Class<T> clazz) {
        return request(method, url, clazz, null);
    }

    public static <T> Observable<T> request(HttpMethod method, String url, Class<T> clazz, HttpParams params) {
        return request(method, url, clazz, params, null);
    }

    public static <T> Observable<T> request(HttpMethod method, String url, Class<T> clazz, HttpParams params, HttpHeaders headers) {
        return request(method, url, null, clazz, params, headers);
    }

    /**
     * 这个封装其实没有必要，只是有些人喜欢这么干，我就多此一举写出来了。。
     * 这个封装其实没有必要，只是有些人喜欢这么干，我就多此一举写出来了。。
     * 这个封装其实没有必要，只是有些人喜欢这么干，我就多此一举写出来了。。
     */
    public static <T> Observable<T> request(HttpMethod method, String url, Type type, Class<T> clazz, HttpParams params, HttpHeaders headers) {
        Request<T, ? extends Request> request;
        if (method == HttpMethod.GET) request = OkGo.get(url);
        else if (method == HttpMethod.POST) request = OkGo.post(url);
        else if (method == HttpMethod.PUT) request = OkGo.put(url);
        else if (method == HttpMethod.DELETE) request = OkGo.delete(url);
        else if (method == HttpMethod.HEAD) request = OkGo.head(url);
        else if (method == HttpMethod.PATCH) request = OkGo.patch(url);
        else if (method == HttpMethod.OPTIONS) request = OkGo.options(url);
        else if (method == HttpMethod.TRACE) request = OkGo.trace(url);
        else request = OkGo.get(url);

        request.headers(headers);
        request.params(params);
        if (type != null) {
            request.converter(new JsonConvert<T>(type));
        } else if (clazz != null) {
            request.converter(new JsonConvert<T>(clazz));
        } else {
            request.converter(new JsonConvert<T>());
        }
        return request.adapt(new ObservableBody<T>());
    }
}
