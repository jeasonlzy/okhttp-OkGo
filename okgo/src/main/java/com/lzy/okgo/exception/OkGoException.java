package com.lzy.okgo.exception;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OkGoException extends Exception {

    public static OkGoException INSTANCE(String msg) {
        return new OkGoException(msg);
    }

    public OkGoException() {
        super();
    }

    public OkGoException(String detailMessage) {
        super(detailMessage);
    }

    public OkGoException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public OkGoException(Throwable throwable) {
        super(throwable);
    }
}