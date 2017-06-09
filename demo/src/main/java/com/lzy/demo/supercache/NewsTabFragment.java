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
package com.lzy.demo.supercache;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.demo.R;
import com.lzy.demo.base.BaseFragment;
import com.lzy.demo.model.GankModel;
import com.lzy.demo.model.GankResponse;
import com.lzy.demo.utils.Urls;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NewsTabFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.RequestLoadMoreListener {

    private static final int PAGE_SIZE = 20;

    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    @Bind(R.id.refreshLayout) SwipeRefreshLayout refreshLayout;

    private Context context;
    private int currentPage = 2;
    private NewsAdapter newsAdapter;
    private String url;
    private boolean isInitCache = false;

    public static NewsTabFragment newInstance() {
        return new NewsTabFragment();
    }

    @Override
    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_refresh, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        url = Urls.URL_GANK_BASE + fragmentTitle + "/" + PAGE_SIZE + "/";
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new NewsAdapter(null);
        newsAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        newsAdapter.isFirstOnly(false);
        recyclerView.setAdapter(newsAdapter);

        refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        refreshLayout.setOnRefreshListener(this);
        newsAdapter.setOnLoadMoreListener(this);

        //开启loading,获取数据
        setRefreshing(true);
        onRefresh();
    }

    /** 下拉刷新 */
    @Override
    public void onRefresh() {
        OkGo.<GankResponse<List<GankModel>>>get(url + "1")//
                .cacheKey("TabFragment_" + fragmentTitle)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new NewsCallback<GankResponse<List<GankModel>>>() {
                    @Override
                    public void onSuccess(Response<GankResponse<List<GankModel>>> response) {
                        List<GankModel> results = response.body().results;
                        if (results != null) {
                            currentPage = 2;
                            newsAdapter.setNewData(results);
                        }
                    }

                    @Override
                    public void onCacheSuccess(Response<GankResponse<List<GankModel>>> response) {
                        //一般来说,只需呀第一次初始化界面的时候需要使用缓存刷新界面,以后不需要,所以用一个变量标识
                        if (!isInitCache) {
                            //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                            onSuccess(response);
                            isInitCache = true;
                        }
                    }

                    @Override
                    public void onError(Response<GankResponse<List<GankModel>>> response) {
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(response.getException().getMessage());
                    }

                    @Override
                    public void onFinish() {
                        //可能需要移除之前添加的布局
                        newsAdapter.removeAllFooterView();
                        //最后调用结束刷新的方法
                        setRefreshing(false);
                    }
                });
    }

    /** 上拉加载 */
    @Override
    public void onLoadMoreRequested() {
        OkGo.<GankResponse<List<GankModel>>>get(url + currentPage)//
                .cacheMode(CacheMode.NO_CACHE)       //上拉不需要缓存
                .execute(new NewsCallback<GankResponse<List<GankModel>>>() {
                    @Override
                    public void onSuccess(Response<GankResponse<List<GankModel>>> response) {
                        List<GankModel> results = response.body().results;
                        if (results != null && results.size() > 0) {
                            currentPage++;
                            newsAdapter.addData(results);
                        } else {
                            //显示没有更多数据
                            newsAdapter.loadComplete();
                            View noDataView = inflater.inflate(R.layout.item_no_data, (ViewGroup) recyclerView.getParent(), false);
                            newsAdapter.addFooterView(noDataView);
                        }
                    }

                    @Override
                    public void onError(Response<GankResponse<List<GankModel>>> response) {
                        //显示数据加载失败,点击重试
                        newsAdapter.showLoadMoreFailedView();
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(response.getException().getMessage());
                    }
                });
    }

    public void showToast(String msg) {
        Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT).show();
    }

    public void setRefreshing(final boolean refreshing) {
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(refreshing);
            }
        });
    }
}
