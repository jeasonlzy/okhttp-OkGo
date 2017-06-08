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
import com.lzy.okgo.utils.OkLogger;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.OkUpload;
import com.lzy.okserver.task.PriorityRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * 修订历史：
 * ================================================
 */
public class UploadTask<T> implements Runnable {

    public Progress progress;
    public Map<Object, UploadListener<T>> listeners;
    private ThreadPoolExecutor executor;
    private PriorityRunnable priorityRunnable;

    public UploadTask(String tag, Request<T, ? extends Request> request) {
        HttpUtils.checkNotNull(tag, "tag == null");
        progress = new Progress();
        progress.tag = tag;
        progress.folder = OkDownload.getInstance().getFolder();
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        progress.totalSize = -1;
        progress.request = request;

        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public UploadTask(Progress progress) {
        HttpUtils.checkNotNull(progress, "progress == null");
        this.progress = progress;
        executor = OkUpload.getInstance().getThreadPool().getExecutor();
        listeners = new HashMap<>();
    }

    public UploadTask<T> priority(int priority) {
        progress.priority = priority;
        return this;
    }

    public UploadTask<T> register(UploadListener<T> listener) {
        if (listener != null) {
            listeners.put(listener.tag, listener);
        }
        return this;
    }

    public void unRegister(UploadListener<T> listener) {
        HttpUtils.checkNotNull(listener, "listener == null");
        listeners.remove(listener.tag);
    }

    public void unRegister(String tag) {
        HttpUtils.checkNotNull(tag, "tag == null");
        listeners.remove(tag);
    }

    public UploadTask<T> start() {
        if (progress.status == Progress.NONE || progress.status == Progress.PAUSE || progress.status == Progress.ERROR) {
            postOnStart(progress);
            postWaiting(progress);
            priorityRunnable = new PriorityRunnable(progress.priority, this);
            executor.execute(priorityRunnable);
        } else {
            OkLogger.w("the task with tag " + progress.tag + " is already in the upload queue, current task status is " + progress.status);
        }
        return this;
    }

    public void restart() {
        pause();
        progress.status = Progress.NONE;
        progress.currentSize = 0;
        progress.fraction = 0;
        progress.speed = 0;
        start();
    }

    /** 暂停的方法 */
    public void pause() {
        executor.remove(priorityRunnable);
        if (progress.status == Progress.WAITING) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            progress.speed = 0;
            progress.status = Progress.PAUSE;
        } else {
            OkLogger.w("only the task with status WAITING(1) or LOADING(2) can pause, current status is " + progress.status);
        }
    }

    /** 删除一个任务,会删除下载文件 */
    public UploadTask<T> remove() {
        pause();
        listeners.clear();
        //noinspection unchecked
        return (UploadTask<T>) OkUpload.getInstance().removeTask(progress.tag);
    }

    @Override
    public void run() {
        postLoading(progress);
        Response<T> response;
        try {
            //noinspection unchecked
            Request<T, ? extends Request> request = (Request<T, ? extends Request>) progress.request;
            request.uploadInterceptor(new ProgressRequestBody.UploadInterceptor() {
                @Override
                public void uploadProgress(Progress progress) {
                    postLoading(progress);
                }
            });
            response = request.adapt().execute();
        } catch (Exception e) {
            postOnError(progress, e);
            return;
        }

        if (response.isSuccessful()) {
            postOnFinish(progress, response.body());
        } else {
            postOnError(progress, response.getException());
        }
    }

    private void postOnStart(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.speed = 0;
                progress.status = Progress.NONE;
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onStart(progress);
                }
            }
        });
    }

    private void postWaiting(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.speed = 0;
                progress.status = Progress.WAITING;
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postPause(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.speed = 0;
                progress.status = Progress.PAUSE;
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postLoading(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                }
            }
        });
    }

    private void postOnError(final Progress progress, final Throwable throwable) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.speed = 0;
                progress.status = Progress.ERROR;
                progress.exception = throwable;
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onError(progress);
                }
            }
        });
    }

    private void postOnFinish(final Progress progress, final T t) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.speed = 0;
                progress.fraction = 1.0f;
                progress.status = Progress.FINISH;
                for (UploadListener<T> listener : listeners.values()) {
                    listener.onProgress(progress);
                    listener.onFinish(t, progress);
                }
            }
        });
    }
}
