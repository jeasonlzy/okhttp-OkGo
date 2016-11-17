package com.lzy.okgo.cookie;

import com.lzy.okgo.cookie.store.CookieStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/12
 * 描    述：CookieJar的实现类，默认管理了用户自己维护的cookie
 * 修订历史：
 * ================================================
 */
public class CookieJarImpl implements CookieJar {

    private CookieStore cookieStore;
    private Map<String, Set<Cookie>> userCookies = new HashMap<>();  //用户手动添加的Cookie

    public void addCookie(List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            addCookie(cookie);
        }
    }

    public void addCookie(Cookie cookie) {
        String domain = cookie.domain();
        Set<Cookie> domainCookies = userCookies.get(domain);
        if (domainCookies == null) {
            domainCookies = new HashSet<>();
            userCookies.put(domain, domainCookies);
        }
        domainCookies.add(cookie);
    }

    public CookieJarImpl(CookieStore cookieStore) {
        if (cookieStore == null) {
            throw new IllegalArgumentException("cookieStore can not be null!");
        }
        this.cookieStore = cookieStore;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        cookieStore.saveCookies(url, cookies);
    }

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> requestUrlCookies = cookieStore.loadCookies(url);
        Set<Cookie> userUrlCookies = userCookies.get(url.host());
        Set<Cookie> cookieSet = new HashSet<>();
        if (requestUrlCookies != null) cookieSet.addAll(requestUrlCookies);
        if (userUrlCookies != null) cookieSet.addAll(userUrlCookies);
        return new ArrayList<>(cookieSet);
    }

    public CookieStore getCookieStore() {
        return cookieStore;
    }
}
