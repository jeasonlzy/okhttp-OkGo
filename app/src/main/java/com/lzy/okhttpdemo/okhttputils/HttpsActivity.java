package com.lzy.okhttpdemo.okhttputils;

import android.os.Bundle;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseDetailActivity;
import com.lzy.okhttpdemo.callback.StringDialogCallback;
import com.lzy.okhttputils.OkHttpUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

public class HttpsActivity extends BaseDetailActivity {

    private static final String CER_12306 = "-----BEGIN CERTIFICATE-----\n" +
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
        setTitle("支持https请求");
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
                .execute(new StringDialogCallback(this) {
                    @Override
                    public void onSuccess(String data, Call call, Response response) {
                        handleResponse(data, call, response);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        handleError(call, response);
                    }
                });
    }

    @OnClick(R.id.btn_https_request)
    public void btn_https_request(View view) {
        try {

            //证书可以在全局初始化的时候设置,不用每次请求设置一遍
            OkHttpUtils.get("https://kyfw.12306.cn/otn")//
                    .tag(this)//
                    .headers("Connection", "close")           //如果对于部分自签名的https访问不成功，需要加上该控制头
                    .headers("header1", "headerValue1")//
                    .params("param1", "paramValue1")//
//                    .setCertificates(new Buffer().writeUtf8(CER_12306).inputStream())  //方法一：设置自签名网站的证书（选一种即可）
                    .setCertificates(getAssets().open("srca.cer"))                     //方法二：也可以设置https证书（选一种即可）
//                    .setCertificates()                                                 //方法三：信任所有证书（选一种即可）

                    //方法四：传入bks证书,密码,和cer证书,支持双向加密
//                    .setCertificates(getAssets().open("aaaa.bks"), "123456", getAssets().open("srca.cer"))
                    .execute(new StringDialogCallback(this) {
                        @Override
                        public void onSuccess(String data, Call call, Response response) {
                            handleResponse(data, call, response);
                        }

                        @Override
                        public void onError(Call call, Response response, Exception e) {
                            super.onError(call, response, e);
                            handleError(call, response);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}