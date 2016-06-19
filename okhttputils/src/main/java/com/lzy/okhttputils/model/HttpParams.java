package com.lzy.okhttputils.model;

import java.io.File;
import java.io.Serializable;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2015/10/9
 * 描    述：请求参数的包装类，支持一个key对应多个值
 * 修订历史：
 * ================================================
 */
public class HttpParams implements Serializable {

    /** 普通的键值对参数 */
    public ConcurrentHashMap<String, List<String>> urlParamsMap;

    /** 文件的键值对参数 */
    public ConcurrentHashMap<String, List<FileWrapper>> fileParamsMap;

    private void init() {
        urlParamsMap = new ConcurrentHashMap<>();
        fileParamsMap = new ConcurrentHashMap<>();
    }

    public HttpParams() {
        init();
    }

    public HttpParams(String key, String value) {
        init();
        put(key, value);
    }

    public HttpParams(String key, File file) {
        init();
        put(key, file);
    }

    public void put(HttpParams params) {
        if (params != null) {
            if (params.urlParamsMap != null && !params.urlParamsMap.isEmpty()) urlParamsMap.putAll(params.urlParamsMap);
            if (params.fileParamsMap != null && !params.fileParamsMap.isEmpty()) fileParamsMap.putAll(params.fileParamsMap);
        }
    }

    public void put(String key, String value) {
        if (key != null && value != null) {
            List<String> urlValues = urlParamsMap.get(key);
            if (urlValues == null) {
                urlValues = new ArrayList<>();
                urlParamsMap.put(key, urlValues);
            }
            urlValues.add(value);
        }
    }

    public void putUrlParams(String key, List<String> values) {
        if (key != null && values != null && !values.isEmpty()) {
            for (String value : values) {
                put(key, value);
            }
        }
    }

    public void put(String key, File file) {
        put(key, file, file.getName());
    }

    public void put(String key, File file, String fileName) {
        put(key, file, fileName, guessMimeType(fileName));
    }

    public void put(String key, FileWrapper fileWrapper) {
        if (key != null && fileWrapper != null) {
            put(key, fileWrapper.file, fileWrapper.fileName, fileWrapper.contentType);
        }
    }

    public void put(String key, File file, String fileName, MediaType contentType) {
        if (key != null) {
            List<FileWrapper> fileWrappers = fileParamsMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamsMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper(file, fileName, contentType));
        }
    }

    public void putFileParams(String key, List<File> files) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                put(key, file);
            }
        }
    }

    public void putFileWrapperParams(String key, List<FileWrapper> fileWrappers) {
        if (key != null && fileWrappers != null && !fileWrappers.isEmpty()) {
            for (FileWrapper fileWrapper : fileWrappers) {
                put(key, fileWrapper);
            }
        }
    }

    public void removeUrl(String key) {
        urlParamsMap.remove(key);
    }

    public void removeFile(String key) {
        fileParamsMap.remove(key);
    }

    public void clear() {
        urlParamsMap.clear();
        fileParamsMap.clear();
    }

    private MediaType guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        return MediaType.parse(contentType);
    }

    /**
     * 文件类型的包装类
     */
    public static class FileWrapper {
        public File file;
        public String fileName;
        public MediaType contentType;
        public long fileSize;

        public FileWrapper(File file, String fileName, MediaType contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSize = file.length();
        }

        public String getFileName() {
            if (fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }

        @Override
        public String toString() {
            return "FileWrapper{" + "file=" + file + ", fileName='" + fileName + ", contentType=" + contentType + ", fileSize=" + fileSize + '}';
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, List<String>> entry : urlParamsMap.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        for (ConcurrentHashMap.Entry<String, List<FileWrapper>> entry : fileParamsMap.entrySet()) {
            if (result.length() > 0) result.append("&");
            result.append(entry.getKey()).append("=").append(entry.getValue());
        }
        return result.toString();
    }
}