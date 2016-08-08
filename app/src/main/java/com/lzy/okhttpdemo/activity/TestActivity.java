package com.lzy.okhttpdemo.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;
import com.lzy.okhttputils.callback.BitmapCallback;
import com.lzy.okhttputils.callback.StringCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class TestActivity extends AppCompatActivity {

    @Bind(R.id.image) ImageView imageView;
    @Bind(R.id.edit) EditText editText;
    private String __VIEWSTATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn1)
    public void btn1(View view) {
        OkHttpUtils.post("http://jwgl.gdut.edu.cn/CheckCode.aspx")//
                .tag(this)//
//                .headers("Refer", "http://jwgl.gdut.edu.cn/default2.aspx")//
                .execute(new BitmapCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, Bitmap bitmap, Request request, @Nullable Response response) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
    }

    @OnClick(R.id.btn2)
    public void btn2(View view) {
        String txtSecretCode = editText.getText().toString();
        OkHttpUtils.post("http://jwgl.gdut.edu.cn/default2.aspx")//
                .tag(this)//
//                .headers("Refer", "http://jwgl.gdut.edu.cn/default2.aspx")//
                .params("__VIEWSTATE", __VIEWSTATE)//
                .params("txtUserName", "3115005074")//
                .params("TextBox2", "sea8689030")//
                .params("txtSecretCode", txtSecretCode)//
                .params("RadioButtonList1", "学生")//
                .params("Button1", "")//
                .params("lbLanguage", "")//
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
                        System.out.println("onResponse");
                    }
                });
    }

    @OnClick(R.id.btn3)
    public void btn3(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建Doc并获取网页数据
                Document doc = null;
                try {
                    doc = Jsoup.connect("http://jwgl.gdut.edu.cn/default2.aspx").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //解析
                for (Element element : doc.getElementsByTag("input")) {
                    if (element.attr("value").length() == 48) {
                        __VIEWSTATE = element.attr("value");
                        System.out.println("__VIEWSTATE:" + __VIEWSTATE);
                    }
                }
            }
        }).start();
    }
}
