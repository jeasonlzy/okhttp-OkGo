package com.lzy.okhttpserver.download;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
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
public class DownloadInfoDao {

    private Dao<DownloadInfo, Integer> dao;

    @SuppressWarnings("unchecked")
    public DownloadInfoDao(Context context) {
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            this.dao = helper.getDao(DownloadInfo.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<DownloadInfo> queryForAll() {
        try {
            return dao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void create(DownloadInfo downloadInfo) {
        try {
            dao.create(downloadInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String url) {
        try {
            dao.delete(dao.queryForEq("url", url));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(DownloadInfo downloadInfo) {
        try {
            dao.update(downloadInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}