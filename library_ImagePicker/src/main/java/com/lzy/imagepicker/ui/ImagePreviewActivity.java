package com.lzy.imagepicker.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.Formatter;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.Utils;
import com.lzy.imagepicker.adapter.ImagePageAdapter;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.view.SuperCheckBox;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;

public class ImagePreviewActivity extends BaseActivity implements ImagePicker.OnImageSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String ISORIGIN = "isOrigin";

    private boolean isOrigin;    //是否选中原图
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> mImageItems;      //跳转进ImagePreviewFragment的图片文件夹
    private int mCurrentPosition = 0;         //跳转进ImagePreviewFragment时的序号，第几个图片
    private TextView mTitleCount;             //显示当前图片的位置  例如  5/31
    private SuperCheckBox mCbCheck;           //是否选中当前图片的CheckBox
    private SuperCheckBox mCbOrigin;          //原图
    private Button mBtnOk;                    //确认图片的选择
    private ArrayList<ImageItem> selectedImages;   //所有已经选中的图片
    private View content;
    private View topBar;
    private View bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        isOrigin = getIntent().getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
        mCurrentPosition = getIntent().getIntExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
        mImageItems = (ArrayList<ImageItem>) getIntent().getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
        imagePicker = ImagePicker.getInstance();
        imagePicker.addOnImageSelectedListener(this);
        selectedImages = imagePicker.getSelectedImages();

        //初始化控件
        content = findViewById(R.id.content);

        //因为状态栏透明后，布局整体会上移，所以给头部加上状态栏的margin值，保证头部不会被覆盖
        topBar = findViewById(R.id.top_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
        params.topMargin = Utils.getStatusHeight(this);
        topBar.setLayoutParams(params);

        bottomBar = findViewById(R.id.bottom_bar);
        findViewById(R.id.btn_back).setOnClickListener(this);
        mTitleCount = (TextView) findViewById(R.id.tv_des);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mCbCheck = (SuperCheckBox) findViewById(R.id.cb_check);
        mCbOrigin = (SuperCheckBox) findViewById(R.id.cb_origin);
        mCbOrigin.setText(getString(R.string.origin));
        mCbOrigin.setOnCheckedChangeListener(this);
        mCbOrigin.setChecked(isOrigin);
        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.viewpager);
        ImagePageAdapter adapter = new ImagePageAdapter(this, mImageItems);
        adapter.setPhotoViewClickListener(new ImagePageAdapter.PhotoViewClickListener() {
            @Override
            public void OnPhotoTapListener(View view, float v, float v1) {
                onImageSingleTap();
            }
        });
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(mCurrentPosition, false);

        //初始化当前页面的状态
        onImageSelected(0, null, false);
        ImageItem item = mImageItems.get(mCurrentPosition);
        boolean isSelected = imagePicker.isSelect(item);
        mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
        mCbCheck.setChecked(isSelected);
        //滑动ViewPager的时候，根据外界的数据改变当前的选中状态和当前的图片的位置描述文本
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mCurrentPosition = position;
                ImageItem item = mImageItems.get(mCurrentPosition);
                boolean isSelected = imagePicker.isSelect(item);
                mCbCheck.setChecked(isSelected);
                mTitleCount.setText(getString(R.string.preview_image_count, mCurrentPosition + 1, mImageItems.size()));
            }
        });
        //当点击当前选中按钮的时候，需要根据当前的选中状态添加和移除图片
        mCbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageItem imageItem = mImageItems.get(mCurrentPosition);
                int selectLimit = imagePicker.getSelectLimit();
                if (mCbCheck.isChecked() && selectedImages.size() >= selectLimit) {
                    Toast.makeText(ImagePreviewActivity.this, ImagePreviewActivity.this.getString(R.string.select_limit, selectLimit), Toast.LENGTH_SHORT).show();
                    mCbCheck.setChecked(false);
                } else {
                    imagePicker.addSelectedImageItem(mCurrentPosition, imageItem, mCbCheck.isChecked());
                }
            }
        });
    }

    /**
     * 图片添加成功后，修改当前图片的选中数量
     * 当调用 addSelectedImageItem 或 deleteSelectedImageItem 都会触发当前回调
     */
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
        }

        if (mCbOrigin.isChecked()) {
            long size = 0;
            for (ImageItem imageItem : selectedImages)
                size += imageItem.size;
            String fileSize = Formatter.formatFileSize(this, size);
            mCbOrigin.setText(getString(R.string.origin_size, fileSize));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.btn_back) {
            Intent intent = new Intent();
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            setResult(ImagePicker.RESULT_CODE_BACK, intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
        setResult(ImagePicker.RESULT_CODE_BACK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.cb_origin) {
            if (isChecked) {
                long size = 0;
                for (ImageItem item : selectedImages)
                    size += item.size;
                String fileSize = Formatter.formatFileSize(this, size);
                isOrigin = true;
                mCbOrigin.setText(getString(R.string.origin_size, fileSize));
            } else {
                isOrigin = false;
                mCbOrigin.setText(getString(R.string.origin));
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    /** 单击时，隐藏头和尾 */
    public void onImageSingleTap() {
        if (topBar.getVisibility() == View.VISIBLE) {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_out));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_out));
            topBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
            tintManager.setStatusBarTintResource(R.color.transparent);//通知栏所需颜色
            //给最外层布局加上这个属性表示，Activity全屏显示，且状态栏被隐藏覆盖掉。
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {
            topBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.top_in));
            bottomBar.setAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            topBar.setVisibility(View.VISIBLE);
            bottomBar.setVisibility(View.VISIBLE);
            tintManager.setStatusBarTintResource(R.color.status_bar);//通知栏所需颜色
            //Activity全屏显示，但状态栏不会被隐藏覆盖，状态栏依然可见，Activity顶端布局部分会被状态遮住
            if (Build.VERSION.SDK_INT >= 16)
                content.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }
}
