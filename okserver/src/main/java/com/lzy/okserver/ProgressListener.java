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
    /** 成功添加任务的回调 */
    void onStart(Progress progress);

    /** 下载进行时回调 */
    void onProgress(Progress progress);

    /** 下载出错时回调 */
    void onError(Progress progress);

    /** 下载完成时回调 */
    void onFinish(T t, Progress progress);

    /** 被移除时回调 */
    void onRemove(Progress progress);
}
