package com.lzy.demo.utils;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class AnimHelper {

    private AnimHelper() {
        throw new RuntimeException("AnimHelper cannot be initialized!");
    }

    public static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    public static final int DURATION = 300;

    public static void scaleShow(View view, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static void scaleHide(View view, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(view).scaleX(0f).scaleY(0f).alpha(0f).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static void alphaShow(View view, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(view).alpha(1.0f).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static void alphaHide(View view, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(view).alpha(0f).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static void translateUp(View view, ViewPropertyAnimatorListener listener) {
        ViewCompat.animate(view).translationY(0).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static void translateDown(View view, ViewPropertyAnimatorListener listener) {
        int height = view.getHeight();
        ViewGroup.LayoutParams params = view.getLayoutParams();
        ViewGroup.MarginLayoutParams layoutParams = params instanceof ViewGroup.MarginLayoutParams ? ((ViewGroup.MarginLayoutParams) params) : null;
        if (layoutParams != null) height += layoutParams.bottomMargin;
        ViewCompat.animate(view).translationY(height).setDuration(DURATION).setListener(listener).setInterpolator(INTERPOLATOR).withLayer().start();
    }

    public static float floatEvaluator(float originalSize, float finalSize, float percent) {
        return (finalSize - originalSize) * percent + originalSize;
    }

    public static int argbEvaluator(int startColor, int endColor, float percent) {
        int startA = (startColor >> 24) & 0xff;
        int startR = (startColor >> 16) & 0xff;
        int startG = (startColor >> 8) & 0xff;
        int startB = startColor & 0xff;

        int endA = (endColor >> 24) & 0xff;
        int endR = (endColor >> 16) & 0xff;
        int endG = (endColor >> 8) & 0xff;
        int endB = endColor & 0xff;

        return ((startA + (int) (percent * (endA - startA))) << 24) |
                ((startR + (int) (percent * (endR - startR))) << 16) |
                ((startG + (int) (percent * (endG - startG))) << 8) |
                ((startB + (int) (percent * (endB - startB))));
    }

    public static float scaleEvaluator(float originalSize, float finalSize, float percent) {
        float calcSize = (finalSize - originalSize) * percent + originalSize;
        return calcSize / originalSize;
    }

    /** 获得状态栏的高度 */
    public static int getStatusBarHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}