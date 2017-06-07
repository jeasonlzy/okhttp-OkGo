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
package com.lzy.okserver;

import com.lzy.okgo.request.Request;
import com.lzy.okserver.upload.UploadTask;
import com.lzy.okserver.upload.UploadThreadPool;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：全局的上传管理
 * 修订历史：
 * ================================================
 */
public class OkUpload {

    private Map<String, UploadTask<?>> taskMap;         //所有任务
    private UploadThreadPool threadPool;                //上传的线程池

    public static OkUpload getInstance() {
        return OkUploadHolder.instance;
    }

    private static class OkUploadHolder {
        private static final OkUpload instance = new OkUpload();
    }

    private OkUpload() {
        threadPool = new UploadThreadPool();
        taskMap = new HashMap<>();
    }

    public static <T> UploadTask<T> request(String tag, Request<T, ? extends Request> request) {
        return new UploadTask<>(tag, request);
    }

    public UploadThreadPool getThreadPool() {
        return threadPool;
    }

    public Map<String, UploadTask<?>> getTaskMap() {
        return taskMap;
    }
}
