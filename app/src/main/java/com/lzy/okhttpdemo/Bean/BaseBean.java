package com.lzy.okhttpdemo.Bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/14
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class BaseBean implements Serializable{

    public int error;
    public DataBean data;
    public String method;
    public String url;

    public class DataBean {
        public String nohttp;
        public String yolanda;

        @Override
        public String toString() {
            return "DataBean{" +
                    "nohttp='" + nohttp + '\'' +
                    ", yolanda='" + yolanda + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "error=" + error +
                ", data=" + data +
                ", method='" + method + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
