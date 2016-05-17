package com.lzy.okhttputils.cookie.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：Cookie 的内存管理
 * 修订历史：
 * ================================================
 */
public class MemoryCookieStore implements CookieStore {

    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    @Override
    public void saveCookies(HttpUrl url, List<Cookie> cookies) {
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
    public List<Cookie> loadCookies(HttpUrl url) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookies == null) {
            cookies = new ArrayList<>();
            allCookies.put(url.host(), cookies);
        }
        return cookies;
    }

    @Override
    public List<Cookie> getAllCookie() {
        List<Cookie> cookies = new ArrayList<>();
        Set<String> httpUrls = allCookies.keySet();
        for (String url : httpUrls) {
            cookies.addAll(allCookies.get(url));
        }
        return cookies;
    }

    @Override
    public boolean removeCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(url.host());
        return (cookie != null) && cookies.remove(cookie);
    }

    @Override
    public boolean removeCookies(HttpUrl url) {
        return allCookies.remove(url.host()) != null;
    }

    @Override
    public boolean removeAllCookie() {
        allCookies.clear();
        return true;
    }
}
