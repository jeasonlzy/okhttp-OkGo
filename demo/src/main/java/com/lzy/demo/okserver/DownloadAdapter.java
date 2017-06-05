package com.lzy.demo.okserver;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lzy.demo.R;
import com.lzy.demo.model.ApkModel;
import com.lzy.demo.ui.NumberProgressBar;
import com.lzy.demo.utils.ApkUtils;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

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
public class DownloadAdapter extends BaseAdapter {

    private Context context;
    private List<DownloadTask> values;
    private NumberFormat numberFormat;

    public DownloadAdapter(Context context, List<DownloadTask> values) {
        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public DownloadTask getItem(int position) {
        return values.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_download_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        DownloadTask task = getItem(position);
        holder.refresh(task);

        //对于非进度更新的ui放在这里，对于实时更新的进度ui，放在holder中
        ApkModel apk = (ApkModel) task.progress.extra1;
        if (apk != null) {
            Glide.with(context).load(apk.getIconUrl()).error(R.mipmap.ic_launcher).into(holder.icon);
            holder.name.setText(apk.getName());
        } else {
            holder.name.setText(task.progress.fileName);
        }

        DownloadListener downloadListener = new ListDownloadListener(holder);
        task.register(downloadListener);
        return convertView;
    }

    public class ViewHolder {

        @Bind(R.id.icon) ImageView icon;
        @Bind(R.id.name) TextView name;
        @Bind(R.id.downloadSize) TextView downloadSize;
        @Bind(R.id.tvProgress) TextView tvProgress;
        @Bind(R.id.netSpeed) TextView netSpeed;
        @Bind(R.id.pbProgress) NumberProgressBar pbProgress;
        @Bind(R.id.start) Button download;

        private DownloadTask task;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }

        public void refresh(DownloadTask task) {
            this.task = task;
            refresh();
        }

        //对于实时更新的进度ui，放在这里，例如进度的显示，而图片加载等，不要放在这，会不停的重复回调
        //也会导致内存泄漏
        public void refresh() {
            Progress progress = task.progress;
            String currentSize = Formatter.formatFileSize(context, progress.currentSize);
            String totalSize = Formatter.formatFileSize(context, progress.totalSize);
            downloadSize.setText(currentSize + "/" + totalSize);
            switch (progress.status) {
                case Progress.NONE:
                    netSpeed.setText("停止");
                    download.setText("下载");
                    break;
                case Progress.PAUSE:
                    netSpeed.setText("暂停中");
                    download.setText("继续");
                    break;
                case Progress.ERROR:
                    netSpeed.setText("下载出错");
                    download.setText("出错");
                    break;
                case Progress.WAITING:
                    netSpeed.setText("等待中");
                    download.setText("等待");
                    break;
                case Progress.FINISH:
                    if (ApkUtils.isAvailable(context, new File(progress.filePath))) {
                        download.setText("卸载");
                    } else {
                        download.setText("安装");
                    }
                    netSpeed.setText("下载完成");
                    break;
                case Progress.LOADING:
                    String speed = Formatter.formatFileSize(context, progress.speed);
                    netSpeed.setText(String.format("%s/s", speed));
                    download.setText("暂停");
                    break;
            }
            tvProgress.setText(numberFormat.format(progress.fraction));
            pbProgress.setMax(10000);
            pbProgress.setProgress((int) (progress.fraction * 10000));
        }

        @OnClick(R.id.start)
        public void start() {
            switch (task.progress.status) {
                case Progress.PAUSE:
                case Progress.NONE:
                case Progress.ERROR:
                    task.start();
                    break;
                case Progress.LOADING:
                    task.pause();
                    break;
                case Progress.FINISH:
                    if (ApkUtils.isAvailable(context, new File(task.progress.filePath))) {
                        ApkUtils.uninstall(context, ApkUtils.getPackageName(context, task.progress.filePath));
                    } else {
                        ApkUtils.install(context, new File(task.progress.filePath));
                    }
                    break;
            }
            refresh();
        }

        @OnClick(R.id.remove)
        public void remove() {
            OkDownload.getInstance().remove(task.progress.tag);
            values = new ArrayList<>(OkDownload.getInstance().getTaskMap().values());
            notifyDataSetChanged();
        }

        @OnClick(R.id.restart)
        public void restart() {
            task.restart();
        }
    }

    private class ListDownloadListener extends DownloadListener {

        public ListDownloadListener(Object tag) {
            super(tag);
        }

        @Override
        public void onFinish(File file, Progress progress) {
            Toast.makeText(context, "下载完成:" + progress.filePath, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Progress progress) {
            Throwable throwable = progress.exception;
            if (throwable != null) throwable.printStackTrace();
        }

        @Override
        public void onProgress(Progress progress) {
            if (tag == null) return;
            DownloadAdapter.ViewHolder holder = (DownloadAdapter.ViewHolder) tag;
            holder.refresh();  //这里不能使用传递进来的 Progress，否者会出现条目错乱的问题
        }
    }
}