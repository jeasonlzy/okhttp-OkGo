package com.lzy.imagepicker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.adapter.ImageFolderAdapter;
import com.lzy.imagepicker.adapter.ImageGridAdapter;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.Utils;

import java.util.ArrayList;
import java.util.List;

public class ImageGridActivity extends BaseActivity implements ImageDataSource.OnImagesLoadedListener, ImageGridAdapter.OnImageItemClickListener, ImagePicker.OnImageSelectedListener, View.OnClickListener {

    private ImagePicker imagePicker;

    private boolean isOrigin = false;  //是否选中原图
    private int screenWidth;     //屏幕的宽
    private int screenHeight;    //屏幕的高
    private GridView mGridView;  //图片展示控件
    private View mTopBar;        //顶部栏
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private Button mBtnDir;      //文件夹切换按钮
    private Button mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private ListPopupWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private ImageGridAdapter mImageGridAdapter;  //图片九宫格展示的适配器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);

        imagePicker = ImagePicker.getInstance();
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);
        DisplayMetrics dm = Utils.getScreenPix(this);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnDir = (Button) findViewById(R.id.btn_dir);
        mBtnDir.setOnClickListener(this);
        mBtnPre = (Button) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mTopBar = findViewById(R.id.top_bar);
        mFooterBar = findViewById(R.id.footer_bar);
        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);

        onImageSelected(0, null, false);
        new ImageDataSource(this, null, this);
    }

    @Override
    protected void onDestroy() {
        imagePicker.removeOnImageSelectedListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);  //多选不允许裁剪裁剪，返回数据
            finish();
        } else if (id == R.id.btn_dir) {
            //点击文件夹按钮
            if (mFolderPopupWindow == null) createPopupFolderList(screenWidth, screenHeight);
            backgroundAlpha(0.3f);   //改变View的背景透明度
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.show();
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.getListView().setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedImages());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            //点击返回按钮
            finish();
        }
    }

    /** 创建弹出的ListView */
    private void createPopupFolderList(int width, int height) {
        mFolderPopupWindow = new ListPopupWindow(this);
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mFolderPopupWindow.setAdapter(mImageFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);  //如果不设置，就是 AnchorView 的宽度
        mFolderPopupWindow.setHeight(height * 5 / 8);
        mFolderPopupWindow.setAnchorView(mFooterBar);  //ListPopupWindow总会相对于这个View
        mFolderPopupWindow.setModal(true);  //是否为模态，影响返回键的处理
        mFolderPopupWindow.setAnimationStyle(R.style.popupwindow_anim_style);
        mFolderPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
        mFolderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    mImageGridAdapter.refreshData(imageFolder.images);
                    mBtnDir.setText(imageFolder.name);
                }
                mGridView.smoothScrollToPosition(0);//滑动到顶部
            }
        });
    }

    /** 设置屏幕透明度  0.0透明  1.0不透明 */
    public void backgroundAlpha(float alpha) {
        mGridView.setAlpha(alpha);
        mTopBar.setAlpha(alpha);
        mFooterBar.setAlpha(1.0f);
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        mImageGridAdapter.refreshData(imageFolders.get(0).images);
        mImageGridAdapter.setOnImageItemClickListener(this);
        mGridView.setAdapter(mImageGridAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
    }

    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {
        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getCurrentImageFolderItems());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
            imagePicker.clearSelectedImages();
            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                Intent intent = new Intent();
                intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                finish();
            }
        }
    }

    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.select_complete, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
        } else {
            mBtnOk.setText(getString(R.string.complete));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
        }
        mBtnPre.setText(getResources().getString(R.string.preview_count, imagePicker.getSelectImageCount()));
        mImageGridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                //发送广播通知图片增加了
                ImagePicker.galleryAddPic(this, imagePicker.getTakeImageFile());
                ImageItem imageItem = new ImageItem();
                imageItem.path = imagePicker.getTakeImageFile().getAbsolutePath();
                imagePicker.clearSelectedImages();
                imagePicker.addSelectedImageItem(0, imageItem, true);
                if (imagePicker.isCrop()) {
                    Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                    startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);   //单选不需要裁剪，返回数据
                    finish();
                }
            } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
            } else {
                //说明是从裁剪页面过来的数据，直接返回就可以
                setResult(ImagePicker.RESULT_CODE_ITEMS, data);
                finish();
            }
        }
    }
}