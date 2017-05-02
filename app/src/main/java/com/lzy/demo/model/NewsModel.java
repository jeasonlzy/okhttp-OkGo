package com.lzy.demo.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NewsModel implements Serializable {

    private static final long serialVersionUID = 6753210234564872868L;

    public class GankBean implements Serializable {

        private static final long serialVersionUID = -3320444919726119048L;
        @SerializedName("_id")
        public String id;
        @SerializedName("createdAt")
        public Date createTime;
        public String desc;
        public String[] images;
        @SerializedName("publishedAt")
        public Date publishTime;
        public String source;
        public String type;
        public String url;
        public boolean used;
        public String who;
    }

}