package com.lzy.okhttpdemo.cache;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzy.okhttpdemo.R;
import com.lzy.okhttpdemo.WebActivity;
import com.lzy.okhttpdemo.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class CacheDemoActivity extends BaseActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.viewPager) ViewPager viewPager;
    @Bind(R.id.tab) TabLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_demo);
        initToolBar(toolbar, true, "缓存演示");

        ArrayList<Fragment> fragments = new ArrayList<>();
        ArrayList<String> strings = new ArrayList<>();
        strings.add("第一页");
        strings.add("第二页");
        strings.add("第三页");
        fragments.add(TabFragment.newInstance("第一页"));
        fragments.add(TabFragment.newInstance("第二页"));
        fragments.add(TabFragment.newInstance("第三页"));
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, strings);
        viewPager.setAdapter(adapter);
        tab.setupWithViewPager(viewPager);
    }

    @OnClick(R.id.fab)
    public void fab(View view) {
        startActivity(new Intent(this, WebActivity.class));
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments;
        private ArrayList<String> strings;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments, ArrayList<String> strings) {
            super(fm);
            this.fragments = fragments;
            this.strings = strings;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return strings.get(position);
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