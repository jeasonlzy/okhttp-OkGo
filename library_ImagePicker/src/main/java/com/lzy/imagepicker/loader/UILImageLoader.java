package com.lzy.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

public class UILImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
//        ImageSize size = new ImageSize(width, height);
//        ImageLoader.getInstance().displayImage("file://" + path, imageView, size);
    }

    @Override
    public void clearMemoryCache() {
    }
}
