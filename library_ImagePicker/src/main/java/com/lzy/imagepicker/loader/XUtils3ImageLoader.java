package com.lzy.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

public class XUtils3ImageLoader implements ImageLoader {
    @Override
    public void displayImage(Activity activity, String path, ImageView imageView, int width, int height) {
//        ImageOptions options = new ImageOptions.Builder()
//                .setLoadingDrawableId(R.mipmap.default_image)
//                .setFailureDrawableId(R.mipmap.default_image)
//                .setConfig(Bitmap.Config.RGB_565)
//                .setSize(width, height).setCrop(true)
//                .setUseMemCache(false).build();
//        x.image().bind(imageView, "file://" + path, options);
    }

    @Override
    public void clearMemoryCache() {
    }
}
