package com.lzy.okhttpdemo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.lzy.imagepicker.loader.GlideImageLoader;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.callback.DialogCallback;
import com.lzy.okhttpdemo.ui.ProgressPieView;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttpserver.download.DownloadManager;
import com.lzy.okhttpserver.listener.UploadListener;
import com.lzy.okhttpserver.task.ExecutorWithListener;
import com.lzy.okhttpserver.upload.UploadInfo;
import com.lzy.okhttpserver.upload.UploadManager;
import com.lzy.okhttputils.OkHttpUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.Request;
import okhttp3.Response;

public class UploadFragment extends Fragment implements View.OnClickListener, ExecutorWithListener.OnAllTaskEndListener {

    private GridView gridView;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        view.findViewById(R.id.select).setOnClickListener(this);
        view.findViewById(R.id.upload).setOnClickListener(this);
//        view.findViewById(R.id.qiniu).setOnClickListener(this);
        gridView = (GridView) view.findViewById(R.id.gridView);

        final TextView tvCorePoolSize = (TextView) view.findViewById(R.id.tvCorePoolSize);
        SeekBar sbCorePoolSize = (SeekBar) view.findViewById(R.id.sbCorePoolSize);
        sbCorePoolSize.setMax(3);
        sbCorePoolSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                UploadManager.getInstance(getContext()).getThreadPool().setCorePoolSize(progress);
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
        UploadManager.getInstance(getContext()).getThreadPool().getExecutor().addOnAllTaskEndListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //记得移除
        UploadManager.getInstance(getContext()).getThreadPool().getExecutor().removeOnAllTaskEndListener(this);
    }

    @Override
    public void onAllTaskEnd() {
        for (UploadInfo uploadInfo : UploadManager.getInstance(getContext()).getAllTask()) {
            if (uploadInfo.getState() != UploadManager.FINISH) {
                Toast.makeText(getContext(), "所有上传线程结束，部分上传未完成", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Toast.makeText(getContext(), "所有上传任务完成", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select:
                imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new GlideImageLoader());
                imagePicker.setShowCamera(true);
                imagePicker.setSelectLimit(9);
                imagePicker.setCrop(false);
                Intent intent = new Intent(getContext(), ImageGridActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.upload:
                if (images != null) {
                    for (int i = 0; i < images.size(); i++) {
                        MyUploadListener listener = new MyUploadListener();
                        listener.setUserTag(gridView.getChildAt(i));
                        UploadManager.getInstance(getContext()).addTask(Urls.URL_FORM_UPLOAD, new File(images.get(i).path), "imageFile", listener);
                    }
                }
                break;
//            case R.id.qiniu:
//                uploadQiniu();
//                break;
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
                Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
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
                convertView = View.inflate(getContext(), R.layout.item_upload_manager, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            imagePicker.getImageLoader().displayImage(getActivity(), getItem(position).path, holder.imageView, size, size);
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
            Log.e("MyUploadListener", "onProgress:" + uploadInfo.getFileName() + " " + uploadInfo.getTotalLength() + " " + uploadInfo.getUploadLength() + " " + uploadInfo.getProgress());
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
            Log.e("MyUploadListener", "parseNetworkResponse");
            return response.body().string();
        }
    }

//    private void uploadQiniu() {
//        Random random = new Random();
//        System.out.println("---------" + images.get(0).path);
//        OkHttpUtils.put("http://upload.qiniu.com")//
//                .tag(this)//
//                .params("key", "test" + random.nextInt(1000))//
//                .params("x:aaa", "aaa")//
//                .params("token", QiniuToken.getToken())//
//                .params("file", new File(images.get(0).path))//
//                .execute(new DialogCallback<String>(getActivity(), String.class) {
//                    @Override
//                    public void onResponse(boolean isFromCache, String s, Request request, Response response) {
//                        System.out.println("onResponse:" + s);
//                    }
//                });
//    }
}
