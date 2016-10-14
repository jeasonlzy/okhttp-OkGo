package com.lzy.demo.okrx;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lzy.demo.R;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.base.BaseRecyclerAdapter;
import com.lzy.demo.base.DividerItemDecoration;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RxActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private ArrayList<String[]> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler);
        initToolBar(toolbar, true, "OkRx使用示例");

        initData();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new RxActivity.MainAdapter(this));
    }

    private void initData() {
        data = new ArrayList<>();
        data.add(new String[]{"基本请求", "基本的使用方法,包括JsonCallback解析,上传Json文本等"});
        data.add(new String[]{"请求图片", "请求服务器返回bitmap对象"});
        data.add(new String[]{"文件上传", "支持参数和文件一起上传,并回调上传进度"});
        data.add(new String[]{"文件下载", "支持下载进度回调"});
    }

    private class MainAdapter extends BaseRecyclerAdapter<String[], RxActivity.ViewHolder> {

        public MainAdapter(Context context) {
            super(context, data);
        }

        @Override
        public RxActivity.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_main_list, parent, false);
            return new RxActivity.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RxActivity.ViewHolder holder, int position) {
            String[] strings = mDatas.get(position);
            holder.bind(position, strings);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @Bind(R.id.title) TextView title;
        @Bind(R.id.des) TextView des;

        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(int position, String[] strings) {
            this.position = position;
            title.setText(strings[0]);
            des.setText(strings[1]);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position == 0) startActivity(new Intent(RxActivity.this, RxCommonActivity.class));
            if (position == 1) startActivity(new Intent(RxActivity.this, RxBitmapActivity.class));
            if (position == 2) startActivity(new Intent(RxActivity.this, RxFormUploadActivity.class));
            if (position == 3) startActivity(new Intent(RxActivity.this, RxFileDownloadActivity.class));
        }
    }
}
