package com.lzy.okhttputils.utils;

import android.text.TextUtils;

import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.Locale;
import java.util.StringTokenizer;

import okhttp3.Headers;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/8
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */
public class HeaderParser {
    /**
     * 根据请求结果生成对应的缓存实体类，以下为缓存相关的响应头
     * Cache-Control: public                             响应被缓存，并且在多用户间共享
     * Cache-Control: private                            响应只能作为私有缓存，不能在用户之间共享
     * Cache-Control: no-cache                           提醒浏览器要从服务器提取文档进行验证
     * Cache-Control: no-store                           绝对禁止缓存（用于机密，敏感文件）
     * Cache-Control: max-age=60                         60秒之后缓存过期（相对时间）,优先级比Expires高
     * Date: Mon, 19 Nov 2012 08:39:00 GMT               当前response发送的时间
     * Expires: Mon, 19 Nov 2012 08:40:01 GMT            缓存过期的时间（绝对时间）
     * Last-Modified: Mon, 19 Nov 2012 08:38:01 GMT      服务器端文件的最后修改时间
     * ETag: "20b1add7ec1cd1:0"                          服务器端文件的ETag值
     * 如果同时存在cache-control和Expires，浏览器总是优先使用cache-control
     *
     * @param responseHeaders 返回数据中的响应头
     * @param data            解析出来的数据
     * @param cacheKey        缓存的key
     * @param forceCache      是否强制缓存，如果为 true ,忽略304头信息
     * @return 缓存的实体类
     */
    public static <T> CacheEntity<T> parseCacheHeaders(Headers responseHeaders, T data, String cacheKey, boolean forceCache) {
        long date = HttpHeaders.getDate(responseHeaders.get(HttpHeaders.HEAD_KEY_DATE));
        long expires = HttpHeaders.getExpiration(responseHeaders.get(HttpHeaders.HEAD_KEY_EXPIRES));
        String cacheControl = HttpHeaders.getCacheControl(responseHeaders.get(HttpHeaders.HEAD_KEY_CACHE_CONTROL), responseHeaders.get(HttpHeaders.HEAD_KEY_PRAGMA));

        //没有缓存头控制，不需要缓存
        if (TextUtils.isEmpty(cacheControl) && expires <= 0 && !forceCache) return null;

        long maxAge = 0;
        if (!TextUtils.isEmpty(cacheControl)) {
            StringTokenizer tokens = new StringTokenizer(cacheControl, ",");
            while (tokens.hasMoreTokens()) {
                String token = tokens.nextToken().trim().toLowerCase(Locale.getDefault());
                if ((token.equals("no-cache") || token.equals("no-store")) && !forceCache) {
                    //服务器指定不缓存
                    return null;
                } else if (token.startsWith("max-age=")) {
                    try {
                        //获取最大缓存时间
                        maxAge = Long.parseLong(token.substring(8));
                        //服务器缓存设置立马过期，不缓存
                        if (maxAge <= 0 && !forceCache) return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        long localExpire = 0;   // 缓存相对于本地的到期时间
        long now;               //获取基准缓存时间，优先使用response中的date头，如果没有就使用本地时间
        if (date > 0) {
            now = date;
        } else {
            now = System.currentTimeMillis();
        }
        if (maxAge > 0) {
            // Http1.1 优先验证 Cache-Control 头
            localExpire = now + maxAge * 1000;
        } else if (expires >= 0) {
            // Http1.0 验证 Expires 头
            localExpire = expires;
        }

        //将response中所有的头存入 HttpHeaders，原因是写入数据库的对象需要实现序列化，而ok默认的Header没有序列化
        HttpHeaders headers = new HttpHeaders();
        for (String headerName : responseHeaders.names()) {
            headers.put(headerName, responseHeaders.get(headerName));
        }

        //构建缓存实体对象
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setKey(cacheKey);
        cacheEntity.setData(data);
        cacheEntity.setLocalExpire(localExpire);
        cacheEntity.setResponseHeaders(headers);
        return cacheEntity;
    }

    /**
     * 对每个请求添加默认的请求头，如果有缓存，并返回缓存实体对象
     * Cache-Control: max-age=0                            以秒为单位
     * If-Modified-Since: Mon, 19 Nov 2012 08:38:01 GMT    缓存文件的最后修改时间。
     * If-None-Match: "0693f67a67cc1:0"                    缓存文件的ETag值
     * Cache-Control: no-cache                             不使用缓存
     * Pragma: no-cache                                    不使用缓存
     * Accept-Language: zh-CN,zh;q=0.8                     支持的语言
     * User-Agent:                                         用户代理，它的信息包括硬件平台、系统软件、应用软件和用户个人偏好
     * Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36
     *
     * @param request     请求类
     * @param cacheEntity 缓存实体类
     * @param cacheMode   缓存模式
     */
    public static <T> void addDefaultHeaders(BaseRequest request, CacheEntity<T> cacheEntity, CacheMode cacheMode) {
        //1. 按照标准的 http 协议，添加304相关响应头
        if (cacheEntity == null || cacheMode != CacheMode.DEFAULT) {
            //缓存不存在，或者缓存模式不是标准的304，则移除相关的缓存头，避免服务器返回304
            request.removeHeader(HttpHeaders.HEAD_KEY_IF_NONE_MATCH);
            request.removeHeader(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE);
        } else if (cacheEntity.getLocalExpire() < System.currentTimeMillis()) {
            //缓存已经过期
            HttpHeaders responseHeaders = cacheEntity.getResponseHeaders();
            String eTag = responseHeaders.get(HttpHeaders.HEAD_KEY_E_TAG);
            if (eTag != null) request.headers(HttpHeaders.HEAD_KEY_IF_NONE_MATCH, eTag);
            long lastModified = HttpHeaders.getLastModified(responseHeaders.get(HttpHeaders.HEAD_KEY_LAST_MODIFIED));
            if (lastModified > 0)
                request.headers(HttpHeaders.HEAD_KEY_IF_MODIFIED_SINCE, HttpHeaders.formatMillisToGMT(lastModified));
        }

        // 2. 添加 Accept-Language
        String acceptLanguage = HttpHeaders.getAcceptLanguage();
        if (!TextUtils.isEmpty(acceptLanguage)) request.headers(HttpHeaders.HEAD_KEY_ACCEPT_LANGUAGE, acceptLanguage);

        // 3. 添加 UserAgent
        String userAgent = HttpHeaders.getUserAgent();
        if (!TextUtils.isEmpty(userAgent)) request.headers(HttpHeaders.HEAD_KEY_USER_AGENT, userAgent);
    }
}
