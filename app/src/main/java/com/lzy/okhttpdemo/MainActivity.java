package com.lzy.okhttpdemo;

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

import com.lzy.okhttpdemo.base.BaseActivity;
import com.lzy.okhttpdemo.base.BaseRecyclerAdapter;
import com.lzy.okhttpdemo.base.DividerItemDecoration;
import com.lzy.okhttpdemo.okhttpserver.DownloadActivity;
import com.lzy.okhttpdemo.okhttpserver.UploadActivity;
import com.lzy.okhttpdemo.okhttputils.OkHttpActivity;
import com.lzy.okhttputils.OkHttpUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    private String[] titles = {"标准请求(OkHttpUtils依赖)", "下载管理(OkHttpServer依赖)", "上传管理(OkHttpServer依赖)"};
    private String[] dess = {//
            "这里面包含了OkHttpUtils所有的使用方法,OkHttpUtils不仅支持所有请求,而且支持文件上传和下载进度监听回调,总之你想要的功能,这里面都可以找到", //
            "这个属于OkHttpServer依赖中的功能,并不属于OkHttpUtils,这个包维护较少,一般情况下,不做特殊的下载管理功能是不建议使用这个包的,OkHttpUtils完全可以胜任", //
            "这个同上,也属于OkHttpServer依赖中的功能,同样该包的功能OkHttpUtils完全可以胜任"};

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar(toolbar, false, "");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    @Override
    protected boolean translucentStatusBar() {
        return true;
    }

    private class MainAdapter extends BaseRecyclerAdapter<String, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, titles);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_main_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position);
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

        public void bind(int position) {
            this.position = position;
            title.setText(titles[position]);
            des.setText(dess[position]);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position == 0) startActivity(new Intent(MainActivity.this, OkHttpActivity.class));
            if (position == 1) startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            if (position == 2) startActivity(new Intent(MainActivity.this, UploadActivity.class));
        }
    }
}