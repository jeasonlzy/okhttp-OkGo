package com.lzy.okhttputils.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <pre>
 *     OkHttpClient client = new OkHttpClient.Builder()
 *             .cookieJar(new JavaNetCookieJar(new CookieManager(
 *                     new PersistentCookieStore(getApplicationContext()),
 *                             CookiePolicy.ACCEPT_ALL))
 *             .build();
 * </pre>
 * from http://stackoverflow.com/questions/25461792/persistent-cookie-store-using-okhttp-2-on-android
 * <br/>
 * A persistent cookie store which implements the Apache HttpClient CookieStore interface.
 * Cookies are stored and will persist on the user's device between application sessions since they
 * are serialized and stored in SharedPreferences. Instances of this class are
 * designed to be used with AsyncHttpClient#setCookieStore, but can also be used with a
 * regular old apache HttpClient/HttpContext if you prefer.
 */
public class PersistentCookieStore implements CookieStore {

    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String COOKIE_NAME_PREFIX = "cookie_";

    private final HashMap<String, ConcurrentHashMap<String, HttpCookie>> cookies;
    private final SharedPreferences cookiePrefs;

    /**
     * Construct a persistent cookie store.
     *
     * @param context Context to attach cookie store to
     */
    public PersistentCookieStore(Context context) {
        cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        cookies = new HashMap<String, ConcurrentHashMap<String, HttpCookie>>();

        // Load any previously stored cookies into the store
        Map<String, ?> prefsMap = cookiePrefs.getAll();
        for (Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if (((String) entry.getValue()) != null && !((String) entry.getValue()).startsWith(COOKIE_NAME_PREFIX)) {
                String[] cookieNames = TextUtils.split((String) entry.getValue(), ",");
                for (String name : cookieNames) {
                    String encodedCookie = cookiePrefs.getString(COOKIE_NAME_PREFIX + name, null);
                    if (encodedCookie != null) {
                        HttpCookie decodedCookie = decodeCookie(encodedCookie);
                        if (decodedCookie != null) {
                            if (!cookies.containsKey(entry.getKey()))
                                cookies.put(entry.getKey(), new ConcurrentHashMap<String, HttpCookie>());
                            cookies.get(entry.getKey()).put(name, decodedCookie);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);

        // Save cookie into local store, or remove if expired
        if (!cookie.hasExpired()) {
            if (!cookies.containsKey(uri.getHost()))
                cookies.put(uri.getHost(), new ConcurrentHashMap<String, HttpCookie>());
            cookies.get(uri.getHost()).put(name, cookie);
        } else {
            if (cookies.containsKey(uri.toString())) cookies.get(uri.getHost()).remove(name);
        }

        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.putString(uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableHttpCookie(cookie)));
        prefsWriter.commit();
    }

    protected String getCookieToken(URI uri, HttpCookie cookie) {
        return cookie.getName() + cookie.getDomain();
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        if (cookies.containsKey(uri.getHost())) ret.addAll(cookies.get(uri.getHost()).values());
        return ret;
    }

    @Override
    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.commit();
        cookies.clear();
        return true;
    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        String name = getCookieToken(uri, cookie);

        if (cookies.containsKey(uri.getHost()) && cookies.get(uri.getHost()).containsKey(name)) {
            cookies.get(uri.getHost()).remove(name);

            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
            if (cookiePrefs.contains(COOKIE_NAME_PREFIX + name)) {
                prefsWriter.remove(COOKIE_NAME_PREFIX + name);
            }
            prefsWriter.putString(uri.getHost(), TextUtils.join(",", cookies.get(uri.getHost()).keySet()));
            prefsWriter.commit();

            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<HttpCookie> getCookies() {
        ArrayList<HttpCookie> ret = new ArrayList<HttpCookie>();
        for (String key : cookies.keySet())
            ret.addAll(cookies.get(key).values());

        return ret;
    }

    @Override
    public List<URI> getURIs() {
        ArrayList<URI> ret = new ArrayList<URI>();
        for (String key : cookies.keySet())
            try {
                ret.add(new URI(key));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        return ret;
    }

    /**
     * Serializes Cookie object into String
     *
     * @param cookie cookie to be encoded, can be null
     * @return cookie encoded as String
     */
    protected String encodeCookie(SerializableHttpCookie cookie) {
        if (cookie == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    /**
     * Returns cookie decoded from cookie string
     *
     * @param cookieString string of cookie as returned from http request
     * @return decoded cookie or null if exception occured
     */
    protected HttpCookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        HttpCookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableHttpCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }

        return cookie;
    }

    /**
     * Using some super basic byte array &lt;-&gt; hex conversions so we don't have to rely on any
     * large Base64 libraries. Can be overridden if you like!
     *
     * @param bytes byte array to be converted
     * @return string containing hex values
     */
    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    /**
     * Converts hex values from strings to byte arra
     *
     * @param hexString string of hex-encoded values
     * @return decoded byte array
     */
    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}