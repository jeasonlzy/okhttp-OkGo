package com.lzy.library_xutilsdm;

/**
 * Author: wyouflf
 * Time: 2014/05/23
 */
public interface TaskHandler {

    boolean supportPause();

    boolean supportResume();

    boolean supportCancel();

    void pause();

    void resume();

    void cancel();

    boolean isPaused();

    boolean isCancelled();
}
