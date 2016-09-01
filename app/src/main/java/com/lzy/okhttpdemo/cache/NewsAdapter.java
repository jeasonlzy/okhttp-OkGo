package com.lzy.okhttpdemo.cache;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.ninegrid.NineGridView;
import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.WebActivity;
import com.lzy.okhttpdemo.base.BaseRecyclerAdapter;
import com.lzy.okhttpdemo.model.NewsModel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/8/17
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NewsAdapter extends BaseRecyclerAdapter<NewsModel.ContentList, NewsAdapter.TestViewHolder> {

    public NewsAdapter(Context context) {
        super(context);
    }

    @Override
    public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_news, parent, false);
        return new TestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TestViewHolder holder, int position) {
        holder.bind(mDatas.get(position));
    }

    public class TestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title) TextView title;
        @Bind(R.id.nineGrid) NineGridView nineGrid;
        @Bind(R.id.desc) TextView desc;
        @Bind(R.id.pubDate) TextView pubDate;
        @Bind(R.id.source) TextView source;

        private NewsModel.ContentList item;

        public TestViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(NewsModel.ContentList item) {
            this.item = item;
            title.setText(item.title);
            desc.setText(item.desc);
            pubDate.setText(item.pubDate);
            source.setText(item.source);

//            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
//            List<NewsModel.NewsImage> images = item.imageurls;
//            if (images != null) {
//                for (NewsModel.NewsImage image : images) {
//                    ImageInfo info = new ImageInfo();
//                    info.setThumbnailUrl(image.url);
//                    info.setBigImageUrl(image.url);
//                    imageInfo.add(info);
//                }
//            }
//            nineGrid.setAdapter(new NineGridViewClickAdapter(mContext, imageInfo));

//            if (images != null && images.size() == 1) {
//                nineGrid.setSingleImageRatio(images.get(0).width * 1.0f / images.get(0).height);
//            }

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            WebActivity.runActivity(mContext, item.title, item.link);
        }
    }
}