package com.lzy.okhttpdemo.activity;

import android.os.Bundle;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.utils.Constant;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheEntity;
import com.lzy.okhttputils.cache.CacheManager;

import java.util.List;

import butterknife.ButterKnife;

public class CacheActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache);
        ButterKnife.bind(this);

        if (actionBar != null) actionBar.setTitle(Constant.getData().get(6)[0]);

        List<CacheEntity<Object>> all = CacheManager.INSTANCE.getAll();
        for (CacheEntity<Object> cacheEntity : all) {
            System.out.println(cacheEntity);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Activity销毁时，取消网络请求
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
