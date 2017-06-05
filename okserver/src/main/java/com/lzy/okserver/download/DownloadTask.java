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
package com.lzy.okserver.download;

import android.text.TextUtils;

import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.exception.OkGoException;
import com.lzy.okgo.exception.StorageException;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.Request;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.task.PriorityAsyncTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

import static com.lzy.okgo.utils.OkLogger.tag;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：文件的下载任务类
 * 修订历史：
 * ================================================
 */
public class DownloadTask extends PriorityAsyncTask<Void, Progress, Progress> {

    private static final int BUFFER_SIZE = 1024 * 8;    //读写缓存大小

    private boolean isPause;                            //当前任务是暂停还是停止， true 暂停， false 停止
    public Progress progress;                           //当前任务的信息
    public Request<File, ? extends Request> request;
    public Map<Object, DownloadListener> listenerMap;

    public DownloadTask(String tag, Request<File, ? extends Request> request) {
        listenerMap = new HashMap<>();
        progress = new Progress();
        progress.tag = tag;
        progress.url = request.getBaseUrl();
        progress.status = Progress.NONE;
        this.request = request;
    }

    public DownloadTask(Progress progress, Request<File, ? extends Request> request) {
        listenerMap = new HashMap<>();
        this.progress = progress;
        this.request = request;
    }

    public DownloadTask folder(String folder) {
        progress.folder = folder;
        return this;
    }

    public DownloadTask fileName(String fileName) {
        progress.fileName = fileName;
        return this;
    }

    public DownloadTask extra1(Serializable extra1) {
        progress.extra1 = extra1;
        return this;
    }

    public DownloadTask extra2(Serializable extra2) {
        progress.extra2 = extra2;
        return this;
    }

    public DownloadTask extra3(Serializable extra3) {
        progress.extra3 = extra3;
        return this;
    }

    public DownloadTask register(DownloadListener listener) {
        Progress progress = DownloadManager.getInstance().get(tag);
        if (progress == null) {
            progress = this.progress;
            DownloadManager.getInstance().insert(progress);
        }

        Map<String, DownloadTask> taskMap = OkDownload.getInstance().getTaskMap();
        DownloadTask downloadTask = taskMap.get(progress.tag);
        if (downloadTask == null) {
            downloadTask = this;
            taskMap.put(progress.tag, downloadTask);
        }
        downloadTask.listenerMap.put(listener.tag, listener);
        return this;
    }

    public void unRegister(DownloadListener listener) {
        listenerMap.remove(listener.tag);
    }

    public void unRegister(String tag) {
        listenerMap.remove(tag);
    }

    public void start() {
        executeOnExecutor(OkDownload.getInstance().getThreadPool().getExecutor());
    }

    public void restart() {
        //如果是重新下载，需要删除临时文件
        IOUtils.delFileOrFolder(progress.filePath);

        progress.fraction = 0;
        progress.currentSize = 0;
        progress.totalSize = -1;
        progress.speed = 0;
        progress.status = Progress.NONE;
        start();
    }

    /** 暂停的方法 */
    public void pause() {
        if (progress.status == Progress.WAITING) {
            progress.speed = 0;
            progress.status = Progress.PAUSE;
            postMessage(null, progress);
        } else {
            isPause = true;
        }
        super.cancel(false);
    }

    /** 停止的方法 */
    public void stop() {
        if (progress.status == Progress.PAUSE || progress.status == Progress.ERROR || progress.status == Progress.WAITING) {
            progress.speed = 0;
            progress.status = Progress.NONE;
            postMessage(null, progress);
        } else {
            isPause = false;
        }
        super.cancel(false);
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        for (DownloadListener listener : listenerMap.values()) {
            listener.onAdd(progress);
        }

        progress.speed = 0;
        progress.status = Progress.WAITING;
        postMessage(null, progress);
    }

    /** 如果调用了Cancel，就不会执行该方法，所以任务结束的回调不放在这里面 */
    @Override
    protected void onPostExecute(Progress progress) {
    }

