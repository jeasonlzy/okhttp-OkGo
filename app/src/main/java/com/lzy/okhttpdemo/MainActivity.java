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
import com.lzy.okhttpdemo.cache.CacheDemoActivity;
import com.lzy.okhttpdemo.okhttpserver.DownloadActivity;
import com.lzy.okhttpdemo.okhttpserver.UploadActivity;
import com.lzy.okhttpdemo.okhttputils.OkHttpActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    private ArrayList<OkHttpModel> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar(toolbar, false, "");

        initData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(new MainAdapter(this));
    }

    @OnClick(R.id.fab)
    public void fab(View view) {
        startActivity(new Intent(this, WebActivity.class));
    }

    private void initData() {
        items = new ArrayList<>();
        OkHttpModel model1 = new OkHttpModel();
        model1.title = "";
        model1.des = "下面是OkHttpUtils包的使用方法";
        model1.type = 1;
        items.add(model1);
        OkHttpModel model2 = new OkHttpModel();
        model2.title = "标准请求(OkHttpUtils)";
        model2.des = "这里面包含了OkHttpUtils所有的使用方法,OkHttpUtils不仅支持所有请求,而且支持文件上传和下载进度监听回调,总之你想要的功能,这里面都可以找到";
        model2.type = 0;
        items.add(model2);
        OkHttpModel model3 = new OkHttpModel();
        model3.title = "缓存示例 -- 先联网获取数据,然后断开网络再进试试";
        model3.des = "OkHttpUtils的强大的缓存功能,让你代码无需关心数据来源,专注于业务逻辑的实现,五种缓存模式满足你各种使用场景";
        model3.type = 0;
        items.add(model3);
        OkHttpModel model4 = new OkHttpModel();
        model4.title = "";
        model4.des = "下面是OkHttpServer包的使用方法";
        model4.type = 1;
        items.add(model4);
        OkHttpModel model5 = new OkHttpModel();
        model5.title = "下载管理(OkHttpServer)";
        model5.des = "这个属于OkHttpServer依赖中的功能,并不属于OkHttpUtils,这个包维护较少,一般情况下,不做特殊的下载管理功能是不建议使用这个包的,OkHttpUtils完全可以胜任";
        model5.type = 0;
        items.add(model5);
        OkHttpModel model6 = new OkHttpModel();
        model6.title = "上传管理(OkHttpServer)";
        model6.des = "这个同上,也属于OkHttpServer依赖中的功能,同样该包的功能OkHttpUtils完全可以胜任";
        model6.type = 0;
        items.add(model6);
    }

    @Override
    protected boolean translucentStatusBar() {
        return true;
    }

    private class MainAdapter extends BaseRecyclerAdapter<OkHttpModel, ViewHolder> {

        public MainAdapter(Context context) {
            super(context, items);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 0) {
                view = inflater.inflate(R.layout.item_main_list, parent, false);
            } else {
                view = inflater.inflate(R.layout.item_main_type, parent, false);
            }
            return new ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(position, items.get(position));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        TextView des;
        TextView divider;
        private int position;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            des = (TextView) itemView.findViewById(R.id.des);
            divider = (TextView) itemView.findViewById(R.id.divider);
        }

        public void bind(int position, OkHttpModel model) {
            this.position = position;
            if (model.type == 0) {
                title.setText(model.title);
                des.setText(model.des);
            } else {
                divider.setText(model.des);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (position == 1) startActivity(new Intent(MainActivity.this, OkHttpActivity.class));
            if (position == 2) startActivity(new Intent(MainActivity.this, CacheDemoActivity.class));
            if (position == 4) startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            if (position == 5) startActivity(new Intent(MainActivity.this, UploadActivity.class));
        }
    }

    private class OkHttpModel {
        public String title;
        public String des;
        public int type;
    }
}