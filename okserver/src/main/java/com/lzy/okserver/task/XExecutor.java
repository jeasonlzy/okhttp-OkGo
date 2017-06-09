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
package com.lzy.okserver.task;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/22
 * 描    述：用于监听任务结束的回调
 * 修订历史：
 * ================================================
 */
public class XExecutor extends ThreadPoolExecutor {

    private Handler innerHandler = new Handler(Looper.getMainLooper());

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public XExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /** 任务结束后回调 */
    @Override
    protected void afterExecute(final Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (taskEndListenerList != null && taskEndListenerList.size() > 0) {
            for (final OnTaskEndListener listener : taskEndListenerList) {
                innerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.onTaskEnd(r);
                    }
                });
            }
        }
        //当前正在运行的数量为1 表示当前正在停止的任务，同时队列中没有任务，表示所有任务下载完毕
        if (getActiveCount() == 1 && getQueue().size() == 0) {
            if (allTaskEndListenerList != null && allTaskEndListenerList.size() > 0) {
                for (final OnAllTaskEndListener listener : allTaskEndListenerList) {
                    innerHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onAllTaskEnd();
                        }
                    });
                }
            }
        }
    }

    private List<OnTaskEndListener> taskEndListenerList;

    public void addOnTaskEndListener(OnTaskEndListener taskEndListener) {
        if (taskEndListenerList == null) taskEndListenerList = new ArrayList<>();
        taskEndListenerList.add(taskEndListener);
    }

    public void removeOnTaskEndListener(OnTaskEndListener taskEndListener) {
        taskEndListenerList.remove(taskEndListener);
    }

    public interface OnTaskEndListener {
        void onTaskEnd(Runnable r);
    }

    private List<OnAllTaskEndListener> allTaskEndListenerList;

    public void addOnAllTaskEndListener(OnAllTaskEndListener allTaskEndListener) {
        if (allTaskEndListenerList == null) allTaskEndListenerList = new ArrayList<>();
        allTaskEndListenerList.add(allTaskEndListener);
    }

    public void removeOnAllTaskEndListener(OnAllTaskEndListener allTaskEndListener) {
        allTaskEndListenerList.remove(allTaskEndListener);
    }

    public interface OnAllTaskEndListener {
        void onAllTaskEnd();
    }
}
