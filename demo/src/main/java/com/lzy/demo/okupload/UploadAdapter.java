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
package com.lzy.demo.okupload;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.demo.R;
import com.lzy.demo.ui.NumberProgressBar;
import com.lzy.demo.utils.Urls;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.convert.StringConvert;
import com.lzy.okgo.db.UploadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okgo.request.base.Request;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.upload.UploadListener;
import com.lzy.okserver.upload.UploadTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder> {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_FINISH = 1;
    public static final int TYPE_ING = 2;

    private List<UploadTask<?>> values;
    private List<ImageItem> images;
    private NumberFormat numberFormat;
    private LayoutInflater inflater;
    private Context context;
    private int type = -1;

    public UploadAdapter(Context context) {
        this.context = context;
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(int type) {
        //这里是将数据库的数据恢复
        this.type = type;
        if (type == TYPE_ALL) values = OkUpload.restore(UploadManager.getInstance().getAll());
        if (type == TYPE_FINISH) values = OkUpload.restore(UploadManager.getInstance().getFinished());
        if (type == TYPE_ING) values = OkUpload.restore(UploadManager.getInstance().getUploading());

        //由于Converter是无法保存下来的，所以这里恢复任务的时候，需要额外传入Converter，否则就没法解析数据
        //至于数据类型，统一就行，不一定非要是String
        for (UploadTask<?> task : values) {
            //noinspection unchecked
            Request<String, ? extends Request> request = (Request<String, ? extends Request>) task.progress.request;
            request.converter(new StringConvert());
        }

        notifyDataSetChanged();
    }

    public List<UploadTask<?>> updateData(List<ImageItem> images) {
        this.type = -1;
        this.images = images;
        values = new ArrayList<>();
        if (images != null) {
            Random random = new Random();
            for (int i = 0; i < images.size(); i++) {
                ImageItem imageItem = images.get(i);
                //这里是演示可以传递任何数据
                PostRequest<String> postRequest = OkGo.<String>post(Urls.URL_FORM_UPLOAD)//
                        .headers("aaa", "111")//
                        .params("bbb", "222")//
                        .params("fileKey" + i, new File(imageItem.path))//
                        .converter(new StringConvert());

                UploadTask<String> task = OkUpload.request(imageItem.path, postRequest)//
                        .priority(random.nextInt(100))//
                        .extra1(imageItem)//
                        .save();
                values.add(task);
            }
        }
        notifyDataSetChanged();
        return values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_upload_manager, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //noinspection unchecked
        UploadTask<String> task = (UploadTask<String>) values.get(position);
        String tag = createTag(task);
        task.register(new ListUploadListener(tag, holder))//
                .register(new LogUploadListener<String>());
        holder.setTag(tag);
        holder.setTask(task);
        holder.bind();
        holder.refresh(task.progress);
    }

    public void unRegister() {
        Map<String, UploadTask<?>> taskMap = OkUpload.getInstance().getTaskMap();
        for (UploadTask<?> task : taskMap.values()) {
            task.unRegister(createTag(task));
        }
    }

    private String createTag(UploadTask task) {
        return type + "_" + task.progress.tag;
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 : values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.name) TextView name;
        @Bind(R.id.priority) TextView priority;
        @Bind(R.id.downloadSize) TextView downloadSize;
        @Bind(R.id.tvProgress) TextView tvProgress;
        @Bind(R.id.netSpeed) TextView netSpeed;
        @Bind(R.id.pbProgress) NumberProgressBar pbProgress;
        @Bind(R.id.upload) Button upload;
        private UploadTask<?> task;
        private String tag;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setTask(UploadTask<?> task) {
            this.task = task;
        }

        public void bind() {
            Progress progress = task.progress;
            ImageItem item = (ImageItem) progress.extra1;
            Glide.with(context).load(item.path).error(R.mipmap.ic_launcher).into(icon);
            name.setText(item.name);
            priority.setText(String.format("优先级：%s", progress.priority));
        }

        public void refresh(Progress progress) {
            String currentSize = Formatter.formatFileSize(context, progress.currentSize);
            String totalSize = Formatter.formatFileSize(context, progress.totalSize);
            downloadSize.setText(currentSize + "/" + totalSize);
            priority.setText(String.format("优先级：%s", progress.priority));
            switch (progress.status) {
                case Progress.NONE:
                    netSpeed.setText("停止");
                    upload.setText("上传");
                    break;
                case Progress.PAUSE:
                    netSpeed.setText("暂停中");
                    upload.setText("继续");
                    break;
                case Progress.ERROR:
                    netSpeed.setText("上传出错");
                    upload.setText("出错");
                    break;
                case Progress.WAITING:
                    netSpeed.setText("等待中");
                    upload.setText("等待");
                    break;
                case Progress.FINISH:
                    upload.setText("完成");
                    netSpeed.setText("上传成功");
                    break;
                case Progress.LOADING:
                    String speed = Formatter.formatFileSize(context, progress.speed);
                    netSpeed.setText(String.format("%s/s", speed));
                    upload.setText("停止");
                    break;
            }
            tvProgress.setText(numberFormat.format(progress.fraction));
            pbProgress.setMax(10000);
            pbProgress.setProgress((int) (progress.fraction * 10000));
        }

        @OnClick(R.id.upload)
        public void upload() {
            Progress progress = task.progress;
            switch (progress.status) {
                case Progress.PAUSE:
                case Progress.NONE:
                case Progress.ERROR:
                    task.start();
                    break;
                case Progress.LOADING:
                    task.pause();
                    break;
                case Progress.FINISH:
                    break;
            }
            refresh(progress);
        }

        @OnClick(R.id.remove)
        public void remove() {
            task.remove();
            if (type == -1) {
                int removeIndex = -1;
                for (int i = 0; i < images.size(); i++) {
                    if (images.get(i).path.equals(task.progress.tag)) {
                        removeIndex = i;
                        break;
                    }
                }
                if (removeIndex != -1) {
                    images.remove(removeIndex);
                }
                updateData(images);
            } else {
                updateData(type);
            }
        }

        @OnClick(R.id.restart)
        public void restart() {
            task.restart();
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }
    }

    private class ListUploadListener extends UploadListener<String> {

        private ViewHolder holder;

        ListUploadListener(Object tag, ViewHolder holder) {
            super(tag);
            this.holder = holder;
        }

        @Override
        public void onStart(Progress progress) {
        }

        @Override
        public void onProgress(Progress progress) {
            if (tag == holder.getTag()) {
                holder.refresh(progress);
            }
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
        }

        @Override
        public void onFinish(String s, Progress progress) {
            Toast.makeText(context, "上传完成", Toast.LENGTH_SHORT).show();
            if (type != -1) updateData(type);
        }

        @Override
        public void onRemove(Progress progress) {
        }
    }
}
