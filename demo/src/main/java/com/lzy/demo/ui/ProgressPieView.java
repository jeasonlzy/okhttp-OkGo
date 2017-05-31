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
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

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
public class ProgressPieView extends View {

    public interface OnProgressListener {
        public void onProgressChanged(int progress, int max);

        public void onProgressCompleted();
    }

    /**
     * Fills the progress radially in a clockwise direction.
     */
    public static final int FILL_TYPE_RADIAL = 0;
    /**
     * Fills the progress expanding from the center of the view.
     */
    public static final int FILL_TYPE_CENTER = 1;

    public static final int SLOW_ANIMATION_SPEED = 50;
    public static final int MEDIUM_ANIMATION_SPEED = 25;
    public static final int FAST_ANIMATION_SPEED = 1;

    private static final int DEFAULT_MAX = 100;
    private static final int DEFAULT_PROGRESS = 0;
    private static final int DEFAULT_START_ANGLE = -90;
    private static final float DEFAULT_STROKE_WIDTH = 3f;
    private static final float DEFAULT_TEXT_SIZE = 14f;
    private static final int DEFAULT_VIEW_SIZE = 96;

    private static LruCache<String, Typeface> sTypefaceCache = new LruCache<String, Typeface>(8);

    private OnProgressListener mListener;
    private DisplayMetrics mDisplayMetrics;
    private int mMax = DEFAULT_MAX;
    private int mProgress = DEFAULT_PROGRESS;
    private int mStartAngle = DEFAULT_START_ANGLE;
    private boolean mInverted = false;
    private boolean mCounterclockwise = false;
    private boolean mShowStroke = true;
    private float mStrokeWidth = DEFAULT_STROKE_WIDTH;
    private boolean mShowText = true;
    private float mTextSize = DEFAULT_TEXT_SIZE;
    private String mText;
    private String mTypeface;
    private boolean mShowImage = true;
    private Drawable mImage;
    private Rect mImageRect;
    private Paint mStrokePaint;
    private Paint mTextPaint;
    private Paint mProgressPaint;
    private Paint mBackgroundPaint;
    private RectF mInnerRectF;
    private int mProgressFillType = FILL_TYPE_RADIAL;

    private int mAnimationSpeed = MEDIUM_ANIMATION_SPEED;
    private AnimationHandler mAnimationHandler = new AnimationHandler();

    private int mViewSize;

    public ProgressPieView(Context context) {
        this(context, null);
    }

    public ProgressPieView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressPieView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();

