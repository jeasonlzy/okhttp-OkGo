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
package com.lzy.okserver.listener;

import com.lzy.okserver.upload.UploadInfo;

import okhttp3.Response;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
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
