package com.lzy.imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

import java.io.Serializable;

/** ImageLoader抽象类，外部需要实现这个类去加载图片， 尽力减少对第三方库的依赖，所以这么干了 */
public interface ImageLoader extends Serializable {

    void displayImage(Activity activity, String path, ImageView imageView, int width, int height);

    void clearMemoryCache();
}
