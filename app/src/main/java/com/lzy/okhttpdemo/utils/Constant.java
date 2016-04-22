package com.lzy.okhttpdemo.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/6
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */
public class Constant {

    private static ArrayList<String[]> data;

    private Constant() {
    }

    public static List<String[]> getData() {
        if (data == null) {
            data = new ArrayList<>();
            data.add(new String[]{"请求方法演示", "目前支持 GET，HEAD，OPTIONS，POST，PUT，DELETE"});
            data.add(new String[]{"自定义请求对象", "可以用 gson 或 fastjson 解析javaBean,或者对应的集合对象"});
            data.add(new String[]{"请求图片", "请求服务器返回bitmap对象"});
            data.add(new String[]{"上传长文本数据", "使用post请求向服务器上传 json 字符串或者 String 类型字符串，此外也可以使用下面的表单上传，将长文本作为一个参数值上传"});
            data.add(new String[]{"表单上传", "类似网页中，多参数（可以是长文本）和多文件同时上传到服务器，大文件或小文件都可以，同时支持监听上传进度"});
            data.add(new String[]{"文件下载", "可以做大文件或小文件下载，同时支持监听下载进度"});
            data.add(new String[]{"网络缓存", "默认提供了四种缓存模式，根据需要选择使用"});
            data.add(new String[]{"https请求", "允许访问拥有可信证书的网站，也支持自签名证书的网站"});
            data.add(new String[]{"同步请求", "允许直接返回Response对象，会阻塞主线程，需要自行开启子线程"});
            data.add(new String[]{"301重定向", "支持301重定向请求"});
        }
        return data;
    }
}
