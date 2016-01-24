/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lzy.library_xutilsdm;

public class HttpUtils {

//    public final static HttpCache sHttpCache = new HttpCache();
//
//    private final DefaultHttpClient httpClient;
//    private final HttpContext httpContext = new BasicHttpContext();
//
//    private HttpRedirectHandler httpRedirectHandler;
//
//    public HttpUtils() {
//        this(HttpUtils.DEFAULT_CONN_TIMEOUT, null);
//    }
//
//    public HttpUtils(int connTimeout) {
//        this(connTimeout, null);
//    }
//
//    public HttpUtils(String userAgent) {
//        this(HttpUtils.DEFAULT_CONN_TIMEOUT, userAgent);
//    }
//
//    public HttpUtils(int connTimeout, String userAgent) {
//        HttpParams params = new BasicHttpParams();
//
//        ConnManagerParams.setTimeout(params, connTimeout);
//        HttpConnectionParams.setSoTimeout(params, connTimeout);
//        HttpConnectionParams.setConnectionTimeout(params, connTimeout);
//
//        if (TextUtils.isEmpty(userAgent)) {
//            userAgent = OtherUtils.getUserAgent(null);
//        }
//        HttpProtocolParams.setUserAgent(params, userAgent);
//
//        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(10));
//        ConnManagerParams.setMaxTotalConnections(params, 10);
//
//        HttpConnectionParams.setTcpNoDelay(params, true);
//        HttpConnectionParams.setSocketBufferSize(params, 1024 * 8);
//        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
//
//        SchemeRegistry schemeRegistry = new SchemeRegistry();
//        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//        schemeRegistry.register(new Scheme("https", DefaultSSLSocketFactory.getSocketFactory(), 443));
//
//        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(params, schemeRegistry), params);
//
//        httpClient.setHttpRequestRetryHandler(new RetryHandler(DEFAULT_RETRY_TIMES));
//
//        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
//            @Override
//            public void process(org.apache.http.HttpRequest httpRequest, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
//                if (!httpRequest.containsHeader(HEADER_ACCEPT_ENCODING)) {
//                    httpRequest.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
//                }
//            }
//        });
//
//        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
//            @Override
//            public void process(HttpResponse response, HttpContext httpContext) throws org.apache.http.HttpException, IOException {
//                final HttpEntity entity = response.getEntity();
//                if (entity == null) {
//                    return;
//                }
//                final Header encoding = entity.getContentEncoding();
//                if (encoding != null) {
//                    for (HeaderElement element : encoding.getElements()) {
//                        if (element.getName().equalsIgnoreCase("gzip")) {
//                            response.setEntity(new GZipDecompressingEntity(response.getEntity()));
//                            return;
//                        }
//                    }
//                }
//            }
//        });
//    }
//
//    // ************************************    default settings & fields ****************************
//
//    private String responseTextCharset = HTTP.UTF_8;
//
//    private long currentRequestExpiry = HttpCache.getDefaultExpiryTime();
//
//    private final static int DEFAULT_CONN_TIMEOUT = 1000 * 15; // 15s
//
//    private final static int DEFAULT_RETRY_TIMES = 3;
//
//    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
//    private static final String ENCODING_GZIP = "gzip";
//
//    private final static int DEFAULT_POOL_SIZE = 3;
//    private final static PriorityExecutor EXECUTOR = new PriorityExecutor(DEFAULT_POOL_SIZE);
//
//    public HttpClient getHttpClient() {
//        return this.httpClient;
//    }
//
//    // ***************************************** config *******************************************
//
//    public HttpUtils configResponseTextCharset(String charSet) {
//        if (!TextUtils.isEmpty(charSet)) {
//            this.responseTextCharset = charSet;
//        }
//        return this;
//    }
//
//    public HttpUtils configHttpRedirectHandler(HttpRedirectHandler httpRedirectHandler) {
//        this.httpRedirectHandler = httpRedirectHandler;
//        return this;
//    }
//
//    public HttpUtils configHttpCacheSize(int httpCacheSize) {
//        sHttpCache.setCacheSize(httpCacheSize);
//        return this;
//    }
//
//    public HttpUtils configDefaultHttpCacheExpiry(long defaultExpiry) {
//        HttpCache.setDefaultExpiryTime(defaultExpiry);
//        currentRequestExpiry = HttpCache.getDefaultExpiryTime();
//        return this;
//    }
//
//    public HttpUtils configCurrentHttpCacheExpiry(long currRequestExpiry) {
//        this.currentRequestExpiry = currRequestExpiry;
//        return this;
//    }
//
//    public HttpUtils configCookieStore(CookieStore cookieStore) {
//        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
//        return this;
//    }
//
//    public HttpUtils configUserAgent(String userAgent) {
//        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
//        return this;
//    }
//
//    public HttpUtils configTimeout(int timeout) {
//        final HttpParams httpParams = this.httpClient.getParams();
//        ConnManagerParams.setTimeout(httpParams, timeout);
//        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
//        return this;
//    }
//
//    public HttpUtils configSoTimeout(int timeout) {
//        final HttpParams httpParams = this.httpClient.getParams();
//        HttpConnectionParams.setSoTimeout(httpParams, timeout);
//        return this;
//    }
//
//    public HttpUtils configRegisterScheme(Scheme scheme) {
//        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
//        return this;
//    }
//
//    public HttpUtils configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
//        Scheme scheme = new Scheme("https", sslSocketFactory, 443);
//        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
//        return this;
//    }
//
//    public HttpUtils configRequestRetryCount(int count) {
//        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
//        return this;
//    }
//
//    public HttpUtils configRequestThreadPoolSize(int threadPoolSize) {
//        HttpUtils.EXECUTOR.setPoolSize(threadPoolSize);
//        return this;
//    }
//
//    // ***************************************** send request *******************************************
//
//    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url,
//                                   RequestCallBack<T> callBack) {
//        return send(method, url, null, callBack);
//    }
//
//    public <T> HttpHandler<T> send(HttpRequest.HttpMethod method, String url, RequestParams params,
//                                   RequestCallBack<T> callBack) {
//        if (url == null) throw new IllegalArgumentException("url may not be null");
//
//        HttpRequest request = new HttpRequest(method, url);
//        return sendRequest(request, params, callBack);
//    }
//
//    public ResponseStream sendSync(HttpRequest.HttpMethod method, String url) throws HttpException {
//        return sendSync(method, url, null);
//    }
//
//    public ResponseStream sendSync(HttpRequest.HttpMethod method, String url, RequestParams params) throws HttpException {
//        if (url == null) throw new IllegalArgumentException("url may not be null");
//
//        HttpRequest request = new HttpRequest(method, url);
//        return sendSyncRequest(request, params);
//    }
//
//    // ***************************************** download *******************************************
//
//    public HttpHandler<File> download(String url, String target,
//                                      RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, null, false, false, callback);
//    }
//
//    public HttpHandler<File> download(String url, String target,
//                                      boolean autoResume, RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, false, callback);
//    }
//
//    public HttpHandler<File> download(String url, String target,
//                                      boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, null, autoResume, autoRename, callback);
//    }
//
//    public HttpHandler<File> download(String url, String target,
//                                      RequestParams params, RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, params, false, false, callback);
//    }
//
//    public HttpHandler<File> download(String url, String target,
//                                      RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, false, callback);
//    }
//
//    public HttpHandler<File> download(String url, String target,
//                                      RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
//        return download(HttpRequest.HttpMethod.GET, url, target, params, autoResume, autoRename, callback);
//    }
//
//    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target,
//                                      RequestParams params, RequestCallBack<File> callback) {
//        return download(method, url, target, params, false, false, callback);
//    }
//
//    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target,
//                                      RequestParams params, boolean autoResume, RequestCallBack<File> callback) {
//        return download(method, url, target, params, autoResume, false, callback);
//    }
//
//    public HttpHandler<File> download(HttpRequest.HttpMethod method, String url, String target,
//                                      RequestParams params, boolean autoResume, boolean autoRename, RequestCallBack<File> callback) {
//
//        if (url == null) throw new IllegalArgumentException("url may not be null");
//        if (target == null) throw new IllegalArgumentException("target may not be null");
//
//        HttpRequest request = new HttpRequest(method, url);
//
//        HttpHandler<File> handler = new HttpHandler<File>(httpClient, httpContext, responseTextCharset, callback);
//
//        handler.setExpiry(currentRequestExpiry);
//        handler.setHttpRedirectHandler(httpRedirectHandler);
//
//        if (params != null) {
//            request.setRequestParams(params, handler);
//            handler.setPriority(params.getPriority());
//        }
//        handler.executeOnExecutor(EXECUTOR, request, target, autoResume, autoRename);
//        return handler;
//    }
//
//    ////////////////////////////////////////////////////////////////////////////////////////////////
//    private <T> HttpHandler<T> sendRequest(HttpRequest request, RequestParams params, RequestCallBack<T> callBack) {
//
//        HttpHandler<T> handler = new HttpHandler<T>(httpClient, httpContext, responseTextCharset, callBack);
//
//        handler.setExpiry(currentRequestExpiry);
//        handler.setHttpRedirectHandler(httpRedirectHandler);
//        request.setRequestParams(params, handler);
//
//        if (params != null) {
//            handler.setPriority(params.getPriority());
//        }
//        handler.executeOnExecutor(EXECUTOR, request);
//        return handler;
//    }
//
//    private ResponseStream sendSyncRequest(HttpRequest request, RequestParams params) throws HttpException {
//
//        SyncHttpHandler handler = new SyncHttpHandler(httpClient, httpContext, responseTextCharset);
//
//        handler.setExpiry(currentRequestExpiry);
//        handler.setHttpRedirectHandler(httpRedirectHandler);
//        request.setRequestParams(params);
//
//        return handler.sendRequest(request);
//    }
}
