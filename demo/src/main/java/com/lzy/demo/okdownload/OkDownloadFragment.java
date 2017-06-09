package com.lzy.demo.okdownload;

import android.content.Intent;

import com.lzy.demo.base.MainFragment;
import com.lzy.demo.model.ItemModel;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class OkDownloadFragment extends MainFragment {

    @Override
    public void fillData(List<ItemModel> items) {
        items.add(new ItemModel("下载管理(OkServer)",//
                                "1.这个属于OkServer依赖中的功能,并不属于OkGo\n" +//
                                "2.这个包维护较少,一般情况下,不做特殊的下载管理功能,OkGo完全可以胜任\n" +//
                                "3.相比OkGo主要是多了断点下载和下载状态的管理"));

    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) startActivity(new Intent(context, DownloadActivity.class));
    }
}
