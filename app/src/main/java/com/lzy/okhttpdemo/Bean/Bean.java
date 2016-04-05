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
public class Bean implements Serializable{
    public String ip;
    public String host;
    public String port;
    public String connection;
    public String aaa;
    public String bbb;
    public String ccc;
    public String xxx;
    public String yyy;
    public String zzz;

    @Override
    public String toString() {
        return "Bean{" +
                "ip='" + ip + '\'' +
                ", host='" + host + '\'' +
                ", port='" + port + '\'' +
                ", connection='" + connection + '\'' +
                ", aaa='" + aaa + '\'' +
                ", bbb='" + bbb + '\'' +
                ", ccc='" + ccc + '\'' +
                ", xxx='" + xxx + '\'' +
                ", yyy='" + yyy + '\'' +
                ", zzz='" + zzz + '\'' +
                '}';
    }
}
