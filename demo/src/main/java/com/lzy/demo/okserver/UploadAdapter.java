package com.lzy.demo.okserver;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.ui.ProgressPieView;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.okgo.model.Progress;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
class UploadAdapter extends BaseAdapter {

    private List<ImageItem> items;
    private Activity activity;
    private int height;

    UploadAdapter(Activity activity, List<ImageItem> items, int height) {
        this.activity = activity;
        this.items = items;
        this.height = height;
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(activity, R.layout.item_upload_manager, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageItem item = getItem(position);
        holder.bind(item);
        return convertView;
    }

    public class ViewHolder {

        @Bind(R.id.imageView) ImageView imageView;
        @Bind(R.id.tvProgress) TextView tvProgress;
        @Bind(R.id.civ) ProgressPieView civ;
        @Bind(R.id.mask) View mask;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            imageView.setLayoutParams(params);
            tvProgress.setText("请上传");
            civ.setText("请上传");
        }

        void refresh(Progress progress) {
            switch (progress.status) {
                case Progress.NONE:
                    tvProgress.setText("请上传");
                    civ.setText("请上传");
                    break;
                case Progress.ERROR:
                    tvProgress.setText("上传出错");
                    civ.setText("错误");
                    break;
                case Progress.WAITING:
                    tvProgress.setText("等待中");
                    civ.setText("等待");
                    break;
                case Progress.FINISH:
                    tvProgress.setText("上传成功");
                    civ.setText("成功");
                    break;
                case Progress.LOADING:
                    tvProgress.setText("上传中");
                    civ.setProgress((int) (progress.fraction * 100));
                    civ.setText((Math.round(progress.fraction * 10000) * 1.0f / 100) + "%");
                    break;
            }
        }

        void finish() {
            tvProgress.setText("上传成功");
            civ.setVisibility(View.GONE);
            mask.setVisibility(View.GONE);
        }

        public void bind(ImageItem item) {
            ImagePicker.getInstance().getImageLoader().displayImage(activity, item.path, imageView, height, height);
        }
    }
}
