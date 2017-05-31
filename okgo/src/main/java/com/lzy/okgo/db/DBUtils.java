package com.lzy.okgo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzy.okgo.utils.OkLogger;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/5/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class DBUtils {

    /** 是否需要升级表 */
    public static boolean isNeedUpgradeTable(SQLiteDatabase db, TableEntity table) {
        if (!isTableExists(db, table.tableName)) return true;

        Cursor cursor = db.rawQuery("select * from " + table.tableName, null);
        if (cursor == null) return false;
        try {
            int columnCount = table.getColumnCount();
            if (columnCount == cursor.getColumnCount()) {
                for (int i = 0; i < columnCount; i++) {
                    if (table.getColumnIndex(cursor.getColumnName(i)) == -1) {
                        return true;
                    }
                }
            } else {
                return true;
            }
            return false;
        } finally {
            cursor.close();
        }
    }

    /**
     * SQLite数据库中一个特殊的名叫 SQLITE_MASTER 上执行一个SELECT查询以获得所有表的索引。每一个 SQLite 数据库都有一个叫 SQLITE_MASTER 的表， 它定义数据库的模式。
     * SQLITE_MASTER 表看起来如下：
     * CREATE TABLE sqlite_master (
     * type TEXT,
     * name TEXT,
     * tbl_name TEXT,
     * rootpage INTEGER,
     * sql TEXT
     * );
     * 对于表来说，type 字段永远是 ‘table’，name 字段永远是表的名字。
     */
    public static boolean isTableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen()) return false;

        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
            if (!cursor.moveToFirst()) {
                return false;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return count > 0;
    }

    public static boolean isFieldExists(SQLiteDatabase db, String tableName, String fieldName) {
        if (tableName == null || db == null || fieldName == null || !db.isOpen()) return false;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            return cursor != null && cursor.getColumnIndex(fieldName) != -1;
        } catch (Exception e) {
            OkLogger.printStackTrace(e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
