package com.lzy.okhttputils.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    @Override
    public void add(HttpUrl url, List<Cookie> cookies) {
        List<Cookie> oldCookies = allCookies.get(url.host());
        List<Cookie> needRemove = new ArrayList<>();

        for (Cookie newCookie : cookies) {
            for (Cookie oldCookie : oldCookies) {
                if (newCookie.name().equals(oldCookie.name())) {
                    needRemove.add(oldCookie);
                }
            }
        }
        oldCookies.removeAll(needRemove);
        oldCookies.addAll(cookies);

    }

    @Override
    public List<Cookie> get(HttpUrl uri) {
        List<Cookie> cookies = allCookies.get(uri.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(uri.host(), cookies);
        }
        return cookies;

    }

    @Override
    public boolean removeAll() {
        allCookies.clear();
        return true;
    }

    @Override
    public List<Cookie> getCookies() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = allCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(allCookies.get(url));
        }
        return cookies;
    }

    @Override
    public boolean remove(HttpUrl uri, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(uri.host());
        return (cookie != null) && cookies.remove(cookie);
    }
}
