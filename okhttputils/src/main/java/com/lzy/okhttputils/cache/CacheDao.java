package com.lzy.okhttputils.cache;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzy.okhttputils.model.HttpHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

class CacheDao<T> extends DataBaseDao<CacheEntity<T>> {

    public CacheDao() {
        super(new CacheHelper());
    }

    /**
     * replace 语句有如下行为特点
     * 1. replace语句会删除原有的一条记录， 并且插入一条新的记录来替换原记录。
     * 2. 一般用replace语句替换一条记录的所有列， 如果在replace语句中没有指定某列， 在replace之后这列的值被置空 。
     * 3. replace语句根据主键的值确定被替换的是哪一条记录
     * 4. 如果执行replace语句时， 不存在要替换的记录， 那么就会插入一条新的记录。
     * 5. replace语句不能根据where子句来定位要被替换的记录
     * 6. 如果新插入的或替换的记录中， 有字段和表中的其他记录冲突， 那么会删除那条其他记录。
     */
    @Override
    public long replace(CacheEntity<T> cacheEntity) {
        SQLiteDatabase database = openWriter();
        ContentValues values = new ContentValues();
        values.put(CacheHelper.KEY, cacheEntity.getKey());

        HttpHeaders headers = cacheEntity.getResponseHeaders();
        ByteArrayOutputStream headerBAOS = null;
        ObjectOutputStream headerOOS = null;
        try {
            headerBAOS = new ByteArrayOutputStream();
            headerOOS = new ObjectOutputStream(headerBAOS);
            headerOOS.writeObject(headers);
            headerOOS.flush();
            byte[] headerData = headerBAOS.toByteArray();
            values.put(CacheHelper.HEAD, headerData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (headerOOS != null) headerOOS.close();
                if (headerBAOS != null) headerBAOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        T data = cacheEntity.getData();
        ByteArrayOutputStream dataBAOS = null;
        ObjectOutputStream dataOOS = null;
        try {
            dataBAOS = new ByteArrayOutputStream();
            dataOOS = new ObjectOutputStream(dataBAOS);
            dataOOS.writeObject(data);
            dataOOS.flush();
            byte[] dataData = dataBAOS.toByteArray();
            values.put(CacheHelper.DATA, dataData);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOOS != null) dataOOS.close();
                if (dataBAOS != null) dataBAOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        long id = database.replace(getTableName(), null, values);
        closeDatabase(database, null);
        return id;
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
    @SuppressWarnings("unchecked")
    public CacheEntity<T> parseCursorToBean(Cursor cursor) {
        CacheEntity<T> cacheEntity = new CacheEntity<>();
        cacheEntity.setId(cursor.getInt(cursor.getColumnIndex(CacheHelper.ID)));
        cacheEntity.setKey(cursor.getString(cursor.getColumnIndex(CacheHelper.KEY)));

        byte[] headerData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.HEAD));
        ByteArrayInputStream headerBAIS = null;
        ObjectInputStream headerOIS = null;
        try {
            headerBAIS = new ByteArrayInputStream(headerData);
            headerOIS = new ObjectInputStream(headerBAIS);
            Object header = headerOIS.readObject();
            cacheEntity.setResponseHeaders((HttpHeaders) header);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (headerOIS != null) headerOIS.close();
                if (headerBAIS != null) headerBAIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        byte[] dataData = cursor.getBlob(cursor.getColumnIndex(CacheHelper.DATA));
        ByteArrayInputStream dataBAIS = null;
        ObjectInputStream dataOIS = null;
        try {
            dataBAIS = new ByteArrayInputStream(dataData);
            dataOIS = new ObjectInputStream(dataBAIS);
            T data = (T) dataOIS.readObject();
            cacheEntity.setData(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOIS != null) dataOIS.close();
                if (dataBAIS != null) dataBAIS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return cacheEntity;
    }

    @Override
    protected String getTableName() {
        return CacheHelper.TABLE_NAME;
    }
}
