package com.lzy.okhttputils.cookie;

import com.lzy.okhttputils.cookie.store.CookieStore;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

public class CookieJarImpl implements CookieJar {

    private CookieStore cookieStore;
    private List<Cookie> userCookies = new ArrayList<>();   //用户手动添加的Cookie

    public void addCookies(List<Cookie> cookies) {
        userCookies.addAll(cookies);
    }

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.saveCookies(url, cookies);
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookies = cookieStore.loadCookies(url);
        cookies.addAll(userCookies);
        return cookies;
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
