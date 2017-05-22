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
package com.lzy.okserver.upload;

import android.os.Handler;
import android.os.Message;

import com.lzy.okserver.listener.UploadListener;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：用于在主线程回调更新UI
 * 修订历史：
 * ================================================
 */
public class UploadUIHandler extends Handler {

    private UploadListener mGlobalUploadListener;

    @Override
    public void handleMessage(Message msg) {
        MessageBean messageBean = (MessageBean) msg.obj;
        if (messageBean != null) {
            UploadInfo info = messageBean.uploadInfo;
            String errorMsg = messageBean.errorMsg;
            Exception e = messageBean.e;
            Object t = messageBean.data;
            if (mGlobalUploadListener != null) {
                executeListener(mGlobalUploadListener, info, t, errorMsg, e);
            }
            UploadListener listener = info.getListener();
            if (listener != null) executeListener(listener, info, t, errorMsg, e);
        }
    }

    private void executeListener(UploadListener listener, UploadInfo info, Object t, String errorMsg, Exception e) {
        int state = info.getState();
        switch (state) {
            case UploadManager.NONE:
            case UploadManager.WAITING:
            case UploadManager.UPLOADING:
                listener.onProgress(info);
                break;
            case UploadManager.FINISH:
                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onFinish(t);
                break;
            case UploadManager.ERROR:
                listener.onProgress(info);   //结束前再次回调进度，避免最后一点数据没有刷新
                listener.onError(info, errorMsg, e);
                break;
        }
    }

    public void setGlobalDownloadListener(UploadListener uploadListener) {
        this.mGlobalUploadListener = uploadListener;
    }

    public static class MessageBean<T> {
        public UploadInfo uploadInfo;
        public T data;
        public String errorMsg;
        public Exception e;
    }
}
