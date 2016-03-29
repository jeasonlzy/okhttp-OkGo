package com.lzy.okhttpserver.download;

import com.lzy.okhttpserver.task.ExecutorWithListener;
import com.lzy.okhttpserver.task.PriorityBlockingQueue;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：下载管理的线程池
 * 修订历史：
 * ================================================
 */
public class DownloadThreadPool {
    private static final int MAX_IMUM_POOL_SIZE = 5;     //最大线程池的数量
    private static final int KEEP_ALIVE_TIME = 1;        //存活的时间
    private static final TimeUnit UNIT = TimeUnit.HOURS; //时间单位
    private int corePoolSize = 3;                        //核心线程池的数量，同时能执行的线程数量，默认3个
    private ExecutorWithListener executor;               //线程池执行器

    public ExecutorWithListener getExecutor() {
        if (executor == null) {
            synchronized (DownloadThreadPool.class) {
                if (executor == null) {
                    executor = new ExecutorWithListener(corePoolSize, MAX_IMUM_POOL_SIZE, KEEP_ALIVE_TIME, UNIT, //
                            new PriorityBlockingQueue<Runnable>(),   //无限容量的缓冲队列
                            Executors.defaultThreadFactory(),        //线程创建工厂
                            new ThreadPoolExecutor.AbortPolicy());   //继续超出上限的策略，阻止
                }
            }
        }
        return executor;
    }

    /** 必须在首次执行前设置，否者无效 ,范围1-5之间 */
    public void setCorePoolSize(int corePoolSize) {
        if (corePoolSize <= 0) corePoolSize = 1;
        if (corePoolSize > MAX_IMUM_POOL_SIZE) corePoolSize = MAX_IMUM_POOL_SIZE;
        this.corePoolSize = corePoolSize;
    }

    /** 执行任务 */
    public void execute(Runnable runnable) {
        if (runnable != null) {
            getExecutor().execute(runnable);
        }
    }

    /** 移除线程 */
    public void remove(Runnable runnable) {
        if (runnable != null) {
            getExecutor().remove(runnable);
        }
    }
}
