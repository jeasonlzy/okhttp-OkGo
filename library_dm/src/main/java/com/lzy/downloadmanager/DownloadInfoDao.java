package com.lzy.downloadmanager;

import android.content.Context;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

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