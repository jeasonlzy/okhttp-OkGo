package com.lzy.demo;

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

import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.base.BaseRecyclerAdapter;
import com.lzy.demo.base.DividerItemDecoration;
import com.lzy.demo.cache.CacheDemoActivity;
import com.lzy.demo.okgo.FileDownloadActivity;
import com.lzy.demo.okgo.FormUploadActivity;
import com.lzy.demo.okgo.JsonRequestActivity;
import com.lzy.demo.okgo.OkHttpActivity;
import com.lzy.demo.okrx.RxActivity;
import com.lzy.demo.okrx.RxCommonActivity;
import com.lzy.demo.okserver.DownloadActivity;
import com.lzy.demo.okserver.UploadActivity;

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
        WebActivity.runActivity(this, "我的Github,欢迎star", "https://github.com/jeasonlzy");
    }

    private void initData() {
        items = new ArrayList<>();

        OkHttpModel model1 = new OkHttpModel();
        model1.title = "";
        model1.des = "OkRx扩展2.0震撼来袭";
        model1.type = 1;
        items.add(model1);

        OkHttpModel model2 = new OkHttpModel();
        model2.title = "OkRx使用方法示例";
        model2.des = "1.完美结合RxJava\n" +
                     "2.比Retrofit更简单方便\n" +
                     "3.网络请求和RxJava调用,一条链点到底\n" +
                     "4.支持Json数据的自动解析转换";
        model2.type = 0;
        items.add(model2);

        OkHttpModel model3 = new OkHttpModel();
        model3.title = "";
        model3.des = "下面是OkGo包的使用方法";
        model3.type = 1;
        items.add(model3);

        OkHttpModel model4 = new OkHttpModel();
        model4.title = "基本功能(OkGo)";
        model4.des = "1.GET，HEAD，OPTIONS，POST，PUT，DELETE 请求方法演示\n" +
                     "2.请求服务器返回bitmap对象\n" +
                     "3.支持https请求\n" +
                     "4.支持同步请求\n" +
                     "5.支持301重定向";
        model4.type = 0;
        items.add(model4);

        OkHttpModel model5 = new OkHttpModel();
        model5.title = "自动解析Json对象";
        model5.des = "1.自动解析JavaBean对象\n" + //
                     "2.自动解析List<JavaBean>集合对象";
        model5.type = 0;
        items.add(model5);

        OkHttpModel model6 = new OkHttpModel();
        model6.title = "文件下载";
        model6.des = "1.支持大文件或小文件下载，无论多大文件都不会发生OOM\n" +
                     "2.支持监听下载进度和下载网速\n" +
                     "3.支持自定义下载目录和下载文件名";
        model6.type = 0;
        items.add(model6);

        OkHttpModel model7 = new OkHttpModel();
        model7.title = "文件上传";
        model7.des = "1.支持上传单个文件\n" +
                     "2.支持同时上传多个文件\n" +
                     "3.支持多个文件多个参数同时上传\n" +
                     "4.支持大文件上传,无论多大都不会发生OOM\n" +
                     "5.支持监听上传进度和上传网速";
        model7.type = 0;
        items.add(model7);

        OkHttpModel model8 = new OkHttpModel();
        model8.title = "强大的缓存示例 -- 先联网获取数据,然后断开网络再进试试";
        model8.des = "1.OkGo的强大的缓存功能,让你代码无需关心数据来源,专注于业务逻辑的实现\n" +
                     "2.共有五种缓存模式满足你各种使用场景\n" +
                     "3.支持自定义缓存过期时间";
        model8.type = 0;
        items.add(model8);

        OkHttpModel model9 = new OkHttpModel();
        model9.title = "";
        model9.des = "下面是OkServer包的使用方法";
        model9.type = 1;
        items.add(model9);

        OkHttpModel model10 = new OkHttpModel();
        model10.title = "下载管理(OkServer)";
        model10.des = "1.这个属于OkServer依赖中的功能,并不属于OkGo\n" +
                      "2.这个包维护较少,一般情况下,不做特殊的下载管理功能,OkGo完全可以胜任\n" +
                      "3.相比OkGo主要是多了断点下载和下载状态的管理";
        model10.type = 0;
        items.add(model10);

        OkHttpModel model11 = new OkHttpModel();
        model11.title = "上传管理(OkServer)";
        model11.des = "1.这个同上,也属于OkServer依赖中的功能\n" +
                      "2.同样该包的功能OkGo完全可以胜任\n" +
                      "3.上传只是简单上传管理,不支持断点上传或者分片上传";
        model11.type = 0;
        items.add(model11);
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
            if (position == 1) startActivity(new Intent(MainActivity.this, RxActivity.class));
            if (position == 3) startActivity(new Intent(MainActivity.this, OkHttpActivity.class));
            if (position == 4) startActivity(new Intent(MainActivity.this, JsonRequestActivity.class));
            if (position == 5) startActivity(new Intent(MainActivity.this, FileDownloadActivity.class));
            if (position == 6) startActivity(new Intent(MainActivity.this, FormUploadActivity.class));
            if (position == 7) startActivity(new Intent(MainActivity.this, CacheDemoActivity.class));
            if (position == 9) startActivity(new Intent(MainActivity.this, DownloadActivity.class));
            if (position == 10) startActivity(new Intent(MainActivity.this, UploadActivity.class));
        }
    }

    private class OkHttpModel {
        public String title;
        public String des;
        public int type;
    }
}