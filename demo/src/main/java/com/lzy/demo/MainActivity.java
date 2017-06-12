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
package com.lzy.demo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;

import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.okdownload.OkDownloadFragment;
import com.lzy.demo.okgo.OkGoFragment;
import com.lzy.demo.okrx2.OkRx2Fragment;
import com.lzy.demo.okrx2.OkRxFragment;
import com.lzy.demo.okupload.OkUploadFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class MainActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.tab) TabLayout tab;

    private List<Pair<String, Fragment>> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolBar(toolbar, false, "");

        items = new ArrayList<>();
        items.add(new Pair<String, Fragment>("OkGo", new OkGoFragment()));
        items.add(new Pair<String, Fragment>("打赏", new PayFragment()));
        items.add(new Pair<String, Fragment>("OkRx2", new OkRx2Fragment()));
        items.add(new Pair<String, Fragment>("OkRx", new OkRxFragment()));
        items.add(new Pair<String, Fragment>("OkDownload", new OkDownloadFragment()));
        items.add(new Pair<String, Fragment>("OkUpload", new OkUploadFragment()));

        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager()));
        tab.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.fab)
    public void fab(View view) {
        WebActivity.runActivity(this, "我的Github,欢迎star", "https://github.com/jeasonlzy");
    }

    @Override
    protected boolean translucentStatusBar() {
        return true;
    }

    private class MainAdapter extends FragmentPagerAdapter {

        MainAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return items.get(position).second;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return items.get(position).first;
        }
    }
}
