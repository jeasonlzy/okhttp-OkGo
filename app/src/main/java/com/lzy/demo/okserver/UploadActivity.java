package com.lzy.demo.okserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okgo.OkGo;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.ui.ProgressPieView;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.demo.utils.Urls;
import com.lzy.okserver.download.DownloadManager;
import com.lzy.okserver.listener.UploadListener;
import com.lzy.okserver.task.ExecutorWithListener;
import com.lzy.okserver.upload.UploadInfo;
import com.lzy.okserver.upload.UploadManager;
import com.lzy.okgo.request.PostRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Response;

public class UploadActivity extends BaseActivity implements ExecutorWithListener.OnAllTaskEndListener {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.gridView) GridView gridView;
    @Bind(R.id.tvCorePoolSize) TextView tvCorePoolSize;
    @Bind(R.id.sbCorePoolSize) SeekBar sbCorePoolSize;

    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images;
    private UploadManager uploadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        initToolBar(toolbar, true, "上传管理");

        uploadManager = UploadManager.getInstance();
        sbCorePoolSize.setMax(3);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                uploadManager.getThreadPool().setCorePoolSize(progress);
                tvCorePoolSize.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        sbCorePoolSize.setProgress(1);

        //此行代码会导致上面的seekbar监听修改线程池数量无效，此处只是为了演示功能，实际使用时，
        //直接调用 UploadManager.getInstance(getContext()).getThreadPool().setCorePoolSize(progress); 即可生效
        uploadManager.getThreadPool().getExecutor().addOnAllTaskEndListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //记得移除
        uploadManager.getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    @Override
    public void onAllTaskEnd() {
        Toast.makeText(getApplicationContext(), "所有上传任务完成", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.select)
    public void select(View view) {
        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setSelectLimit(9);
        imagePicker.setCrop(false);
        Intent intent = new Intent(getApplicationContext(), ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @OnClick(R.id.upload)
    public void upload(View view) {
        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                MyUploadListener listener = new MyUploadListener();
                listener.setUserTag(gridView.getChildAt(i));
                PostRequest postRequest = OkGo.post(Urls.URL_FORM_UPLOAD)//
                        .headers("headerKey1", "headerValue1")//
                        .headers("headerKey2", "headerValue2")//
                        .params("paramKey1", "paramValue1")//
                        .params("paramKey2", "paramValue2")//
                        .params("fileKey" + i, new File(images.get(i).path));
                uploadManager.addTask(images.get(i).path, postRequest, listener);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                MyAdapter adapter = new MyAdapter(images);
                gridView.setAdapter(adapter);
            } else {
                Toast.makeText(getApplicationContext(), "没有数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class MyAdapter extends BaseAdapter {

        private List<ImageItem> items;

        public MyAdapter(List<ImageItem> items) {
            this.items = items;
        }

        public void setData(List<ImageItem> items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public ImageItem getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int size = gridView.getWidth() / 3;
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_upload_manager, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            imagePicker.getImageLoader().displayImage(UploadActivity.this, getItem(position).path, holder.imageView, size, size);
            return convertView;
        }
    }

    private class ViewHolder {

        private ImageView imageView;
        private TextView tvProgress;
        private ProgressPieView civ;
        private View mask;

        public ViewHolder(View convertView) {
            imageView = (ImageView) convertView.findViewById(R.id.imageView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, gridView.getWidth() / 3);
            imageView.setLayoutParams(params);
            tvProgress = (TextView) convertView.findViewById(R.id.tvProgress);
            mask = convertView.findViewById(R.id.mask);
            civ = (ProgressPieView) convertView.findViewById(R.id.civ);
            tvProgress.setText("请上传");
            civ.setText("请上传");
        }

        public void refresh(UploadInfo uploadInfo) {
            if (uploadInfo.getState() == DownloadManager.NONE) {
                tvProgress.setText("请上传");
                civ.setText("请上传");
            } else if (uploadInfo.getState() == UploadManager.ERROR) {
                tvProgress.setText("上传出错");
                civ.setText("错误");
            } else if (uploadInfo.getState() == UploadManager.WAITING) {
                tvProgress.setText("等待中");
                civ.setText("等待");
            } else if (uploadInfo.getState() == UploadManager.FINISH) {
                tvProgress.setText("上传成功");
                civ.setText("成功");
            } else if (uploadInfo.getState() == UploadManager.UPLOADING) {
                tvProgress.setText("上传中");
                civ.setProgress((int) (uploadInfo.getProgress() * 100));
                civ.setText((Math.round(uploadInfo.getProgress() * 10000) * 1.0f / 100) + "%");
            }
        }

        public void finish() {
            tvProgress.setText("上传成功");
            civ.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);
        }
    }

    private class MyUploadListener extends UploadListener<String> {

        private ViewHolder holder;

        @Override
        public void onProgress(UploadInfo uploadInfo) {
            Log.e("MyUploadListener", "onProgress:" + uploadInfo.getTotalLength() + " " + uploadInfo.getUploadLength() + " " + uploadInfo.getProgress());
            holder = (ViewHolder) ((View) getUserTag()).getTag();
            holder.refresh(uploadInfo);
        }

        @Override
        public void onFinish(String s) {
            Log.e("MyUploadListener", "finish:" + s);
            holder.finish();
        }

        @Override
        public void onError(UploadInfo uploadInfo, String errorMsg, Exception e) {
            Log.e("MyUploadListener", "onError:" + errorMsg);
        }

        @Override
        public String parseNetworkResponse(Response response) throws Exception {
            Log.e("MyUploadListener", "convertSuccess");
            return response.body().string();
        }
    }
}