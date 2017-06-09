package com.lzy.demo.okrx2;

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
public class OkRx2Fragment extends MainFragment {

    @Override
    public void fillData(List<ItemModel> items) {
        items.add(new ItemModel("基本请求", //
                                "基本的使用方法,包括JsonCallback解析,上传Json文本等\n" +//
                                "1.完美结合RxJava\n" +//
                                "2.比Retrofit更简单方便\n" +//
                                "3.网络请求和RxJava调用,一条链点到底\n" +//
                                "4.支持Json数据的自动解析转换"));
        items.add(new ItemModel("请求图片", "请求服务器返回bitmap对象"));
        items.add(new ItemModel("文件上传", "支持参数和文件一起上传,并回调上传进度"));
        items.add(new ItemModel("文件下载", "支持下载进度回调"));
    }

    @Override
    public void onItemClick(int position) {
        if (position == 0) startActivity(new Intent(context, RxCommonActivity.class));
        if (position == 1) startActivity(new Intent(context, RxBitmapActivity.class));
        if (position == 2) startActivity(new Intent(context, RxFormUploadActivity.class));
        if (position == 3) startActivity(new Intent(context, RxFileDownloadActivity.class));
    }
}
