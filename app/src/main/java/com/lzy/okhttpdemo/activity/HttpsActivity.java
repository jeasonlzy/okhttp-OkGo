package com.lzy.okhttpdemo.activity;

import android.os.Bundle;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttputils.OkHttpUtils;

import butterknife.ButterKnife;

public class HttpsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(7)[0]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
