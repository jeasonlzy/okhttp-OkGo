/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.demo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;

import com.lzy.demo.R;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
@SuppressWarnings("unused")
public class SimpleViewBehavior extends CoordinatorLayout.Behavior<View> {

    private static final int UNSPECIFIED_INT = Integer.MAX_VALUE;
    private static final float UNSPECIFIED_FLOAT = Float.MAX_VALUE;

    private static final int DEPEND_TYPE_HEIGHT = 0;
    private static final int DEPEND_TYPE_WIDTH = 1;
    private static final int DEPEND_TYPE_X = 2;
    private static final int DEPEND_TYPE_Y = 3;

    private int mDependViewId = 0;              //默认没有依赖对象
    private int mDependType = DEPEND_TYPE_Y;    //默认按照y方向变化
    private int mDependTargetX;                 //X方向的允许最大距离(影响动画percent)
    private int mDependTargetY;                 //Y方向的允许最大距离(影响动画percent)
    private int mDependTargetWidth;             //依赖控件起始最大宽度(影响动画percent)
    private int mDependTargetHeight;            //依赖控件起始最大高度(影响动画percent)
    private int targetX;
    private int targetY;
    private int targetWidth;
    private int targetHeight;
    private int targetBackgroundColor;
    private float targetAlpha;
    private float targetRotateX;
    private float targetRotateY;
    private int mAnimationId = 0;               //自定义动画id(xml文件定义动画)

    private int mDependStartX;
    private int mDependStartY;
    private int mDependStartWidth;
    private int mDependStartHeight;
    private int mStartX;
    private int mStartY;
    private int mStartWidth;
    private int mStartHeight;
    private int mStartBackgroundColor;
    private float mStartAlpha;
    private float mStartRotateX;
    private float mStartRotateY;

    private Animation mAnimation;
    private boolean isPrepared;

