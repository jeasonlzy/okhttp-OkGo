package com.lzy.okserver.download.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.lzy.okserver.download.DownloadInfo;
import com.lzy.okgo.cache.DataBaseDao;

import java.util.List;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：下载数据库的操作类
 * 修订历史：
 * ================================================
 */
public class DownloadInfoDao extends DataBaseDao<DownloadInfo> {

    public DownloadInfoDao() {
        super(new DownloadInfoHelper());
    }

    /** 根据key获取缓存 */
    public DownloadInfo get(String key) {
        String selection = DownloadInfo.TASK_KEY + "=?";
        String[] selectionArgs = new String[]{key};
        List<DownloadInfo> infos = get(selection, selectionArgs);
        return infos.size() > 0 ? infos.get(0) : null;
    }

    public void delete(String taskKey) {
        delete(DownloadInfo.TASK_KEY + "=?", new String[]{taskKey});
    }

    public int update(DownloadInfo downloadInfo) {
        return update(downloadInfo, DownloadInfo.TASK_KEY + "=?", new String[]{downloadInfo.getTaskKey()});
    }

    @Override
    public List<DownloadInfo> getAll() {
        return get(null, null, null, null, null, DownloadInfo.ID + " ASC", null);
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
}