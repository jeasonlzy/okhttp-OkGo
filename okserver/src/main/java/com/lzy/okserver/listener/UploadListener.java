package com.lzy.okserver.listener;

import com.lzy.okserver.upload.UploadInfo;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：全局的上传监听
 * 修订历史：
 * ================================================
 */
public abstract class UploadListener<T> {

    private Object userTag;

    /** 下载进行时回调 */
    public abstract void onProgress(UploadInfo uploadInfo);

    /** 下载完成时回调 */
    public abstract void onFinish(T t);

    /** 下载出错时回调 */
    public abstract void onError(UploadInfo uploadInfo, String errorMsg, Exception e);

    /** 成功添加任务的回调 */
    public void onAdd(UploadInfo uploadInfo) {
    }

    /** 成功移除任务回调 */
    public void onRemove(UploadInfo uploadInfo) {
    }

    /** 类似View的Tag功能，主要用在listView更新数据的时候，防止数据错乱 */
    public Object getUserTag() {
        return userTag;
    }

    /** 类似View的Tag功能，主要用在listView更新数据的时候，防止数据错乱 */
    public void setUserTag(Object userTag) {
        this.userTag = userTag;
    }

    public abstract T parseNetworkResponse(Response response) throws Exception;

    /** 默认的空实现 */
    public static final UploadListener DEFAULT_UPLOAD_LISTENER = new UploadListener() {
        @Override
        public void onProgress(UploadInfo downloadInfo) {
        }

        @Override
        public void onFinish(Object downloadInfo) {
        }

        @Override
        public void onError(UploadInfo downloadInfo, String errorMsg, Exception e) {
        }

        @Override
        public Response parseNetworkResponse(Response response) throws Exception {
            return response;
        }
    };
}
