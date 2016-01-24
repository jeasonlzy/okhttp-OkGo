package com.lzy.downloadmanager.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/22
 * 描    述：用于监听任务结束的回调
 * 修订历史：
 * ================================================
 */
public class ExecutorWithListener extends ThreadPoolExecutor {

    public ExecutorWithListener(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public ExecutorWithListener(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public ExecutorWithListener(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public ExecutorWithListener(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /** 任务结束后回调 */
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (listenerList != null && listenerList.size() > 0) {
            for (OnTaskEndListener listener : listenerList) {
                listener.onTaskEnd(r);
            }
        }
    }

    private List<OnTaskEndListener> listenerList;

    public void addOnTaskEndListener(OnTaskEndListener taskEndListener) {
        if (listenerList == null) listenerList = new ArrayList<>();
        listenerList.add(taskEndListener);
    }

    public void removeOnTaskEndListener(OnTaskEndListener taskEndListener) {
        listenerList.remove(taskEndListener);
    }

    public interface OnTaskEndListener {
        void onTaskEnd(Runnable r);
    }
}