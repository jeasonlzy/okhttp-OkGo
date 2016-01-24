package com.lzy.downloadmanager;

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
public class EndThreadPoolExecutor extends ThreadPoolExecutor {
    private boolean hasFinish = false;

    public EndThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
    }

    public EndThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public EndThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
    }

    public EndThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (taskEndListener != null) taskEndListener.onTaskEnd(r);
    }

    @Override
    public boolean remove(Runnable task) {
        boolean remove = super.remove(task);
        if (removeListener != null) removeListener.onRemove(task);
        return remove;
    }

    private OnRemoveListener removeListener;

    public void setOnRemoveListener(OnRemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    public interface OnRemoveListener {
        void onRemove(Runnable r);
    }

    private OnTaskEndListener taskEndListener;

    public void setOnTaskEndListener(OnTaskEndListener taskEndListener) {
        this.taskEndListener = taskEndListener;
    }

    public interface OnTaskEndListener {
        void onTaskEnd(Runnable r);
    }
}