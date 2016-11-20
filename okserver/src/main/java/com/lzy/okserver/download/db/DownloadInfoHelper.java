package com.lzy.okserver.download.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.utils.OkLogger;
import com.lzy.okserver.download.DownloadInfo;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：下载数据库的帮助类
 * 修订历史：
 * ================================================
 */
public class DownloadInfoHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "okgo_server.db";
    public static final int DB_CACHE_VERSION = 4;
    public static final String TABLE_NAME = "download_table";

    //四条sql语句
    private static final String SQL_CREATE_TABLE = //
            "CREATE TABLE " + TABLE_NAME + "(" + //
            DownloadInfo.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +//
            DownloadInfo.TASK_KEY + " VARCHAR, " +//
            DownloadInfo.URL + " VARCHAR, " +//
            DownloadInfo.TARGET_FOLDER + " VARCHAR, " +//
            DownloadInfo.TARGET_PATH + " VARCHAR, " +//
            DownloadInfo.FILE_NAME + " VARCHAR, " +//
            DownloadInfo.PROGRESS + " REAL, " +//
            DownloadInfo.TOTAL_LENGTH + " INTEGER, " +//
            DownloadInfo.DOWNLOAD_LENGTH + " INTEGER, " +//
            DownloadInfo.NETWORK_SPEED + " INTEGER, " +//
            DownloadInfo.STATE + " INTEGER, " +//
            DownloadInfo.DOWNLOAD_REQUEST + " BLOB, " +//
            DownloadInfo.DATA + " BLOB)";
    private static final String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX cache_unique_index ON " + TABLE_NAME + "(\"" + DownloadInfo.TASK_KEY + "\")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE " + TABLE_NAME;
    private static final String SQL_DELETE_UNIQUE_INDEX = "DROP INDEX cache_unique_index";

    public DownloadInfoHelper() {
        super(OkGo.getContext(), DB_CACHE_NAME, null, DB_CACHE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(SQL_CREATE_TABLE);
            //建立key的唯一索引后，方便使用 replace 语句
            db.execSQL(SQL_CREATE_UNIQUE_INDEX);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            OkLogger.e(e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.beginTransaction();
            try {
                if (newVersion > 3) {
                    db.execSQL(SQL_DELETE_UNIQUE_INDEX);
                    db.execSQL(SQL_DELETE_TABLE);
                }
                db.execSQL(SQL_CREATE_TABLE);
                db.execSQL(SQL_CREATE_UNIQUE_INDEX);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                OkLogger.e(e);
            } finally {
                db.endTransaction();
            }
        }
    }
}