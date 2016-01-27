package com.lzy.imagepicker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.lzy.imagepicker.R;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/7
 * 描    述：
 * Matrix 的9个值分别为  缩放  平移  倾斜
 * MSCALE_X	 MSKEW_X	MTRANS_X
 * MSKEW_Y	 MSCALE_Y	MTRANS_Y
 * MPERSP_0  MPERSP_1	MPERSP_2
 * 修订历史：
 * ================================================
 */

public class CropImageView extends ImageView {

    /******************************** 中间的FocusView绘图相关的参数 *****************************/
    public enum Style {
        RECTANGLE, CIRCLE
    }

    private Style[] styles = {Style.RECTANGLE, Style.CIRCLE};

    private int mMaskColor = 0xAF000000;   //暗色
    private int mBorderColor = 0xAA808080; //焦点框的边框颜色
    private int mBorderWidth = 1;         //焦点边框的宽度（画笔宽度）
    private int mFocusWidth = 250;         //焦点框的宽度
    private int mFocusHeight = 250;        //焦点框的高度
    private int mDefaultStyleIndex = 0;    //默认焦点框的形状

    private Style mStyle = styles[mDefaultStyleIndex];
    private Paint mBorderPaint = new Paint();
    private Path mFocusPath = new Path();
    private RectF mFocusRect = new RectF();

    /******************************** 图片缩放位移控制的参数 ************************************/
    private static final float MAX_SCALE = 4.0f;  //最大缩放比，图片缩放后的大小与中间选中区域的比值
    private static final int NONE = 0;   // 初始化
    private static final int DRAG = 1;   // 拖拽
    private static final int ZOOM = 2;   // 缩放
    private static final int ROTATE = 3; // 旋转
    private static final int ZOOM_OR_ROTATE = 4;  // 缩放或旋转

    private static final int SAVE_SUCCESS = 1001;  // 缩放或旋转
    private static final int SAVE_ERROR = 1002;  // 缩放或旋转

