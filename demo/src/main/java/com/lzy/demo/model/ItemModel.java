package com.lzy.demo.model;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ItemModel {
    public String title;
    public String des;
    public int type;

    public ItemModel() {
    }

    public ItemModel(String title, String des) {
        this.title = title;
        this.des = des;
    }

    public ItemModel(String title, String des, int type) {
        this.title = title;
        this.des = des;
        this.type = type;
    }
}
