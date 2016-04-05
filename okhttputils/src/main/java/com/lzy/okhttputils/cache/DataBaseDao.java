package com.lzy.okhttputils.cache;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class DataBaseDao<T> {

    private SQLiteOpenHelper helper;

    public DataBaseDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    protected final SQLiteDatabase openReader() {
        return helper.getReadableDatabase();
    }

    protected final SQLiteDatabase openWriter() {
        return helper.getWritableDatabase();
    }

    protected final void closeDatabase(SQLiteDatabase database, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) cursor.close();
        if (database != null && database.isOpen()) database.close();
    }

    /** 需要数据库中有个 _id 的字段 */
    public final int count() {
        return countColumn("_id");
    }

    /** 返回一列的总记录数量 */
    public final int countColumn(String columnName) {
        String sql = "SELECT COUNT(?) FROM " + getTableName();
        SQLiteDatabase database = openReader();
        Cursor cursor = database.rawQuery(sql, new String[]{columnName});
        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        closeDatabase(database, cursor);
        return count;
    }

    /** 删除所有数据 */
    public final int deleteAll() {
        return delete(null, null);
    }

    /** 根据条件删除数据库中的数据 */
    public final int delete(String whereClause, String[] whereArgs) {
        SQLiteDatabase database = openWriter();
        int result = database.delete(getTableName(), whereClause, whereArgs);
        closeDatabase(database, null);
        return result;
    }

    /** 查询并返回所有对象的集合 */
    public final List<T> getAll() {
        return get(null, null);
    }

    /** 按条件查询对象并返回集合 */
    public final List<T> get(String selection, String[] selectionArgs) {
        return get(null, selection, selectionArgs, null, null, null, null);
    }

    /** 按条件查询对象并返回集合 */
    public final List<T> get(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        SQLiteDatabase database = openReader();
        List<T> list = new ArrayList<>();
        Cursor cursor = database.query(getTableName(), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
        while (!cursor.isClosed() && cursor.moveToNext()) {
            list.add(parseCursorToBean(cursor));
        }
        closeDatabase(database, cursor);
        return list;
    }

    /** 将Cursor解析成对应的JavaBean */
    public abstract T parseCursorToBean(Cursor cursor);

    /** 修改数据的方法 */
    public abstract long replace(T t);

    /** 获取对应的表名 */
    protected abstract String getTableName();
}