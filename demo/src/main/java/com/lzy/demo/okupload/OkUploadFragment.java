package com.lzy.demo.okupload;

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
public class OkUploadFragment extends MainFragment {

    @Override
    public void fillData(List<ItemModel> items) {
        items.add(new ItemModel("上传管理(OkServer)",//
                                "1.这个同上,也属于OkServer依赖中的功能\n" +//
                                "2.同样该包的功能OkGo完全可以胜任\n" +//
                                "3.上传只是简单上传管理,不支持断点上传或者分片上传"));
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) startActivity(new Intent(context, UploadActivity.class));
    }
}
