package com.lzy.okgo.cache;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.utils.OkLogger;

class CacheHelper extends SQLiteOpenHelper {

    public static final String DB_CACHE_NAME = "okgo_cache.db";
    public static final int DB_CACHE_VERSION = 3;
    public static final String TABLE_NAME = "cache_table";

    //表中的五个字段
    public static final String ID = "_id";
    public static final String KEY = "key";
    public static final String LOCAL_EXPIRE = "localExpire";
    public static final String HEAD = "head";
    public static final String DATA = "data";

    //四条sql语句
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + //
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +//
            KEY + " VARCHAR, " +//
            LOCAL_EXPIRE + " INTEGER, " +//
            HEAD + " BLOB, " +//
            DATA + " BLOB)";
    private static final String SQL_CREATE_UNIQUE_INDEX = "CREATE UNIQUE INDEX cache_unique_index ON " + TABLE_NAME + "(\"key\")";
    private static final String SQL_DELETE_TABLE = "DROP TABLE " + TABLE_NAME;
    private static final String SQL_DELETE_UNIQUE_INDEX = "DROP INDEX cache_unique_index";

    public CacheHelper() {
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
                db.execSQL(SQL_DELETE_UNIQUE_INDEX);
                db.execSQL(SQL_DELETE_TABLE);
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}