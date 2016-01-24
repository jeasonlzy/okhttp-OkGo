package com.lzy.library_xutilsdm;

/**
 * Author: wyouflf
 * Date: 14-5-16
 * Time: 上午11:25
 */
public class PriorityObject<E> {

    public final Priority priority;
    public final E obj;

    public PriorityObject(Priority priority, E obj) {
        this.priority = priority == null ? Priority.DEFAULT : priority;
        this.obj = obj;
    }
}
