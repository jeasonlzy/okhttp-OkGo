package com.lzy.okhttpdemo.okhttputils;

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

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseActivity;
import com.lzy.okhttpdemo.base.BaseRecyclerAdapter;
import com.lzy.okhttpdemo.base.DividerItemDecoration;
import com.lzy.okhttpdemo.utils.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;

public class OkHttpActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok_http);
        initToolBar(toolbar, true, "OkHttpUtils功能介绍");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    private class MainAdapter extends BaseRecyclerAdapter<String[], ViewHolder> {

        public MainAdapter(Context context) {
            super(context, Constant.getData());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.item_main_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
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
            if (position == 0) startActivity(new Intent(OkHttpActivity.this, MethodActivity.class));
            if (position == 1) startActivity(new Intent(OkHttpActivity.this, CustomRequestActivity.class));
            if (position == 2) startActivity(new Intent(OkHttpActivity.this, BitmapRequestActivity.class));
            if (position == 3) startActivity(new Intent(OkHttpActivity.this, PostTextActivity.class));
            if (position == 4) startActivity(new Intent(OkHttpActivity.this, FormUploadActivity.class));
            if (position == 5) startActivity(new Intent(OkHttpActivity.this, FileDownloadActivity.class));
            if (position == 6) startActivity(new Intent(OkHttpActivity.this, CacheActivity.class));
            if (position == 7) startActivity(new Intent(OkHttpActivity.this, HttpsActivity.class));
            if (position == 8) startActivity(new Intent(OkHttpActivity.this, SyncActivity.class));
            if (position == 9) startActivity(new Intent(OkHttpActivity.this, RedirectActivity.class));
            if (position == 10) startActivity(new Intent(OkHttpActivity.this, TestActivity.class));
        }
    }
}