    /** 一旦该方法执行，意味着开始下载了 */
    @Override
    protected Progress doInBackground(Void... params) {
        if (isCancelled()) return progress;
        progress.speed = 0;
        progress.status = Progress.LOADING;
        postMessage(null, progress);

        long startPosition = progress.currentSize;
        Response response;
        try {
            response = request.headers(HttpHeaders.HEAD_KEY_RANGE, "bytes=" + startPosition + "-").execute();
        } catch (IOException e) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = e;
            postMessage(null, progress);
            return progress;
        }
        int code = response.code();
        if (code == 404 || code >= 500) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = HttpException.NET_ERROR();
            postMessage(null, progress);
            return progress;
        }
        //构建下载文件路径，如果有设置，就用设置的，否者就自己创建
        String fileName = progress.fileName;
        if (TextUtils.isEmpty(fileName)) {
            fileName = HttpUtils.getNetFileName(response, progress.url);
            progress.fileName = fileName;
        }
        if (!IOUtils.createFolder(progress.folder)) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = StorageException.NOT_AVAILABLE();
            postMessage(null, progress);
            return progress;
        }
        File file = null;
        if (TextUtils.isEmpty(progress.filePath)) {
            file = new File(progress.folder, fileName);
            progress.filePath = file.getAbsolutePath();
        }
        //检查文件有效性，文件大小大于总文件大小
        if (startPosition > progress.totalSize) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = new OkGoException("Breakpoint file has expired");
            postMessage(null, progress);
            return progress;
        }
        if (startPosition == progress.totalSize && startPosition > 0) {
            progress.fraction = 1.0f;
            progress.speed = 0;
            progress.status = Progress.FINISH;
            postMessage(file, progress);
            return progress;
        }
        //设置断点写文件
        file = new File(progress.filePath);
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.seek(startPosition);
            progress.currentSize = startPosition;
        } catch (Exception e) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = new OkGoException("Breakpoint file does not exist");
            postMessage(null, progress);
            return progress;
        }
        //获取流对象，准备进行读写文件
        ResponseBody body = response.body();
        if (body == null) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = new HttpException("response body is null");
            postMessage(null, progress);
            return progress;
        }
        progress.totalSize = body.contentLength();
        InputStream is = body.byteStream();
        //读写文件流
        try {
            download(is, randomAccessFile, progress);
        } catch (IOException e) {
            progress.speed = 0;
            progress.status = Progress.ERROR;
            progress.exception = new StorageException("File read or write exception");
            postMessage(null, progress);
            return progress;
        }

        if (isCancelled()) {
            progress.speed = 0;
            if (isPause) progress.status = Progress.PAUSE;  //暂停
            else progress.status = Progress.NONE;           //停止
            postMessage(null, progress);
        } else if (file.length() == progress.totalSize && progress.status == Progress.LOADING) {
            progress.speed = 0;
            progress.status = Progress.FINISH;              //下载完成
            postMessage(file, progress);
        } else if (file.length() != progress.currentSize) {
            progress.speed = 0;
            progress.status = Progress.ERROR;               //由于不明原因，文件保存有误
            progress.exception = OkGoException.UNKNOWN();
            postMessage(null, progress);
        }
        return progress;
    }

    /** 执行文件下载 */
    private void download(InputStream input, RandomAccessFile out, Progress progress) throws IOException {
        if (input == null || out == null) return;

        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int len;
        try {
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && !isCancelled()) {
                out.write(buffer, 0, len);

                Progress.changeProgress(progress, len, progress.totalSize, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        postMessage(null, progress);
                    }
                });
            }
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(input);
        }
    }

    private void postMessage(final File file, final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (progress.status) {
                    case Progress.NONE:
                    case Progress.WAITING:
                    case Progress.LOADING:
                    case Progress.PAUSE:
                        for (DownloadListener listener : listenerMap.values()) {
                            listener.onProgress(progress);
                        }
                        break;
                    case Progress.FINISH:
                        for (DownloadListener listener : listenerMap.values()) {
                            listener.onProgress(progress);
                            listener.onFinish(file, progress);
                        }
                        break;
                    case Progress.ERROR:
                        for (DownloadListener listener : listenerMap.values()) {
                            listener.onProgress(progress);
                            listener.onError(progress);
                        }
                        break;
                }
            }
        });
    }
}
