package com.lzy.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.R;

import java.util.concurrent.atomic.AtomicInteger;

public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
        Glide.with(activity)    //配置上下文
                .load("file://" + path)         //设置图片路径
                .error(R.mipmap.default_image)  //设置错误图片
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