        mStrokeWidth = mStrokeWidth * mDisplayMetrics.density;
        mTextSize = mTextSize * mDisplayMetrics.scaledDensity;

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressPieView);
        final Resources res = getResources();

        mMax = a.getInteger(R.styleable.ProgressPieView_ppvMax, mMax);
        mProgress = a.getInteger(R.styleable.ProgressPieView_ppvProgress, mProgress);
        mStartAngle = a.getInt(R.styleable.ProgressPieView_ppvStartAngle, mStartAngle);
        mInverted = a.getBoolean(R.styleable.ProgressPieView_ppvInverted, mInverted);
        mCounterclockwise = a.getBoolean(R.styleable.ProgressPieView_ppvCounterclockwise, mCounterclockwise);
        mStrokeWidth = a.getDimension(R.styleable.ProgressPieView_ppvStrokeWidth, mStrokeWidth);
        mTypeface = a.getString(R.styleable.ProgressPieView_ppvTypeface);
        mTextSize = a.getDimension(R.styleable.ProgressPieView_android_textSize, mTextSize);
        mText = a.getString(R.styleable.ProgressPieView_android_text);

        mShowStroke = a.getBoolean(R.styleable.ProgressPieView_ppvShowStroke, mShowStroke);
        mShowText = a.getBoolean(R.styleable.ProgressPieView_ppvShowText, mShowText);
        mImage = a.getDrawable(R.styleable.ProgressPieView_ppvImage);

        int backgroundColor = Color.parseColor("#00000000");
        backgroundColor = a.getColor(R.styleable.ProgressPieView_ppvBackgroundColor, backgroundColor);
        int progressColor = Color.parseColor("#33b5e5");
        progressColor = a.getColor(R.styleable.ProgressPieView_ppvProgressColor, progressColor);
        int strokeColor = Color.parseColor("#33b5e5");
        strokeColor = a.getColor(R.styleable.ProgressPieView_ppvStrokeColor, strokeColor);
        int textColor = Color.parseColor("#333333");
        textColor = a.getColor(R.styleable.ProgressPieView_android_textColor, textColor);

        mProgressFillType = a.getInteger(R.styleable.ProgressPieView_ppvProgressFillType, mProgressFillType);

        a.recycle();

        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setColor(backgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStyle(Paint.Style.FILL);

        mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStrokePaint.setColor(strokeColor);
        mStrokePaint.setStyle(Paint.Style.STROKE);
        mStrokePaint.setStrokeWidth(mStrokeWidth);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        mInnerRectF = new RectF();
        mImageRect = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = resolveSize(DEFAULT_VIEW_SIZE, widthMeasureSpec);
        int height = resolveSize(DEFAULT_VIEW_SIZE, heightMeasureSpec);
        mViewSize = Math.min(width, height);

        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mInnerRectF.set(0, 0, mViewSize, mViewSize);
        mInnerRectF.offset((getWidth() - mViewSize) / 2, (getHeight() - mViewSize) / 2);
        if (mShowStroke) {
            final int halfBorder = (int) (mStrokePaint.getStrokeWidth() / 2f + 0.5f);
            mInnerRectF.inset(halfBorder, halfBorder);
        }
        float centerX = mInnerRectF.centerX();
        float centerY = mInnerRectF.centerY();

        canvas.drawArc(mInnerRectF, 0, 360, true, mBackgroundPaint);

        switch (mProgressFillType) {
            case FILL_TYPE_RADIAL:
                float sweepAngle = 360 * mProgress / mMax;
                if (mInverted) {
                    sweepAngle = sweepAngle - 360;
                }
                if (mCounterclockwise) {
                    sweepAngle = -sweepAngle;
                }
                canvas.drawArc(mInnerRectF, mStartAngle, sweepAngle, true, mProgressPaint);
                break;
            case FILL_TYPE_CENTER:
                float radius = (mViewSize / 2) * ((float) mProgress / mMax);
                if (mShowStroke) {
                    radius = radius + 0.5f - mStrokePaint.getStrokeWidth();
                }
                canvas.drawCircle(centerX, centerY, radius, mProgressPaint);
                break;
            default:
                throw new IllegalArgumentException("Invalid Progress Fill = " + mProgressFillType);
        }

        if (!TextUtils.isEmpty(mText) && mShowText) {
            if (!TextUtils.isEmpty(mTypeface)) {
                Typeface typeface = sTypefaceCache.get(mTypeface);
                if (null == typeface && null != getResources()) {
                    AssetManager assets = getResources().getAssets();
                    if (null != assets) {
                        typeface = Typeface.createFromAsset(assets, mTypeface);
                        sTypefaceCache.put(mTypeface, typeface);
                    }
                }
                mTextPaint.setTypeface(typeface);
            }
            int xPos = (int) centerX;
            int yPos = (int) (centerY - (mTextPaint.descent() + mTextPaint.ascent()) / 2);
            canvas.drawText(mText, xPos, yPos, mTextPaint);
        }

        if (null != mImage && mShowImage) {
            int drawableSize = mImage.getIntrinsicWidth();
            mImageRect.set(0, 0, drawableSize, drawableSize);
            mImageRect.offset((getWidth() - drawableSize) / 2, (getHeight() - drawableSize) / 2);
            mImage.setBounds(mImageRect);
            mImage.draw(canvas);
        }

        if (mShowStroke) {
            canvas.drawOval(mInnerRectF, mStrokePaint);
        }

    }

    /**
     * Gets the maximum progress value.
     */
    public int getMax() {
        return mMax;
    }

    /**
     * Sets the maximum progress value. Defaults to 100.
     */
    public void setMax(int max) {
        if (max <= 0 || max < mProgress) {
            throw new IllegalArgumentException(String.format("Max (%d) must be > 0 and >= %d", max, mProgress));
        }
        mMax = max;
        invalidate();
    }

    /**
     * Sets the animation speed used in the animateProgressFill method.
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.mAnimationSpeed = animationSpeed;
    }

    /**
     * Returns the current animation speed used in animateProgressFill method.
     */
    public int getAnimationSpeed() {
        return this.mAnimationSpeed;
    }

    /**
     * Animates a progress fill of the view, using a Handler.
     */
    public void animateProgressFill() {
        mAnimationHandler.removeMessages(0);
        mAnimationHandler.setAnimateTo(mMax);
        mAnimationHandler.sendEmptyMessage(0);
        invalidate();
    }

    /**
     * Animates a progress fill of the view, using a Handler.
     *
     * @param animateTo - the progress value the animation should stop at (0 - MAX)
     */
    public void animateProgressFill(int animateTo) {
        mAnimationHandler.removeMessages(0);
        if (animateTo > mMax || animateTo < 0) {
            throw new IllegalArgumentException(String.format("Animation progress (%d) is greater than the max progress (%d) or lower than 0 ", animateTo, mMax));
        }
        mAnimationHandler.setAnimateTo(animateTo);
        mAnimationHandler.sendEmptyMessage(0);
        invalidate();
    }

    /**
     * Stops the views animation.
     */
    public void stopAnimating() {
        mAnimationHandler.removeMessages(0);
        mAnimationHandler.setAnimateTo(mProgress);
        invalidate();
    }

    /**
     * Gets the current progress from 0 to max.
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Sets the current progress (must be between 0 and max).
     */
    public void setProgress(int progress) {
        if (progress > mMax || progress < 0) {
            throw new IllegalArgumentException(String.format("Progress (%d) must be between %d and %d", progress, 0, mMax));
        }
        mProgress = progress;
        if (null != mListener) {
            if (mProgress == mMax) {
                mListener.onProgressCompleted();
            } else {
                mListener.onProgressChanged(mProgress, mMax);
            }
        }
        invalidate();
    }

    /**
     * Gets the start angle the {@link #FILL_TYPE_RADIAL} uses.
     */
    public int getStartAngle() {
        return mStartAngle;
    }

    /**
     * Sets the start angle the {@link #FILL_TYPE_RADIAL} uses.
     *
     * @param startAngle start angle in degrees
     */
    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
    }

    /**
     * Gets the inverted state.
     */
    public boolean isInverted() {
        return mInverted;
    }

    /**
     * Sets the inverted state.
     *
     * @param inverted draw the progress inverted or not
     */
    public void setInverted(boolean inverted) {
        mInverted = inverted;
    }

    /**
     * Gets the counterclockwise state.
     */
    public boolean isCounterclockwise() {
        return mCounterclockwise;
    }

    /**
     * Sets the counterclockwise state.
     *
     * @param counterclockwise draw the progress counterclockwise or not
     */
    public void setCounterclockwise(boolean counterclockwise) {
        mCounterclockwise = counterclockwise;
    }

    /**
     * Gets the color used to display the progress of the view.
     */
    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    /**
     * Sets the color used to display the progress of the view.
     *
     * @param color - color of the progress part of the view
     */
    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    /**
     * Gets the color used to display the background of the view.
     */
    public int getBackgroundColor() {
        return mBackgroundPaint.getColor();
    }

    /**
     * Sets the color used to display the background of the view.
     *
     * @param color - color of the background part of the view
     */
    public void setBackgroundColor(int color) {
        mBackgroundPaint.setColor(color);
        invalidate();
    }

    /**
     * Gets the color used to display the text of the view.
     */
    public int getTextColor() {
        return mTextPaint.getColor();
    }

    /**
     * Sets the color used to display the text of the view.
     *
     * @param color - color of the text part of the view
     */
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    /**
     * Gets the text size in sp.
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * Sets the text size.
     *
     * @param sizeSp in sp for the text
     */
    public void setTextSize(int sizeSp) {
        mTextSize = sizeSp * mDisplayMetrics.scaledDensity;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    /**
     * Gets the text of the view.
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the text of the view.
     *
     * @param text to be displayed in the view
     */
    public void setText(String text) {
        mText = text;
        invalidate();
    }

    /**
     * Gets the typeface of the text.
     */
    public String getTypeface() {
        return mTypeface;
    }

    /**
     * Sets the text typeface.
     * - i.printStackTrace. fonts/Roboto/Roboto-Regular.ttf
     *
     * @param typeface that the text is displayed in
     */
    public void setTypeface(String typeface) {
        mTypeface = typeface;
        invalidate();
    }

    /**
     * Gets the show text state.
     */
    public boolean isTextShowing() {
        return mShowText;
    }

    /**
     * Sets the show text state.
     *
     * @param showText show or hide text
     */
    public void setShowText(boolean showText) {
        mShowText = showText;
        invalidate();
    }

    /**
     * Get the color used to display the stroke of the view.
     */
    public int getStrokeColor() {
        return mStrokePaint.getColor();
    }

    /**
     * Sets the color used to display the stroke of the view.
     *
     * @param color - color of the stroke part of the view
     */
    public void setStrokeColor(int color) {
        mStrokePaint.setColor(color);
        invalidate();
    }

    /**
     * Gets the stroke width in dp.
     */
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    /**
     * Set the stroke width.
     *
     * @param widthDp in dp for the pie border
     */
    public void setStrokeWidth(int widthDp) {
        mStrokeWidth = widthDp * mDisplayMetrics.density;
        mStrokePaint.setStrokeWidth(mStrokeWidth);
        invalidate();
    }

    /**
     * Gets the show stroke state.
     */
    public boolean isStrokeShowing() {
        return mShowStroke;
    }

    /**
     * Sets the show stroke state.
     *
     * @param showStroke show or hide stroke
     */
    public void setShowStroke(boolean showStroke) {
        mShowStroke = showStroke;
        invalidate();
    }

    /**
     * Gets the drawable of the view.
     */
    public Drawable getImageDrawable() {
        return mImage;
    }

    /**
     * Sets the drawable of the view.
     *
     * @param image drawable of the view
     */
    public void setImageDrawable(Drawable image) {
        mImage = image;
        invalidate();
    }

    /**
     * Sets the drawable of the view.
     *
     * @param resId resource id of the view's drawable
     */
    public void setImageResource(int resId) {
        if (null != getResources()) {
            mImage = getResources().getDrawable(resId);
            invalidate();
        }
    }

    /**
     * Gets the show image state.
     */
    public boolean isImageShowing() {
        return mShowImage;
    }

    /**
     * Sets the show image state.
     *
     * @param showImage show or hide image
     */
    public void setShowImage(boolean showImage) {
        mShowImage = showImage;
        invalidate();
    }

    /**
     * Gets the progress fill type.
     */
    public int getProgressFillType() {
        return mProgressFillType;
    }

    /**
     * Sets the progress fill type.
     *
     * @param fillType one of {@link #FILL_TYPE_CENTER}, {@link #FILL_TYPE_RADIAL}
     */
    public void setProgressFillType(int fillType) {
        mProgressFillType = fillType;
    }

    /**
     * Sets the progress listner.
     *
     * @param listener progress listener
     */
    public void setOnProgressListener(OnProgressListener listener) {
        mListener = listener;
    }

    /**
     * Handler used to perform the fill animation.
     */
    private class AnimationHandler extends Handler {

        private int mAnimateTo;

        public void setAnimateTo(int animateTo) {
            mAnimateTo = animateTo;
        }

        @Override
        public void handleMessage(Message msg) {
            if (mProgress > mAnimateTo) {
                setProgress(mProgress - 1);
                sendEmptyMessageDelayed(0, mAnimationSpeed);
            } else if (mProgress < mAnimateTo) {
                setProgress(mProgress + 1);
                sendEmptyMessageDelayed(0, mAnimationSpeed);
            } else {
                removeMessages(0);
            }
        }
    }

}
