/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.okgo.model;

import com.lzy.okgo.utils.HttpUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.MediaType;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2015/10/9
 * 描    述：请求参数的包装类，支持一个key对应多个值
 * 修订历史：
 * ================================================
 */
public class HttpParams implements Serializable {
    private static final long serialVersionUID = 7369819159227055048L;

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    public static final boolean IS_REPLACE = true;

    public LinkedHashMap<String, String> pathParamsMap;             //url路径参数
    public LinkedHashMap<String, List<String>> stringParamsMap;     //普通的键值对参数
    public LinkedHashMap<String, List<String>> queryParamsMap;      //普通的键值对参数
    public LinkedHashMap<String, List<FileWrapper>> fileParamsMap;  //文件的键值对参数

    public HttpParams() {
        init();
    }

    public HttpParams(String key, String value) {
        init();
        put(key, value, IS_REPLACE);
    }

    public HttpParams(String key, File file) {
        init();
        put(key, file, IS_REPLACE);
    }

    private void init() {
        pathParamsMap = new LinkedHashMap<>();
        stringParamsMap = new LinkedHashMap<>();
        queryParamsMap = new LinkedHashMap<>();
        fileParamsMap = new LinkedHashMap<>();
    }

    public void put(HttpParams params) {
        if (params == null) return;

        if (params.stringParamsMap != null && !params.stringParamsMap.isEmpty()) {
            stringParamsMap.putAll(params.stringParamsMap);
        }
        if (params.fileParamsMap != null && !params.fileParamsMap.isEmpty()) {
            fileParamsMap.putAll(params.fileParamsMap);
        }
    }

    public void put(String key, int value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, long value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, float value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, double value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, char value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, boolean value, boolean... isReplace) {
        put(key, String.valueOf(value), isReplace);
    }

    public void put(String key, String value, boolean... isReplace) {
        if (key == null || value == null) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }

        List<String> stringParams = stringParamsMap.get(key);
        if (stringParams == null) {
            stringParams = new ArrayList<>();
            stringParamsMap.put(key, stringParams);
        }
        if (replace) stringParams.clear();
        stringParams.add(value);
    }

    public void putStringMap(Map<String, String> params, boolean... isReplace) {
        if (params == null || params.isEmpty()) return;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            put(entry.getKey(), entry.getValue(), isReplace);
        }
    }

    public void putStringList(String key, List<String> values, boolean... isReplace) {
        if (key == null || values == null || values.isEmpty()) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }
        if (replace) {
            List<String> params = stringParamsMap.get(key);
            if (params != null) params.clear();
        }

        for (String value : values) {
            put(key, value, false);
        }
    }

    public void put(String key, File file, boolean... isReplace) {
        if (file == null) return;

        put(key, file, file.getName(), isReplace);
    }

    public void put(String key, File file, String fileName, boolean... isReplace) {
        put(key, file, fileName, HttpUtils.guessMimeType(fileName), isReplace);
    }

    public void put(String key, FileWrapper fileWrapper, boolean... isReplace) {
        if (fileWrapper == null) return;

        put(key, fileWrapper.file, fileWrapper.fileName, fileWrapper.contentType, isReplace);
    }

    public void put(String key, File file, String fileName, MediaType contentType, boolean... isReplace) {
        if (key == null || file == null) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }

        List<FileWrapper> fileParams = fileParamsMap.get(key);
        if (fileParams == null) {
            fileParams = new ArrayList<>();
            fileParamsMap.put(key, fileParams);
        }
        if (replace) fileParams.clear();
        fileParams.add(new FileWrapper(file, fileName, contentType));
    }

    public void putFileMap(Map<String, File> params, boolean... isReplace) {
        if (params == null || params.isEmpty()) return;

        for (Map.Entry<String, File> entry : params.entrySet()) {
            put(entry.getKey(), entry.getValue(), isReplace);
        }
    }

    public void putFileList(String key, List<File> files, boolean... isReplace) {
        if (key == null || files == null || files.isEmpty()) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }
        if (replace) {
            List<FileWrapper> params = fileParamsMap.get(key);
            if (params != null) params.clear();
        }

        for (File file : files) {
            put(key, file, false);
        }
    }

    public void putFileWrapperList(String key, List<FileWrapper> fileWrappers, boolean... isReplace) {
        if (key == null || fileWrappers == null || fileWrappers.isEmpty()) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }
        if (replace) {
            List<FileWrapper> params = fileParamsMap.get(key);
            if (params != null) params.clear();
        }

        for (FileWrapper fileWrapper : fileWrappers) {
            put(key, fileWrapper, false);
        }
    }

    public void putPath(String key, String value) {
        if (key == null || value == null) return;

        pathParamsMap.put(key, value);
    }

    public void putQuery(String key, int value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, long value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, float value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, double value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, char value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, boolean value, boolean... isReplace) {
        putQuery(key, String.valueOf(value), isReplace);
    }

    public void putQuery(String key, String value, boolean... isReplace) {
        if (key == null || value == null) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }

        List<String> stringParams = queryParamsMap.get(key);
        if (stringParams == null) {
            stringParams = new ArrayList<>();
            queryParamsMap.put(key, stringParams);
        }
        if (replace) stringParams.clear();
        stringParams.add(value);
    }

    public void putQueryStringMap(Map<String, String> params, boolean... isReplace) {
        if (params == null || params.isEmpty()) return;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            putQuery(entry.getKey(), entry.getValue(), isReplace);
        }
    }

    public void putQueryStringList(String key, List<String> values, boolean... isReplace) {
        if (key == null || values == null || values.isEmpty()) return;

        boolean replace = IS_REPLACE;
        if (isReplace != null && isReplace.length > 0) {
            replace = isReplace[0];
        }
        if (replace) {
            List<String> params = stringParamsMap.get(key);
            if (params != null) params.clear();
        }

        for (String value : values) {
            putQuery(key, value, false);
        }
    }

    public void removeUrl(String key) {
        stringParamsMap.remove(key);
    }

    public void removeFile(String key) {
        fileParamsMap.remove(key);
    }

    public void remove(String key) {
        removeUrl(key);
        removeFile(key);
    }

    public void clear() {
        stringParamsMap.clear();
        fileParamsMap.clear();
    }

    /** 文件类型的包装类 */
    public static class FileWrapper implements Serializable {
        private static final long serialVersionUID = -2356139899636767776L;

        public File file;
        public String fileName;
        public transient MediaType contentType;
        public long fileSize;

        public FileWrapper(File file, String fileName, MediaType contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSize = file.length();
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeObject(contentType.toString());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            contentType = MediaType.parse((String) in.readObject());
        }

        @Override
        public String toString() {
            return "FileWrapper{" + //
                   "file=" + file + //
                   ", fileName=" + fileName + //
                   ", contentType=" + contentType + //
                   ", fileSize=" + fileSize +//
                   "}";
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, List<String>> entry : stringParamsMap.entrySet()) {
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
