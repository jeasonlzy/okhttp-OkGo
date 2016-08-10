package com.lzy.okhttpserver.utils;

import android.text.TextUtils;

import com.lzy.okhttputils.model.HttpHeaders;
import com.lzy.okhttputils.utils.OkLogger;

import java.io.File;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy0216
 * 版    本：1.0
 * 创建日期：16/8/9
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ServerUtils {

    /** 根据响应头或者url获取文件名 */
    public static String getNetFileName(Response response, String url) {
        String fileName = getHeaderFileName(response);
        if (TextUtils.isEmpty(fileName)) fileName = getUrlFileName(url);
        if (TextUtils.isEmpty(fileName)) fileName = "default";
        return fileName;
    }

    /** 解析文件头 Content-Disposition:attachment;filename=FileName.txt */
    private static String getHeaderFileName(Response response) {
        String dispositionHeader = response.header(HttpHeaders.HEAD_KEY_CONTENT_DISPOSITION);
        if (dispositionHeader != null) {
            String split = "filename=";
            int indexOf = dispositionHeader.indexOf(split);
            if (indexOf != -1) {
                return dispositionHeader.substring(indexOf + split.length(), dispositionHeader.length());
            }
        }
        return null;
    }

    /** 通过 ‘？’ 和 ‘/’ 判断文件名 */
    private static String getUrlFileName(String url) {
        int index = url.lastIndexOf('?');
        String filename;
        if (index > 1) {
            filename = url.substring(url.lastIndexOf('/') + 1, index);
        } else {
            filename = url.substring(url.lastIndexOf('/') + 1);
        }
        return filename;
    }

    /** 根据路径删除文件 */
    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) return true;
        File file = new File(path);
        if (!file.exists()) return true;
        if (file.isFile()) {
            boolean delete = file.delete();
            OkLogger.e("deleteFile:" + delete + " path:" + path);
            return delete;
        }
        return false;
    }
}