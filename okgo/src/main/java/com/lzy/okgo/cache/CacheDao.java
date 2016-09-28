package com.lzy.okgo.cache;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

class CacheDao<T> extends DataBaseDao<CacheEntity<T>> {

    public CacheDao() {
        super(new CacheHelper());
    }

    /** 根据key获取缓存 */
    public CacheEntity<T> get(String key) {
        String selection = CacheHelper.KEY + "=?";
        String[] selectionArgs = new String[]{key};
        List<CacheEntity<T>> cacheEntities = get(selection, selectionArgs);
        return cacheEntities.size() > 0 ? cacheEntities.get(0) : null;
    }

    /** 移除一个缓存 */
    public boolean remove(String key) {
        String whereClause = CacheHelper.KEY + "=?";
        String[] whereArgs = new String[]{key};
        int delete = delete(whereClause, whereArgs);
        return delete > 0;
    }

    @Override
    public CacheEntity<T> parseCursorToBean(Cursor cursor) {
        return CacheEntity.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(CacheEntity<T> cacheEntity) {
        return CacheEntity.getContentValues(cacheEntity);
    }

    @Override
    protected String getTableName() {
        return CacheHelper.TABLE_NAME;
    }
}