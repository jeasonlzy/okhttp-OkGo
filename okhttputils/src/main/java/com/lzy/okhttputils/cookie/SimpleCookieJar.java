package com.lzy.okhttputils.cookie;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/** cookie的自动化管理 */
public final class SimpleCookieJar implements CookieJar {

    private final List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        allCookies.addAll(cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies) {
            if (cookie.matches(url)) {
                result.add(cookie);
            }
        }
        return result;
    }
}
