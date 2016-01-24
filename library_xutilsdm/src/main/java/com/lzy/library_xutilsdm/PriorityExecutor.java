package com.lzy.library_xutilsdm;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：下载管理的线程池
 * 修订历史：
 * ================================================
 */
public class PriorityExecutor implements Executor {

    private static final int CORE_POOL_SIZE = 3;            //核心线程池数量（同时运行的线程数）
    private static final int MAXIMUM_POOL_SIZE = 256;       //最大线程池数量
    private static final int KEEP_ALIVE = 1;                //线程等待时间
    private static final TimeUnit UNIT = TimeUnit.SECONDS;  //时间单位

    /**
     * 自定义的线程工程，每次需要创建线程的时候，会调用此方法
     * 可使用 Executors.defaultThreadFactory() 代替
     */
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "PriorityExecutor #" + mCount.getAndIncrement());
        }
    };

    private final ThreadPoolExecutor mThreadPoolExecutor;

    public PriorityExecutor() {
        this(CORE_POOL_SIZE);
    }

    public PriorityExecutor(int poolSize) {
        mThreadPoolExecutor = new ThreadPoolExecutor(poolSize, MAXIMUM_POOL_SIZE, KEEP_ALIVE, UNIT, //
                new PriorityObjectBlockingQueue<Runnable>(),   //使用的线程队列
                sThreadFactory,                                //线程创建工厂
                new ThreadPoolExecutor.AbortPolicy());         //继续超出上限的策略，阻止
    }

    public int getPoolSize() {
        return mThreadPoolExecutor.getCorePoolSize();
    }

    public void setPoolSize(int poolSize) {
        if (poolSize > 0) {
            mThreadPoolExecutor.setCorePoolSize(poolSize);
        }
    }

    public boolean isBusy() {
        return mThreadPoolExecutor.getActiveCount() >= mThreadPoolExecutor.getCorePoolSize();
    }

    @Override
    public void execute(final Runnable r) {
        mThreadPoolExecutor.execute(r);
    }
}
