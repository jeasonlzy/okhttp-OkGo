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

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzy.demo.R;
import com.lzy.demo.WebActivity;
import com.lzy.demo.base.BaseActivity;
import com.lzy.demo.utils.GlideImageLoader;
import com.lzy.ninegrid.NineGridView;

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
public class SuperCacheActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.tab) TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_cache);
        initToolBar(toolbar, true, "强大的缓存");

        NineGridView.setImageLoader(new GlideImageLoader());

        ArrayList<NewsTabFragment> fragments = new ArrayList<>();
        NewsTabFragment fragment1 = NewsTabFragment.newInstance();
        fragment1.setTitle("Android");
        fragments.add(fragment1);
        NewsTabFragment fragment2 = NewsTabFragment.newInstance();
        fragment2.setTitle("iOS");
        fragments.add(fragment2);
        NewsTabFragment fragment3 = NewsTabFragment.newInstance();
        fragment3.setTitle("前端");
        fragments.add(fragment3);
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(fragments.size());
        tab.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.fab)
    public void fab(View view) {
        WebActivity.runActivity(this, "我的Github,欢迎star", "https://github.com/jeasonlzy");
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private List<NewsTabFragment> fragments;

        public MyPagerAdapter(FragmentManager fm, List<NewsTabFragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragments.get(position).getTitle();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
