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

import android.os.Environment;

import com.lzy.okgo.db.DownloadManager;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.Request;
import com.lzy.okgo.utils.IOUtils;
import com.lzy.okgo.utils.OkLogger;
import com.lzy.okserver.download.DownloadListener;
import com.lzy.okserver.download.DownloadTask;
import com.lzy.okserver.download.DownloadThreadPool;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：全局的下载管理类
 * 修订历史：
 * ================================================
 */
public class OkDownload {

    private String folder;                      //下载的默认文件夹
    private DownloadThreadPool threadPool;      //下载的线程池
    private Map<String, DownloadTask> taskMap;  //所有任务

    public static OkDownload getInstance() {
        return OkDownloadHolder.instance;
    }

    private static class OkDownloadHolder {
        private static final OkDownload instance = new OkDownload();
    }

    private OkDownload() {
        folder = Environment.getExternalStorageDirectory() + File.separator + "download" + File.separator;
        IOUtils.createFolder(folder);
        threadPool = new DownloadThreadPool();
        taskMap = new HashMap<>();

        //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误
        List<Progress> taskList = DownloadManager.getInstance().getDownloading();
        for (Progress info : taskList) {
            if (info.status == Progress.WAITING || info.status == Progress.LOADING || info.status == Progress.PAUSE) {
                info.status = Progress.NONE;
            }
        }
        DownloadManager.getInstance().replace(taskList);
    }

    public static DownloadTask request(String tag, Request<File, ? extends Request> request) {
        return new DownloadTask(tag, request);
    }

    public static DownloadTask request(Progress progress, Request<File, ? extends Request> request) {
        return new DownloadTask(progress, request);
    }

    public Map<String, DownloadTask> getTaskMap() {
        return taskMap;
    }

    public DownloadTask getTask(String tag) {
        return taskMap.get(tag);
    }

    /** 开始所有任务 */
    public void startAll() {
        for (DownloadTask task : taskMap.values()) {
            task.start();
        }
    }

    /** 暂停下载 */
    public void pause(String tag) {
        DownloadTask task = taskMap.get(tag);
        if (task == null) {
            OkLogger.w("can't find task with tag = " + tag);
            return;
        }
        Progress progress = task.progress;
        if (progress.status == Progress.LOADING || progress.status == Progress.WAITING) {
            task.pause();
        } else {
            OkLogger.w("only the task with status LOADING(2) or WAITING(1) can pause, current status is " + progress.status);
        }
    }

    /** 暂停全部任务 */
    public void pauseAll() {
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            task.pause();
        }
    }

    /** 停止 */
    public void stop(String tag) {
        DownloadTask task = taskMap.get(tag);
        if (task == null) {
            OkLogger.w("can't find task with tag = " + tag);
            return;
        }
        Progress progress = task.progress;
        if (progress.status != Progress.NONE && progress.status != Progress.FINISH) {
            task.pause();
        } else {
            OkLogger.w("only the task with status isn't NONE(0) and FINISH(4) can stop, current status is " + progress.status);
        }
    }

    /** 停止全部任务,先停止没有下载的，再停止下载中的 */
    public void stopAll() {
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            task.stop();
        }
    }

    /** 删除一个任务,会删除下载文件 */
    public void remove(String tag) {
        remove(tag, false);
    }

    /** 删除一个任务,会删除下载文件 */
    public void remove(String tag, boolean isDeleteFile) {
        DownloadTask task = taskMap.get(tag);
        if (task == null) {
            OkLogger.w("can't find task with tag = " + tag);
            return;
        }

        task.pause();
        for (DownloadListener listener : task.listenerMap.values()) {
            listener.onRemove(task.progress);
        }
        task.listenerMap.remove(tag);
        if (isDeleteFile) IOUtils.delFileOrFolder(task.progress.filePath);
        DownloadManager.getInstance().delete(tag);
    }

    /** 删除所有任务 */
    public void removeAll() {
        for (Map.Entry<String, DownloadTask> entry : taskMap.entrySet()) {
            DownloadTask task = entry.getValue();
            if (task == null) {
                OkLogger.w("can't find task with tag = " + entry.getKey());
                continue;
            }
            for (DownloadListener listener : task.listenerMap.values()) {
                listener.onRemove(task.progress);
            }
            task.listenerMap.clear();
        }
        taskMap.clear();
    }

    /** 重新下载 */
    public void restart(String tag) {
        DownloadTask task = taskMap.get(tag);
        if (task == null) {
            OkLogger.w("can't find task with tag = " + tag);
            return;
        }
        task.restart();
    }

    /** 设置下载目录 */
    public String getFolder() {
        return folder;
    }

    public OkDownload setFolder(String folder) {
        this.folder = folder;
        return this;
    }

    public DownloadThreadPool getThreadPool() {
        return threadPool;
    }
}
