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
package com.lzy.okserver.download.db;

import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.HttpRequest;
import com.lzy.okgo.request.DeleteRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.HeadRequest;
import com.lzy.okgo.request.OptionsRequest;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.PutRequest;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/8
 * 描    述：与BaseRequest对应,主要是为了序列化
 * 修订历史：
 * ================================================
 */
public class DownloadRequest implements Serializable {

    private static final long serialVersionUID = -6883956320373276785L;

    public String method;
    public String url;
    public CacheMode cacheMode;
    public String cacheKey;
    public long cacheTime;
    public HttpParams params;
    public HttpHeaders headers;

    public static String getMethod(HttpRequest request) {
        if (request instanceof GetRequest) return "query";
        if (request instanceof PostRequest) return "post";
        if (request instanceof PutRequest) return "put";
        if (request instanceof DeleteRequest) return "delete";
        if (request instanceof OptionsRequest) return "options";
        if (request instanceof HeadRequest) return "head";
        return "";
    }

    public static HttpRequest createRequest(String url, String method) {
        if (method.equals("query")) return new GetRequest(url);
        if (method.equals("post")) return new PostRequest(url);
        if (method.equals("put")) return new PutRequest(url);
        if (method.equals("delete")) return new DeleteRequest(url);
        if (method.equals("options")) return new OptionsRequest(url);
        if (method.equals("head")) return new HeadRequest(url);
        return null;
    }
}
