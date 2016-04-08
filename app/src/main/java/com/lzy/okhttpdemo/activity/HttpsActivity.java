package com.lzy.okhttpdemo.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.StringDialogCallback;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttputils.OkHttpUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class HttpsActivity extends BaseActivity {

    public static final String CER_12306 = "-----BEGIN CERTIFICATE-----\n" +
            "MIICmjCCAgOgAwIBAgIIbyZr5/jKH6QwDQYJKoZIhvcNAQEFBQAwRzELMAkGA1UEBhMCQ04xKTAn\n" +
            "BgNVBAoTIFNpbm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMB4X\n" +
            "DTA5MDUyNTA2NTYwMFoXDTI5MDUyMDA2NTYwMFowRzELMAkGA1UEBhMCQ04xKTAnBgNVBAoTIFNp\n" +
            "bm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMIGfMA0GCSqGSIb3\n" +
            "DQEBAQUAA4GNADCBiQKBgQDMpbNeb34p0GvLkZ6t72/OOba4mX2K/eZRWFfnuk8e5jKDH+9BgCb2\n" +
            "9bSotqPqTbxXWPxIOz8EjyUO3bfR5pQ8ovNTOlks2rS5BdMhoi4sUjCKi5ELiqtyww/XgY5iFqv6\n" +
            "D4Pw9QvOUcdRVSbPWo1DwMmH75It6pk/rARIFHEjWwIDAQABo4GOMIGLMB8GA1UdIwQYMBaAFHle\n" +
            "tne34lKDQ+3HUYhMY4UsAENYMAwGA1UdEwQFMAMBAf8wLgYDVR0fBCcwJTAjoCGgH4YdaHR0cDov\n" +
            "LzE5Mi4xNjguOS4xNDkvY3JsMS5jcmwwCwYDVR0PBAQDAgH+MB0GA1UdDgQWBBR5XrZ3t+JSg0Pt\n" +
            "x1GITGOFLABDWDANBgkqhkiG9w0BAQUFAAOBgQDGrAm2U/of1LbOnG2bnnQtgcVaBXiVJF8LKPaV\n" +
            "23XQ96HU8xfgSZMJS6U00WHAI7zp0q208RSUft9wDq9ee///VOhzR6Tebg9QfyPSohkBrhXQenvQ\n" +
            "og555S+C3eJAAVeNCTeMS3N/M5hzBRJAoffn3qoYdAO1Q8bTguOi+2849A==\n" +
            "-----END CERTIFICATE-----";

    @Override
    protected void onActivityCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_https);
        ButterKnife.bind(this);
        setTitle(Constant.getData().get(7)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }

    @OnClick(R.id.btn_none_https_request)
    public void btn_none_https_request(View view) {
        OkHttpUtils.get("https://github.com/jeasonlzy0216")//
                .tag(this)//
                .headers("header1", "headerValue1")//
                .params("param1", "paramValue1")//
                .execute(new HttpsCallBack(this));
    }

    @OnClick(R.id.btn_https_request)
    public void btn_https_request(View view) {
        try {
            OkHttpUtils.get("https://kyfw.12306.cn/otn")//
                    .tag(this)//
                    .headers("header1", "headerValue1")//
                    .params("param1", "paramValue1")//
//                    .setCertificates(new Buffer().writeUtf8(CER_12306).inputStream())  //设置自签名网站的证书
                    .setCertificates(getAssets().open("srca.cers"))//这种方式也可以设置https证书
                    .execute(new HttpsCallBack(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class HttpsCallBack extends StringDialogCallback {

        public HttpsCallBack(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String data, Request request, Response response) {
            handleResponse(isFromCache, data, request, response);
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            super.onError(isFromCache, call, response, e);
            handleError(isFromCache, call, response);
            System.out.println(call.request().url());
        }
    }
}
