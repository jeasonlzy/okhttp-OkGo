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
package com.lzy.okserver.download.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okgo.cache.BaseDao;
import com.lzy.okserver.download.DownloadInfo;

import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DownloadDBManager extends BaseDao<DownloadInfo> {

    private DownloadDBManager() {
        super(new DownloadInfoHelper());
    }

    public static DownloadDBManager getInstance() {
        return DownloadDBManagerHolder.instance;
    }

    private static class DownloadDBManagerHolder {
        private static final DownloadDBManager instance = new DownloadDBManager();
    }

    @Override
    public DownloadInfo parseCursorToBean(Cursor cursor) {
        return DownloadInfo.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(DownloadInfo downloadInfo) {
        return DownloadInfo.buildContentValues(downloadInfo);
    }

    @Override
    protected String getTableName() {
        return DownloadInfoHelper.TABLE_NAME;
    }

    /** 获取下载任务 */
    public DownloadInfo get(String key) {
        List<DownloadInfo> infos = query(DownloadInfo.TASK_KEY + "=?", new String[]{key});
        return infos.size() > 0 ? infos.get(0) : null;
    }

    /** 移除下载任务 */
    public void delete(String taskKey) {
        delete(DownloadInfo.TASK_KEY + "=?", new String[]{taskKey});
    }

    /** 更新下载任务 */
    public int update(DownloadInfo downloadInfo) {
        return update(downloadInfo, DownloadInfo.TASK_KEY + "=?", new String[]{downloadInfo.getTaskKey()});
    }

    /** 获取所有下载信息 */
    public List<DownloadInfo> getAll() {
        return query(null, null, null, null, null, DownloadInfo.ID + " ASC", null);
    }

    /** 清空下载任务 */
    public boolean clear() {
        return deleteAll() > 0;
    }
}