    private int mImageWidth;
    private int mImageHeight;
    private int mRotatedImageWidth;
    private int mRotatedImageHeight;
    private Matrix matrix = new Matrix();      //图片变换的matrix
    private Matrix savedMatrix = new Matrix(); //开始变幻的时候，图片的matrix
    private PointF pA = new PointF();          //第一个手指按下点的坐标
    private PointF pB = new PointF();          //第二个手指按下点的坐标
    private PointF midPoint = new PointF();    //两个手指的中间点
    private PointF doubleClickPos = new PointF();  //双击图片的时候，双击点的坐标
    private PointF mFocusMidPoint = new PointF();  //中间View的中间点
    private int mode = NONE;            //初始的模式
    private long doubleClickTime = 0;   //第二次双击的时间
    private double rotation = 0;        //手指旋转的角度，不是90的整数倍，可能为任意值，需要转换成level
    private float oldDist = 1;          //双指第一次的距离
    private int sumRotateLevel = 0;     //旋转的角度，90的整数倍
    private float mMaxScale = MAX_SCALE;//程序根据不同图片的大小，动态得到的最大缩放比
    private boolean isInited = false;   //是否经过了 onSizeChanged 初始化
    private boolean mSaving = false;    //是否正在保存
    private Handler mHandler = new InnerHandler();

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mFocusWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusWidth, getResources().getDisplayMetrics());
        mFocusHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mFocusHeight, getResources().getDisplayMetrics());
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mBorderWidth, getResources().getDisplayMetrics());

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CropImageView);
        mMaskColor = a.getColor(R.styleable.CropImageView_cropMaskColor, mMaskColor);
        mBorderColor = a.getColor(R.styleable.CropImageView_cropBorderColor, mBorderColor);
        mBorderWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropBorderWidth, mBorderWidth);
        mFocusWidth = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusWidth, mFocusWidth);
        mFocusHeight = a.getDimensionPixelSize(R.styleable.CropImageView_cropFocusHeight, mFocusHeight);
        mDefaultStyleIndex = a.getInteger(R.styleable.CropImageView_cropStyle, mDefaultStyleIndex);
        mStyle = styles[mDefaultStyleIndex];
        a.recycle();

        //只允许图片为当前的缩放模式
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        initImage();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        initImage();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        initImage();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        initImage();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        isInited = true;
        initImage();
    }

    /** 初始化图片和焦点框 */
    private void initImage() {
        Drawable d = getDrawable();
        if (!isInited || d == null) return;

        mode = NONE;
        matrix = getImageMatrix();
        mImageWidth = mRotatedImageWidth = d.getIntrinsicWidth();
        mImageHeight = mRotatedImageHeight = d.getIntrinsicHeight();
        //计算出焦点框的中点的坐标和上、下、左、右边的x或y的值
        int viewWidth = getWidth();
        int viewHeight = getHeight();
        float midPointX = viewWidth / 2;
        float midPointY = viewHeight / 2;
        mFocusMidPoint = new PointF(midPointX, midPointY);

        if (mStyle == Style.CIRCLE) {
            int focusSize = Math.min(mFocusWidth, mFocusHeight);
            mFocusWidth = focusSize;
            mFocusHeight = focusSize;
        }
        mFocusRect.left = mFocusMidPoint.x - mFocusWidth / 2;
        mFocusRect.right = mFocusMidPoint.x + mFocusWidth / 2;
        mFocusRect.top = mFocusMidPoint.y - mFocusHeight / 2;
        mFocusRect.bottom = mFocusMidPoint.y + mFocusHeight / 2;

        //适配焦点框的缩放比例（图片的最小边不小于焦点框的最小边）
        float fitFocusScale = getScale(mImageWidth, mImageHeight, mFocusWidth, mFocusHeight, true);
        mMaxScale = fitFocusScale * MAX_SCALE;
        //适配显示图片的ImageView的缩放比例（图片至少有一边是铺满屏幕的显示的情形）
        float fitViewScale = getScale(mImageWidth, mImageHeight, viewWidth, viewHeight, false);
        //确定最终的缩放比例,在适配焦点框的前提下适配显示图片的ImageView，
        //方案：首先满足适配焦点框，如果还能适配显示图片的ImageView，则适配它，即取缩放比例的最大值。
        //采取这种方案的原因：有可能图片很长或者很高，适配了ImageView的时候可能会宽/高已经小于焦点框的宽/高
        float scale = fitViewScale > fitFocusScale ? fitViewScale : fitFocusScale;
        //图像中点为中心进行缩放
        matrix.setScale(scale, scale, mImageWidth / 2, mImageHeight / 2);
        float[] mImageMatrixValues = new float[9];
        matrix.getValues(mImageMatrixValues); //获取缩放后的mImageMatrix的值
        float transX = mFocusMidPoint.x - (mImageMatrixValues[2] + mImageWidth * mImageMatrixValues[0] / 2);  //X轴方向的位移
        float transY = mFocusMidPoint.y - (mImageMatrixValues[5] + mImageHeight * mImageMatrixValues[4] / 2); //Y轴方向的位移
        matrix.postTranslate(transX, transY);
        setImageMatrix(matrix);
        invalidate();
    }

    /** 计算边界缩放比例 isMinScale 是否最小比例，true 最小缩放比例， false 最大缩放比例 */
    private float getScale(int bitmapWidth, int bitmapHeight, int minWidth, int minHeight, boolean isMinScale) {
        float scale;
        float scaleX = (float) minWidth / bitmapWidth;
        float scaleY = (float) minHeight / bitmapHeight;
        if (isMinScale) {
            scale = scaleX > scaleY ? scaleX : scaleY;
        } else {
            scale = scaleX < scaleY ? scaleX : scaleY;
        }
        return scale;
    }

    /** 绘制焦点框 */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (Style.RECTANGLE == mStyle) {
            mFocusPath.addRect(mFocusRect, Path.Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.clipPath(mFocusPath, Region.Op.DIFFERENCE);
            canvas.drawColor(mMaskColor);
            canvas.restore();
        } else if (Style.CIRCLE == mStyle) {
            float radius = Math.min((mFocusRect.right - mFocusRect.left) / 2, (mFocusRect.bottom - mFocusRect.top) / 2);
            mFocusPath.addCircle(mFocusMidPoint.x, mFocusMidPoint.y, radius, Path.Direction.CCW);
            canvas.save();
            canvas.clipRect(0, 0, getWidth(), getHeight());
            canvas.clipPath(mFocusPath, Region.Op.DIFFERENCE);
            canvas.drawColor(mMaskColor);
            canvas.restore();
        }
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setAntiAlias(true);
        canvas.drawPath(mFocusPath, mBorderPaint);
        mFocusPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSaving || null == getDrawable()) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:  //第一个点按下
                savedMatrix.set(matrix);   //以后每次需要变换的时候，以现在的状态为基础进行变换
                pA.set(event.getX(), event.getY());
                pB.set(event.getX(), event.getY());
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:  //第二个点按下
                if (event.getActionIndex() > 1) break;
                pA.set(event.getX(0), event.getY(0));
                pB.set(event.getX(1), event.getY(1));
                midPoint.set((pA.x + pB.x) / 2, (pA.y + pB.y) / 2);
                oldDist = spacing(pA, pB);
                savedMatrix.set(matrix);  //以后每次需要变换的时候，以现在的状态为基础进行变换
                if (oldDist > 10f) mode = ZOOM_OR_ROTATE;//两点之间的距离大于10才有效
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM_OR_ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (a >= 10) {
                        double cosB = (a * a + c * c - b * b) / (2 * a * c);
                        double angleB = Math.acos(cosB);
                        double PID4 = Math.PI / 4;
                        //旋转时，默认角度在 45 - 135 度之间
                        if (angleB > PID4 && angleB < 3 * PID4) mode = ROTATE;
                        else mode = ZOOM;
                    }
                }
                if (mode == DRAG) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - pA.x, event.getY() - pA.y);
                    fixTranslation();
                    setImageMatrix(matrix);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        // 这里之所以用 maxPostScale 矫正一下，主要是防止缩放到最大时，继续缩放图片会产生位移
                        float tScale = Math.min(newDist / oldDist, maxPostScale());
                        if (tScale != 0) {
                            matrix.postScale(tScale, tScale, midPoint.x, midPoint.y);
                            fixScale();
                            fixTranslation();
                            setImageMatrix(matrix);
                        }
                    }
                } else if (mode == ROTATE) {
                    PointF pC = new PointF(event.getX(1) - event.getX(0) + pA.x, event.getY(1) - event.getY(0) + pA.y);
                    double a = spacing(pB.x, pB.y, pC.x, pC.y);
                    double b = spacing(pA.x, pA.y, pC.x, pC.y);
                    double c = spacing(pA.x, pA.y, pB.x, pB.y);
                    if (b > 10) {
                        double cosA = (b * b + c * c - a * a) / (2 * b * c);
                        double angleA = Math.acos(cosA);
                        double ta = pB.y - pA.y;
                        double tb = pA.x - pB.x;
                        double tc = pB.x * pA.y - pA.x * pB.y;
                        double td = ta * pC.x + tb * pC.y + tc;
                        if (td > 0) {
                            angleA = 2 * Math.PI - angleA;
                        }
                        rotation = angleA;
                        matrix.set(savedMatrix);
                        matrix.postRotate((float) (rotation * 180 / Math.PI), midPoint.x, midPoint.y);
                        setImageMatrix(matrix);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mode == DRAG) {
                    if (spacing(pA, pB) < 50) {
                        long now = System.currentTimeMillis();
                        if (now - doubleClickTime < 500 && spacing(pA, doubleClickPos) < 50) {
                            doubleClick(pA.x, pA.y);
                            now = 0;
                        }
                        doubleClickPos.set(pA);
                        doubleClickTime = now;
                    }
                } else if (mode == ROTATE) {
                    int rotateLevel = (int) Math.floor((rotation + Math.PI / 4) / (Math.PI / 2));
                    if (rotateLevel == 4) rotateLevel = 0;
                    matrix.set(savedMatrix);
                    matrix.postRotate(90 * rotateLevel, midPoint.x, midPoint.y);
                    if (rotateLevel == 1 || rotateLevel == 3) {
                        int tmp = mRotatedImageWidth;
                        mRotatedImageWidth = mRotatedImageHeight;
                        mRotatedImageHeight = tmp;
                    }
                    fixScale();
                    fixTranslation();
                    setImageMatrix(matrix);
                    sumRotateLevel += rotateLevel;
                }
                mode = NONE;
                break;
        }
        return true;
    }

    /** 修正图片的缩放比 */
    private void fixScale() {
        float imageMatrixValues[] = new float[9];
        matrix.getValues(imageMatrixValues);
        float currentScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        float minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true);
        mMaxScale = minScale * MAX_SCALE;

        //保证图片最小是占满中间的焦点空间
        if (currentScale < minScale) {
            float scale = minScale / currentScale;
            matrix.postScale(scale, scale);
        } else if (currentScale > mMaxScale) {
            float scale = mMaxScale / currentScale;
            matrix.postScale(scale, scale);
        }
    }

    /** 修正图片的位移 */
    private void fixTranslation() {
        RectF imageRect = new RectF(0, 0, mImageWidth, mImageHeight);
        matrix.mapRect(imageRect);  //获取当前图片（缩放以后的）相对于当前控件的位置区域，超过控件的上边缘或左边缘为负
        float deltaX = 0, deltaY = 0;
        if (imageRect.left > mFocusRect.left) {
            deltaX = -imageRect.left + mFocusRect.left;
        } else if (imageRect.right < mFocusRect.right) {
            deltaX = -imageRect.right + mFocusRect.right;
        }
        if (imageRect.top > mFocusRect.top) {
            deltaY = -imageRect.top + mFocusRect.top;
        } else if (imageRect.bottom < mFocusRect.bottom) {
            deltaY = -imageRect.bottom + mFocusRect.bottom;
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    /** 获取当前图片允许的最大缩放比 */
    private float maxPostScale() {
        float imageMatrixValues[] = new float[9];
        matrix.getValues(imageMatrixValues);
        float curScale = Math.abs(imageMatrixValues[0]) + Math.abs(imageMatrixValues[1]);
        return mMaxScale / curScale;
    }

    /** 计算两点之间的距离 */
    private float spacing(float x1, float y1, float x2, float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return (float) Math.sqrt(x * x + y * y);
    }

    /** 计算两点之间的距离 */
    private float spacing(PointF pA, PointF pB) {
        return spacing(pA.x, pA.y, pB.x, pB.y);
    }

    /** 双击触发的方法 */
    private void doubleClick(float x, float y) {
        float p[] = new float[9];
        matrix.getValues(p);
        float curScale = Math.abs(p[0]) + Math.abs(p[1]);
        float minScale = getScale(mRotatedImageWidth, mRotatedImageHeight, mFocusWidth, mFocusHeight, true);
        if (curScale < mMaxScale) {
            //每次双击的时候，缩放加 minScale
            float toScale = Math.min(curScale + minScale, mMaxScale) / curScale;
            matrix.postScale(toScale, toScale, x, y);
        } else {
            float toScale = minScale / curScale;
            matrix.postScale(toScale, toScale, x, y);
            fixTranslation();
        }
        setImageMatrix(matrix);
    }

    /**
     * @param expectWidth     期望的宽度
     * @param exceptHeight    期望的高度
     * @param isSaveRectangle 是否按矩形区域保存图片
     * @return 裁剪后的Bitmap
     */
    public Bitmap getCropBitmap(int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (expectWidth <= 0 || exceptHeight < 0) return null;
        Bitmap srcBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        srcBitmap = rotate(srcBitmap, sumRotateLevel * 90);  //最好用level，因为角度可能不是90的整数
        return makeCropBitmap(srcBitmap, mFocusRect, getImageMatrixRect(), expectWidth, exceptHeight, isSaveRectangle);
    }

    /**
     * @param bitmap  要旋转的图片
     * @param degrees 选择的角度（单位 度）
     * @return 旋转后的Bitmap
     */
    private Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees != 0 && bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                if (bitmap != rotateBitmap) {
//                    bitmap.recycle();
                    return rotateBitmap;
                }
            } catch (OutOfMemoryError ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * @return 获取当前图片显示的矩形区域
     */
    private RectF getImageMatrixRect() {
        RectF rectF = new RectF();
        rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        matrix.mapRect(rectF);
        return rectF;
    }

    /**
     * @param bitmap          需要裁剪的图片
     * @param focusRect       中间需要裁剪的矩形区域
     * @param imageMatrixRect 当前图片在屏幕上的显示矩形区域
     * @param expectWidth     希望获得的图片宽度，如果图片宽度不足时，拉伸图片
     * @param exceptHeight    希望获得的图片高度，如果图片高度不足时，拉伸图片
     * @param isSaveRectangle 是否希望按矩形区域保存图片
     * @return 裁剪后的图片的Bitmap
     */
    private Bitmap makeCropBitmap(Bitmap bitmap, RectF focusRect, RectF imageMatrixRect, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        float scale = imageMatrixRect.width() / bitmap.getWidth();
        int left = (int) ((focusRect.left - imageMatrixRect.left) / scale);
        int top = (int) ((focusRect.top - imageMatrixRect.top) / scale);
        int width = (int) (focusRect.width() / scale);
        int height = (int) (focusRect.height() / scale);

        if (left < 0) left = 0;
        if (top < 0) top = 0;
        if (left + width > bitmap.getWidth()) width = bitmap.getWidth() - left;
        if (top + height > bitmap.getHeight()) height = bitmap.getHeight() - top;

        try {
            bitmap = Bitmap.createBitmap(bitmap, left, top, width, height);
            if (expectWidth != width || exceptHeight != height) {
                bitmap = Bitmap.createScaledBitmap(bitmap, expectWidth, exceptHeight, true);
                if (mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
                    //如果是圆形，就将图片裁剪成圆的
                    int length = Math.min(expectWidth, exceptHeight);
                    int radius = length / 2;
                    Bitmap circleBitmap = Bitmap.createBitmap(length, length, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(circleBitmap);
                    BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(bitmapShader);
                    canvas.drawCircle(expectWidth / 2f, exceptHeight / 2f, radius, paint);
                    bitmap = circleBitmap;
                }
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * @param folder          希望保存的文件夹
     * @param expectWidth     希望保存的图片宽度
     * @param exceptHeight    希望保存的图片高度
     * @param isSaveRectangle 是否希望按矩形区域保存图片
     */
    public void saveBitmapToFile(File folder, int expectWidth, int exceptHeight, boolean isSaveRectangle) {
        if (mSaving) return;
        mSaving = true;
        final Bitmap croppedImage = getCropBitmap(expectWidth, exceptHeight, isSaveRectangle);
        Bitmap.CompressFormat outputFormat = Bitmap.CompressFormat.JPEG;
        File saveFile = createFile(folder, "IMG_", ".jpg");
        if (mStyle == CropImageView.Style.CIRCLE && !isSaveRectangle) {
            outputFormat = Bitmap.CompressFormat.PNG;
            saveFile = createFile(folder, "IMG_", ".png");
        }
        final Bitmap.CompressFormat finalOutputFormat = outputFormat;
        final File finalSaveFile = saveFile;
        new Thread() {
            @Override
            public void run() {
                saveOutput(croppedImage, finalOutputFormat, finalSaveFile);
            }
        }.start();
    }

    /** 根据系统时间、前缀、后缀产生一个文件 */
    private File createFile(File folder, String prefix, String suffix) {
        if (!folder.exists() || !folder.isDirectory()) folder.mkdirs();
        try {
            File nomedia = new File(folder, ".nomedia");  //在当前文件夹底下创建一个 .nomedia 文件
            if (!nomedia.exists()) nomedia.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA);
        String filename = prefix + dateFormat.format(new Date(System.currentTimeMillis())) + suffix;
        return new File(folder, filename);
    }

    /** 将图片保存在本地 */
    private void saveOutput(Bitmap croppedImage, Bitmap.CompressFormat outputFormat, File saveFile) {
        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(Uri.fromFile(saveFile));
            if (outputStream != null) croppedImage.compress(outputFormat, 90, outputStream);
            Message.obtain(mHandler, SAVE_SUCCESS, saveFile).sendToTarget();
        } catch (IOException ex) {
            ex.printStackTrace();
            Message.obtain(mHandler, SAVE_ERROR, saveFile).sendToTarget();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        mSaving = false;
        croppedImage.recycle();
    }

    private class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            File saveFile = (File) msg.obj;
            switch (msg.what) {
                case SAVE_SUCCESS:
                    if (listener != null) listener.onBitmapSaveSuccess(saveFile);
                    break;
                case SAVE_ERROR:
                    if (listener != null) listener.onBitmapSaveError(saveFile);
                    break;
            }
        }
    }

    /** 图片保存完成的监听 */
    private OnBitmapSaveCompleteListener listener;

    public interface OnBitmapSaveCompleteListener {
        void onBitmapSaveSuccess(File file);

        void onBitmapSaveError(File file);
    }

    public void setOnBitmapSaveCompleteListener(OnBitmapSaveCompleteListener listener) {
        this.listener = listener;
    }

    /** 返回焦点框宽度 */
    public int getFocusWidth() {
        return mFocusWidth;
    }

    /** 设置焦点框的宽度 */
    public void setFocusWidth(int width) {
        mFocusWidth = width;
        initImage();
    }

    /** 获取焦点框的高度 */
    public int getFocusHeight() {
        return mFocusHeight;
    }

    /** 设置焦点框的高度 */
    public void setFocusHeight(int height) {
        mFocusHeight = height;
        initImage();
    }

    /** 返回阴影颜色 */
    public int getMaskColor() {
        return mMaskColor;
    }

    /** 设置阴影颜色 */
    public void setMaskColor(int color) {
        mMaskColor = color;
        invalidate();
    }

    /** 返回焦点框边框颜色 */
    public int getFocusColor() {
        return mBorderColor;
    }

    /** 设置焦点框边框颜色 */
    public void setBorderColor(int color) {
        mBorderColor = color;
        invalidate();
    }

    /** 返回焦点框边框绘制宽度 */
    public float getBorderWidth() {
        return mBorderWidth;
    }

    /** 设置焦点边框宽度 */
    public void setBorderWidth(int width) {
        mBorderWidth = width;
        invalidate();
    }

    /** 设置焦点框的形状 */
    public void setFocusStyle(Style style) {
        this.mStyle = style;
        invalidate();
    }

    /** 获取焦点框的形状 */
    public Style getFocusStyle() {
        return mStyle;
    }
}