    public SimpleViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SimpleViewBehavior);
        mDependViewId = a.getResourceId(R.styleable.SimpleViewBehavior_svb_dependOn, mDependViewId);
        mDependType = a.getInt(R.styleable.SimpleViewBehavior_svb_dependType, mDependType);
        mDependTargetX = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_dependTargetX, UNSPECIFIED_INT);
        mDependTargetY = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_dependTargetY, UNSPECIFIED_INT);
        mDependTargetWidth = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_dependTargetWidth, UNSPECIFIED_INT);
        mDependTargetHeight = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_dependTargetHeight, UNSPECIFIED_INT);
        targetX = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_targetX, UNSPECIFIED_INT);
        targetY = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_targetY, UNSPECIFIED_INT);
        targetWidth = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_targetWidth, UNSPECIFIED_INT);
        targetHeight = a.getDimensionPixelOffset(R.styleable.SimpleViewBehavior_svb_targetHeight, UNSPECIFIED_INT);
        targetBackgroundColor = a.getColor(R.styleable.SimpleViewBehavior_svb_targetBackgroundColor, UNSPECIFIED_INT);
        targetAlpha = a.getFloat(R.styleable.SimpleViewBehavior_svb_targetAlpha, UNSPECIFIED_FLOAT);
        targetRotateX = a.getFloat(R.styleable.SimpleViewBehavior_svb_targetRotateX, UNSPECIFIED_FLOAT);
        targetRotateY = a.getFloat(R.styleable.SimpleViewBehavior_svb_targetRotateY, UNSPECIFIED_FLOAT);
        mAnimationId = a.getResourceId(R.styleable.SimpleViewBehavior_svb_animation, mAnimationId);
        a.recycle();
    }

    /** 初始化数据 */
    private void prepare(CoordinatorLayout parent, View child, View dependency) {
        mDependStartX = (int) dependency.getX();
        mDependStartY = (int) dependency.getY();
        mDependStartWidth = dependency.getWidth();
        mDependStartHeight = dependency.getHeight();
        mStartX = (int) child.getX();
        mStartY = (int) child.getY();
        mStartWidth = child.getWidth();
        mStartHeight = child.getHeight();
        mStartAlpha = child.getAlpha();
        mStartRotateX = child.getRotationX();
        mStartRotateY = child.getRotationY();

        //特殊处理y方向变化
        if (mDependTargetY == UNSPECIFIED_INT && dependency instanceof AppBarLayout) {
            mDependTargetY = ((AppBarLayout) dependency).getTotalScrollRange();
        }
        // 背景颜色渐变
        if (child.getBackground() instanceof ColorDrawable) mStartBackgroundColor = ((ColorDrawable) child.getBackground()).getColor();
        // 自定义动画
        if (mAnimationId != 0) {
            mAnimation = AnimationUtils.loadAnimation(child.getContext(), mAnimationId);
            mAnimation.initialize(child.getWidth(), child.getHeight(), parent.getWidth(), parent.getHeight());
        }
        // 兼容5.0以上的沉浸模式
        if (Build.VERSION.SDK_INT > 16 && parent.getFitsSystemWindows() && targetY != UNSPECIFIED_INT) {
            targetY += getStatusBarHeight(parent.getContext());
        }
        isPrepared = true;
    }

    /**
     * child 是指应用behavior的View ，dependency 担任触发behavior的角色，并与child进行互动。
     * layoutDependsOn方法在每次layout发生变化时都会调用，我们需要在dependency控件发生变化时返回True，
     * 在我们的例子中是用户在屏幕上滑动时（因为AppBarLayout发生了移动），然后我们需要让child做出相应的反应。
     */
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency.getId() == mDependViewId;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        // 该方法会在滑动的时候一直回调,但只需要初始化一次
        if (!isPrepared) prepare(parent, child, dependency);
        updateView(child, dependency);
        return false;
    }

    /**
     * 这个是CoordinatorLayout在进行measure的过程中，利用Behavior对象对子view进行大小测量的一个方法。
     * 在这个方法内，我们可以通过parent.getDependencies(child);这个方法，获取到这个child依赖的view，然后通过获取这个child依赖的view的大小来决定自身的大小。
     */
    @Override
    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        return super.onMeasureChild(parent, child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    /**
     * 这个方法是用来子view用来布局自身使用，如果依赖其他view，那么系统会首先调用
     * public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency)
     * 这个方法，可以在这个回调中记录dependency的一些位置信息，在onLayoutChild中利用保存下来的信息进行计算，然后得到自身的具体位置。
     */
    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        boolean bool = super.onLayoutChild(parent, child, layoutDirection);
        if (isPrepared) updateView(child, parent.getDependencies(child).get(0));
        return bool;
    }

    public void updateView(View child, View dependency) {
        float percent = 0;
        float start = 0;
        float current = 0;
        float end = UNSPECIFIED_INT;
        switch (mDependType) {
            case DEPEND_TYPE_WIDTH:
                start = mDependStartWidth;
                current = dependency.getWidth();
                end = mDependTargetWidth;
                break;
            case DEPEND_TYPE_HEIGHT:
                start = mDependStartHeight;
                current = dependency.getHeight();
                end = mDependTargetHeight;
                break;
            case DEPEND_TYPE_X:
                start = mDependStartX;
                current = dependency.getX();
                end = mDependTargetX;
                break;
            case DEPEND_TYPE_Y:
                start = mDependStartY;
                current = dependency.getY();
                end = mDependTargetY;
                break;
        }
        if (end != UNSPECIFIED_INT) {
            percent = Math.abs(current - start) / Math.abs(end - start);
        }
        updateViewWithPercent(child, percent > 1 ? 1 : percent);
    }

    /** 更新View */
    public void updateViewWithPercent(View child, float percent) {
        if (mAnimation == null) {
            //如果没有自定义动画,那么使用属性动画
            float newX = targetX == UNSPECIFIED_INT ? 0 : (targetX - mStartX) * percent;
            float newY = targetY == UNSPECIFIED_INT ? 0 : (targetY - mStartY) * percent;
            //缩放动画
            if (targetWidth != UNSPECIFIED_INT || targetHeight != UNSPECIFIED_INT) {
                child.setScaleX(scaleEvaluator(mStartWidth, targetWidth, percent));
                child.setScaleY(scaleEvaluator(mStartHeight, targetHeight, percent));
                float newWidth = floatEvaluator(mStartWidth, targetWidth, percent);
                float newHeight = floatEvaluator(mStartWidth, targetWidth, percent);
                newX -= (mStartWidth - newWidth) / 2;
                newY -= (mStartHeight - newHeight) / 2;
            }
            //平移动画
            child.setTranslationX(newX);
            child.setTranslationY(newY);
            //透明度变化
            if (targetAlpha != UNSPECIFIED_FLOAT) child.setAlpha(floatEvaluator(mStartAlpha, targetAlpha, percent));
            //背景渐变
            if (targetBackgroundColor != UNSPECIFIED_INT && mStartBackgroundColor != 0) {
                child.setBackgroundColor(argbEvaluator(mStartBackgroundColor, targetBackgroundColor, percent));
            }
            //旋转动画
            if (targetRotateX != UNSPECIFIED_FLOAT) child.setRotationX(floatEvaluator(mStartRotateX, targetRotateX, percent));
            if (targetRotateY != UNSPECIFIED_FLOAT) child.setRotationY(floatEvaluator(mStartRotateY, targetRotateY, percent));
        } else {
            mAnimation.setStartTime(0);
            mAnimation.restrictDuration(100);
            Transformation transformation = new Transformation();
            mAnimation.getTransformation((long) (percent * 100), transformation);
            BehaviorAnimation animation = new BehaviorAnimation(transformation);
            child.startAnimation(animation);
        }
        child.requestLayout();
    }

    private static class BehaviorAnimation extends Animation {

        private Transformation mTransformation;

        public BehaviorAnimation(Transformation transformation) {
            mTransformation = transformation;
            setDuration(0);
            setFillAfter(true);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            t.compose(mTransformation);
            super.applyTransformation(interpolatedTime, t);
        }
    }

    public static float floatEvaluator(float originalSize, float finalSize, float percent) {
        return (finalSize - originalSize) * percent + originalSize;
    }

    public static float scaleEvaluator(float originalSize, float finalSize, float percent) {
        float calcSize = (finalSize - originalSize) * percent + originalSize;
        return calcSize / originalSize;
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

        return ((startA + (int) (percent * (endA - startA))) << 24) |//
               ((startR + (int) (percent * (endR - startR))) << 16) |//
               ((startG + (int) (percent * (endG - startG))) << 8) |//
               ((startB + (int) (percent * (endB - startB))));
    }

    /** 获取状态栏的高度 */
    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
