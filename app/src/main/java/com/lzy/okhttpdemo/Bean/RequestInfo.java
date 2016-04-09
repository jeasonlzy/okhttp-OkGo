package com.lzy.okhttpdemo.Bean;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/7
 * 描    述：我的Github地址  https://github.com/jeasonlzy0216
 * 修订历史：
 * ================================================
 */
public class RequestInfo implements Serializable{
    public String method;
    public String ip;
    public String url;
    public String des;
    public String upload;
    public Author author;

    public class Author implements Serializable{
        public String name;
        public String fullname;
        public String github;
        public String address;
        public String qq;
        public String email;
        public String des;

        @Override
        public String toString() {
            return "Author{" +
                    "name='" + name + '\'' +
                    ", fullname='" + fullname + '\'' +
                    ", github='" + github + '\'' +
                    ", address='" + address + '\'' +
                    ", qq='" + qq + '\'' +
                    ", email='" + email + '\'' +
                    ", des='" + des + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "RequestInfo{" +
                "method='" + method + '\'' +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                ", des='" + des + '\'' +
                ", upload='" + upload + '\'' +
                ", author=" + author +
                '}';
    }
}
