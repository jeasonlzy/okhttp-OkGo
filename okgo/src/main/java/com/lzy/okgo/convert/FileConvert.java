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
package com.lzy.okgo.convert;

import android.os.Environment;
import android.text.TextUtils;

import com.lzy.okgo.callback.Callback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.utils.HttpUtils;
import com.lzy.okgo.utils.OkLogger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：字符串的转换器
 * 修订历史：
 * ================================================
 */
public class FileConvert implements Converter<File> {

    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载目标文件夹

    private String destFileDir;                 //目标文件存储的文件夹路径
    private String destFileName;                //目标文件存储的文件名
    private Callback<File> callback;            //下载回调

    public FileConvert() {
        this(null);
    }

    public FileConvert(String destFileName) {
        this(Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER, destFileName);
    }

    public FileConvert(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public void setCallback(Callback<File> callback) {
        this.callback = callback;
    }

    @Override
    public File convertResponse(Response value) throws Throwable {
        if (TextUtils.isEmpty(destFileDir)) destFileDir = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;
        if (TextUtils.isEmpty(destFileName)) destFileName = HttpUtils.getNetFileName(value, value.request().url().toString());

        File dir = new File(destFileDir);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, destFileName);
        if (file.exists()) file.delete();

        InputStream bodyStream = null;
        byte[] buffer = new byte[8096];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = value.body();
            if (body == null) return null;

            bodyStream = body.byteStream();
            Progress progress = new Progress();
            progress.totalSize = body.contentLength();

            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);

                if (callback == null) continue;
                Progress.changeProgress(progress, len, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        onProgress(progress);
                    }
                });
            }
            fileOutputStream.flush();
            return file;
        } finally {
            try {
                if (bodyStream != null) bodyStream.close();
            } catch (IOException e) {
                OkLogger.printStackTrace(e);
            }
            try {
                if (fileOutputStream != null) fileOutputStream.close();
            } catch (IOException e) {
                OkLogger.printStackTrace(e);
            }
        }
    }

    private void onProgress(final Progress progress) {
        HttpUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.downloadProgress(progress);   //进度回调的方法
            }
        });
    }
}
