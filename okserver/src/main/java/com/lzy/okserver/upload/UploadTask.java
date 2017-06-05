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
package com.lzy.okserver.upload;

import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.ProgressRequestBody;
import com.lzy.okgo.request.Request;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.PriorityAsyncTask;

import java.util.HashMap;
import java.util.Map;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * 修订历史：
 * ================================================
 */
public class UploadTask<T> extends PriorityAsyncTask<Void, Progress, Progress> {

    public Progress progress;                           //当前任务的信息
    public Request<T, ? extends Request> request;
    public Map<Object, UploadListener<T>> listenerMap;

    public UploadTask(String tag, Request<T, ? extends Request> request) {
        listenerMap = new HashMap<>();
        progress = new Progress();
        progress.tag = tag;
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        this.request = request;
    }

    public UploadTask(Progress progress, Request<T, ? extends Request> request) {
        listenerMap = new HashMap<>();
        this.progress = progress;
        this.request = request;
    }

    public UploadTask<T> register(UploadListener<T> listener) {
        Map<String, UploadTask<?>> taskMap = OkUpload.getInstance().getTaskMap();
        //noinspection unchecked
        UploadTask<T> uploadTask = (UploadTask<T>) taskMap.get(progress.tag);
        if (uploadTask == null) {
            uploadTask = new UploadTask<>(progress, request);
            taskMap.put(progress.tag, uploadTask);
        }
        uploadTask.listenerMap.put(listener.tag, listener);
        return this;
    }

    public void start() {
        executeOnExecutor(OkUpload.getInstance().getThreadPool().getExecutor());
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        for (UploadListener<T> listener : listenerMap.values()) {
            listener.onAdd(progress);
        }

        progress.speed = 0;
        progress.status = Progress.WAITING;
        postMessage(null, progress);
    }

    /** 一旦该方法执行，意味着开始下载了 */
    @Override
    protected Progress doInBackground(Void... params) {
        if (isCancelled()) return progress;
        progress.speed = 0;
        progress.status = Progress.LOADING;
        postMessage(null, progress);

        Response<T> response;
        try {
            request.uploadInterceptor(new ProgressRequestBody.UploadInterceptor() {
                @Override
                public void uploadProgress(Progress progress) {
                    postMessage(null, progress);
                }
            });
            response = request.adapt().execute();
        } catch (Exception e) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = e;
            postMessage(null, progress);
            return progress;
        }

        if (response.isSuccessful()) {
            try {
                progress.speed = 0;
                progress.status = Progress.FINISH;
                postMessage(response.body(), progress);
                return progress;
            } catch (Throwable e) {
                progress.speed = 0;
                progress.status = Progress.ERROR;
                progress.exception = e;
                postMessage(null, progress);
                return progress;
            }
        } else {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = response.getException();
            postMessage(null, progress);
            return progress;
        }
    }

    private void postMessage(final T data, final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (progress.status) {
                    case Progress.NONE:
                    case Progress.WAITING:
                    case Progress.LOADING:
                        for (UploadListener<T> listener : listenerMap.values()) {
                            listener.onProgress(progress);
                        }
                        break;
                    case Progress.FINISH:
                        for (UploadListener<T> listener : listenerMap.values()) {
                            listener.onProgress(progress);
                            listener.onFinish(data, progress);
                        }
                        break;
                    case Progress.ERROR:
                        for (UploadListener<T> listener : listenerMap.values()) {
                            listener.onProgress(progress);
                            listener.onError(progress);
                        }
                        break;
                }
            }
        });
    }
}
