package com.lzy.demo.model;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/29
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class NewsResponse<T> {
    public String showapi_res_error;
    public int showapi_res_code;
    public T showapi_res_body;
}