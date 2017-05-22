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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.demo.utils.AnimHelper;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
@SuppressWarnings("unused")
public class TranslateUpDownBehavior extends FloatingActionButton.Behavior {

    private boolean isAnimating = false;
    private OnStateChangeListener listener;

    public TranslateUpDownBehavior(Context context, AttributeSet attrs) {
        super();
    }

    public static TranslateUpDownBehavior form(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null || !(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("parent must be CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof TranslateUpDownBehavior)) {
            throw new IllegalArgumentException("the behavior must be TranslateUpDownBehavior");
        }
        return (TranslateUpDownBehavior) behavior;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (((dyConsumed > 0 && dyUnconsumed == 0) || (dyConsumed == 0 && dyUnconsumed > 0)) && !isAnimating && child.getVisibility() == View.VISIBLE) {
            if (listener != null) listener.onChange(true);
            AnimHelper.translateDown(child, new MyViewPropertyAnimatorListener() {
                @Override
                public void onAnimationEnd(View view) {
                    super.onAnimationEnd(view);
                    view.setVisibility(View.INVISIBLE);
                }
            });
        } else if ((dyConsumed < 0 && dyUnconsumed == 0) || (dyConsumed == 0 && dyUnconsumed < 0) && !isAnimating && child.getVisibility() == View.INVISIBLE) {
            if (listener != null) listener.onChange(false);
            child.setVisibility(View.VISIBLE);
            AnimHelper.translateUp(child, null);
        }
    }

    private class MyViewPropertyAnimatorListener implements ViewPropertyAnimatorListener {

        @Override
        public void onAnimationStart(View view) {
            isAnimating = true;
        }

        @Override
        public void onAnimationEnd(View view) {
            isAnimating = false;
        }

        @Override
        public void onAnimationCancel(View view) {
            isAnimating = false;
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnStateChangeListener {
        void onChange(boolean isUp);
    }
}
