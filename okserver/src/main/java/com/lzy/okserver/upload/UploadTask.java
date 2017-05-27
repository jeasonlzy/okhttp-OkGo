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

import android.os.Message;

import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.HttpBodyRequest;
import com.lzy.okserver.listener.UploadListener;
import com.lzy.okserver.task.PriorityAsyncTask;

import java.io.IOException;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * 修订历史：
 * ================================================
 */
public class UploadTask<T> extends PriorityAsyncTask<Void, UploadInfo, UploadInfo> {

    private UploadUIHandler mUploadUIHandler;    //下载UI回调
    private UploadInfo mUploadInfo;              //当前任务的信息

    public UploadTask(UploadInfo downloadInfo, UploadListener<T> uploadListener) {
        mUploadInfo = downloadInfo;
        mUploadInfo.setListener(uploadListener);
        mUploadUIHandler = UploadManager.getInstance().getHandler();
        //将当前任务在定义的线程池中执行
        executeOnExecutor(UploadManager.getInstance().getThreadPool().getExecutor());
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        //添加成功的回调
        UploadListener listener = mUploadInfo.getListener();
        if (listener != null) listener.onAdd(mUploadInfo);

        mUploadInfo.setNetworkSpeed(0);
        mUploadInfo.setState(UploadManager.WAITING);
        postMessage(null, null, null);
    }

    /** 一旦该方法执行，意味着开始下载了 */
    @Override
    protected UploadInfo doInBackground(Void... params) {
        if (isCancelled()) return mUploadInfo;
        mUploadInfo.setNetworkSpeed(0);
        mUploadInfo.setState(UploadManager.UPLOADING);
        postMessage(null, null, null);

        //构建请求体,默认使用post请求上传
        okhttp3.Response response;
        try {
            HttpBodyRequest request = mUploadInfo.getRequest();
            request.setCallback(new MergeListener());
            response = request.execute();
        } catch (IOException e) {
            e.printStackTrace();
            mUploadInfo.setNetworkSpeed(0);
            mUploadInfo.setState(UploadManager.ERROR);
            postMessage(null, "网络异常", e);
            return mUploadInfo;
        }

        if (response.isSuccessful()) {
            //解析过程中抛出异常，一般为 json 格式错误，或者数据解析异常
            try {
                T t = (T) mUploadInfo.getListener().parseNetworkResponse(response);
                mUploadInfo.setNetworkSpeed(0);
                mUploadInfo.setState(UploadManager.FINISH); //上传成功
                postMessage(t, null, null);
                return mUploadInfo;
            } catch (Exception e) {
                e.printStackTrace();
                mUploadInfo.setNetworkSpeed(0);
                mUploadInfo.setState(UploadManager.ERROR);
                postMessage(null, "解析数据对象出错", e);
                return mUploadInfo;
            }
        } else {
            mUploadInfo.setNetworkSpeed(0);
            mUploadInfo.setState(UploadManager.ERROR);
            postMessage(null, "数据返回失败", null);
            return mUploadInfo;
        }
    }

    private class MergeListener extends AbsCallback<T> {

        private long lastRefreshUiTime;

        MergeListener() {
            lastRefreshUiTime = System.currentTimeMillis();
        }

        //只有这个方法会被调用，主要是为了对接接口，获取进度
        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            long curTime = System.currentTimeMillis();
            //每200毫秒刷新一次数据
            if (curTime - lastRefreshUiTime >= 200 || progress == 1.0f) {
                mUploadInfo.setState(UploadManager.UPLOADING);
                mUploadInfo.setUploadLength(currentSize);
                mUploadInfo.setTotalLength(totalSize);
                mUploadInfo.setProgress(progress);
                mUploadInfo.setNetworkSpeed(networkSpeed);
                postMessage(null, null, null);
                lastRefreshUiTime = System.currentTimeMillis();
            }
        }

        @Override
        public void onSuccess(T t, Response<T> response) {
        }

        @Override
        public T convertResponse(okhttp3.Response response) throws Exception {
            return null;
        }
    }

    private void postMessage(T data, String errorMsg, Exception e) {
        UploadUIHandler.MessageBean<T> messageBean = new UploadUIHandler.MessageBean<>();
        messageBean.uploadInfo = mUploadInfo;
        messageBean.errorMsg = errorMsg;
        messageBean.e = e;
        messageBean.data = data;
        Message msg = mUploadUIHandler.obtainMessage();
        msg.obj = messageBean;
        mUploadUIHandler.sendMessage(msg);
    }
}
