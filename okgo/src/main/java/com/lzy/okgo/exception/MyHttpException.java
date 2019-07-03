package com.lzy.okgo.exception;

public class MyHttpException extends RuntimeException {
    private int code;
    private String message;
    private String body;

    public MyHttpException(int code, String message, String body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
