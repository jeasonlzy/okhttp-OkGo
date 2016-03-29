package com.lzy.okhttputils;

import android.util.Log;

/** 日志工具类 */
public class L {
    public static boolean debug = false;
    public static String tag = "OkHttpUtils";

    public static void v(String msg) {
        if (debug) Log.v(tag, msg);
    }

    public static void d(String msg) {
        if (debug) Log.d(tag, msg);
    }

    public static void i(String msg) {
        if (debug) Log.i(tag, msg);
    }

    public static void w(String msg) {
        if (debug) Log.w(tag, msg);
    }

    public static void e(String msg) {
        if (debug) Log.e(tag, msg);
    }
}

