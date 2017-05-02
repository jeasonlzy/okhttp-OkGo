package com.lzy.demo.cache;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.demo.R;
import com.lzy.demo.WebActivity;
import com.lzy.demo.model.NewsModel;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NewsAdapter extends BaseQuickAdapter<NewsModel.GankBean> {

    public NewsAdapter(List<NewsModel.GankBean> data) {
        super(R.layout.item_news, data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, final NewsModel.GankBean gank) {
        baseViewHolder.setText(R.id.title, gank.desc)//
                .setText(R.id.desc, gank.desc)//
                .setText(R.id.pubDate, gank.publishTime.toString())//
                .setText(R.id.source, gank.source);

        View view = baseViewHolder.getConvertView();
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebActivity.runActivity(mContext, gank.desc, gank.url);
            }
        });

        NineGridView nineGrid = baseViewHolder.getView(R.id.nineGrid);
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        if (gank.images != null) {
            for (String image : gank.images) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfo.add(info);
            }
        }
        nineGrid.setAdapter(new NineGridViewClickAdapter(mContext, imageInfo));

        if (gank.images != null && gank.images.length == 1) {
            nineGrid.setSingleImageRatio(1 / 1);
        }
    }
}