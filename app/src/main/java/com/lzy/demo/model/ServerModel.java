package com.lzy.demo.model;

import java.io.Serializable;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）
 * 版    本：1.0
 * 创建日期：2016/4/7
 * 描    述：我的Github地址  https://github.com/jeasonlzy
 * 修订历史：
 * ================================================
 */
public class ServerModel implements Serializable{
    private static final long serialVersionUID = -828322761336296999L;

    public String method;
    public String ip;
    public String url;
    public String des;
    public String upload;
    public Author author;

    public class Author implements Serializable{
        private static final long serialVersionUID = 2701611773813762723L;

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
        return "ServerModel{" +
                "method='" + method + '\'' +
                ", ip='" + ip + '\'' +
                ", url='" + url + '\'' +
                ", des='" + des + '\'' +
                ", upload='" + upload + '\'' +
                ", author=" + author +
                '}';
    }
}
