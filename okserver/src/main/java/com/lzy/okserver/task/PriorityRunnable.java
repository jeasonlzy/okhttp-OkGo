package com.lzy.okserver.task;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：Runnable对象的优先级封装
 * 修订历史：
 * ================================================
 */
public class PriorityRunnable extends PriorityObject<Runnable> implements Runnable {

    public PriorityRunnable(Priority priority, Runnable obj) {
        super(priority, obj);
    }

    @Override
    public void run() {
        this.obj.run();
    }
}
