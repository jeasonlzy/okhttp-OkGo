package com.lzy.okhttpdemo.cache;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.base.BaseFragment;
import com.lzy.okhttpdemo.model.NewsModel;
import com.lzy.okhttpdemo.utils.Urls;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cache.CacheMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Response;

public class NewsTabFragment extends BaseFragment {

    @Bind(R.id.recyclerView) RecyclerView recyclerView;

    private Context context;
    private int currentPage;
    private NewsAdapter newsAdapter;
    private List<NewsModel.ContentList> mData;

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
        View view = inflater.inflate(R.layout.fragment_tab, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initData() {
        mData = new ArrayList<>();
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new NewsAdapter(context);
        recyclerView.setAdapter(newsAdapter);
        refreshData();
    }

    private void refreshData() {
        OkHttpUtils.get(Urls.NEWS)//
                .params("channelName", fragmentTitle)//
                .params("page", String.valueOf(1))              //初始化或者下拉刷新,默认加载第一页
                .cacheKey("TabFragment_" + fragmentTitle)       //由于该fragment会被复用,必须保证key唯一,否则数据会发生覆盖
                .cacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)  //缓存模式先使用缓存,然后使用网络数据
                .execute(new NewsCallback<NewsModel>(NewsModel.class) {
                    @Override
                    public void onSuccess(NewsModel newsModel, Call call, Response response) {
                        currentPage = newsModel.pagebean.currentPage;
                        refreshView(true, newsModel.pagebean.contentlist);
                    }

                    @Override
                    public void onCacheSuccess(NewsModel newsModel, Call call) {
                        //一般来说,缓存回调成功和网络回调成功做的事情是一样的,所以这里直接回调onSuccess
                        onSuccess(newsModel, call, null);
                    }

                    @Override
                    public void onCacheError(Call call, Exception e) {
                        //获取缓存失败的回调方法,一般很少用到,需要就复写,不需要不用关心
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }
                });
    }

    private void loadMoreData() {
        OkHttpUtils.get(Urls.NEWS)//
                .params("channelName", fragmentTitle)//
                .params("page", String.valueOf(currentPage + 1)) //上拉加载更多
                .cacheMode(CacheMode.NO_CACHE)                   //上拉不需要缓存
                .execute(new NewsCallback<NewsModel>(NewsModel.class) {
                    @Override
                    public void onSuccess(NewsModel newsModel, Call call, Response response) {
                        currentPage = newsModel.pagebean.currentPage;
                        refreshView(false, newsModel.pagebean.contentlist);
                    }

                    @Override
                    public void onError(Call call, Response response, Exception e) {
                        super.onError(call, response, e);
                        //网络请求失败的回调,一般会弹个Toast
                        showToast(e.getMessage());
                    }
                });
    }

    private void refreshView(boolean isClear, List<NewsModel.ContentList> contentlist) {
        if (isClear) mData.clear();
        mData.addAll(contentlist);
        newsAdapter.updateItems(mData);
    }

    public void showToast(String msg) {
        Snackbar.make(recyclerView, msg, Snackbar.LENGTH_SHORT).show();
    }
}