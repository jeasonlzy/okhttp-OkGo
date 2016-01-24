package com.lzy.okhttpdemo.Bean;

import java.io.Serializable;

public class ApkInfo implements Serializable{
    private String name;
    private String url;
    private String iconUrl;

    public ApkInfo() {
    }

    public ApkInfo(String name, String url, String iconUrl) {
        this.name = name;
        this.url = url;
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
