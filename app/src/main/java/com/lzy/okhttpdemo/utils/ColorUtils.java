package com.lzy.okhttpdemo.utils;/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2015/8/4
 * 描    述：
 * 修订历史：
 * ================================================
 */

import android.graphics.Color;

import java.util.Random;

public class ColorUtils {

    public static int randomColor() {
        Random random = new Random();
        int red = random.nextInt(150) + 50;
        int green = random.nextInt(150) + 50;
        int blue = random.nextInt(150) + 50;
        return Color.rgb(red, green, blue);
    }
}
