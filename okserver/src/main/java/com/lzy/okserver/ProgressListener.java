package com.lzy.okserver;

import com.lzy.okgo.model.Progress;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/4
 * 描    述：
 * 修订历史：
 * ================================================
 */
public interface ProgressListener<T> {
    /** 下载进行时回调 */
    void onProgress(Progress progress);

    /** 下载完成时回调 */
    void onFinish(T t, Progress progress);

    /** 下载出错时回调 */
    void onError(Progress progress);

    /** 成功添加任务的回调 */
    void onAdd(Progress progress);

    /** 成功移除任务回调 */
    void onRemove(Progress progress);
}
