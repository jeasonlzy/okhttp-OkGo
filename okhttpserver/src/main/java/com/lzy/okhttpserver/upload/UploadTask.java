package com.lzy.okhttpserver.upload;

import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.lzy.okhttpserver.listener.UploadListener;
import com.lzy.okhttpserver.task.PriorityAsyncTask;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.AbsCallback;
import com.lzy.okhttputils.request.PostRequest;
import com.lzy.okhttpserver.L;

import java.io.File;
import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/26
 * 描    述：上传任务类
 * 修订历史：
 * ================================================
 */
public class UploadTask<T> extends PriorityAsyncTask<Void, UploadInfo, UploadInfo> {

    private UploadUIHandler mUploadUIHandler;    //下载UI回调
    private UploadInfo mUploadInfo;              //当前任务的信息

    public UploadTask(UploadInfo downloadInfo, Context context, UploadListener<T> uploadListener) {
        mUploadInfo = downloadInfo;
        mUploadInfo.setListener(uploadListener);
        mUploadUIHandler = UploadManager.getInstance(context).getHandler();
        //将当前任务在定义的线程池中执行
        executeOnExecutor(UploadManager.getInstance(context).getThreadPool().getExecutor());
    }

    /** 每个任务进队列的时候，都会执行该方法 */
    @Override
    protected void onPreExecute() {
        L.e("onPreExecute:" + mUploadInfo.getResourcePath());

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
        L.e("doInBackground:" + mUploadInfo.getResourcePath());
        mUploadInfo.setNetworkSpeed(0);
        mUploadInfo.setState(UploadManager.UPLOADING);
        postMessage(null, null, null);

        //构建请求体,默认使用post请求上传
        Response response;
        try {
            PostRequest postRequest = OkHttpUtils.post(mUploadInfo.getUrl());
            File resource = new File(mUploadInfo.getResourcePath());
            if (TextUtils.isEmpty(mUploadInfo.getFileName())) {
                mUploadInfo.setFileName(resource.getName());
            }
            postRequest.params(mUploadInfo.getKey(), resource, mUploadInfo.getFileName());
            //接口对接，数据回调
            postRequest.setCallback(new MergeListener());
            response = postRequest.execute();
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

        public MergeListener() {
            lastRefreshUiTime = System.currentTimeMillis();
        }

        //只有这个方法会被调用，主要是为了对接接口，获取进度
        @Override
        public void upProgress(long currentSize, long totalSize, float progress, long networkSpeed) {
            long curTime = System.currentTimeMillis();
            //每100毫秒刷新一次数据
            if (curTime - lastRefreshUiTime >= 100 || progress == 1.0f) {
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
        public T parseNetworkResponse(Response response) {
            return null;
        }

        @Override
        public void onResponse(boolean isFromCache, T t, Request request, Response response) {
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