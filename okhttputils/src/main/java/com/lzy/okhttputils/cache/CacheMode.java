package com.lzy.okhttputils.cache;

public enum CacheMode {
    /** 按照HTTP协议的默认缓存规则，例如有304响应头时缓存 */
    DEFAULT,

    /** 请求网络失败后，从缓存中取出缓存数据 */
    REQUEST_FAILED_READ_CACHE,

    /** 只允许从缓存读取 */
    ONLY_READ_CACHE,

    /** 如果缓存不存在才请求服务器，否则使用缓存 */
    IF_NONE_CACHE_REQUEST
}
