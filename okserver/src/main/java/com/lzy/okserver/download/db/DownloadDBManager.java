package com.lzy.okserver.download.db;

import com.lzy.okserver.download.DownloadInfo;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/8/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public enum DownloadDBManager {

    INSTANCE;

    private Lock mLock;
    private DownloadInfoDao infoDao;

    DownloadDBManager() {
        mLock = new ReentrantLock();
        infoDao = new DownloadInfoDao();
    }

    /** 获取下载任务 */
    public DownloadInfo get(String key) {
        mLock.lock();
        try {
            return infoDao.get(key);
        } finally {
            mLock.unlock();
        }
    }

    /** 获取所有下载信息 */
    public List<DownloadInfo> getAll() {
        mLock.lock();
        try {
            return infoDao.getAll();
        } finally {
            mLock.unlock();
        }
    }

    /** 更新下载任务，没有就创建，有就替换 */
    public DownloadInfo replace(DownloadInfo entity) {
        mLock.lock();
        try {
            infoDao.replace(entity);
            return entity;
        } finally {
            mLock.unlock();
        }
    }

    /** 移除下载任务 */
    public void delete(String key) {
        mLock.lock();
        try {
            infoDao.delete(key);
        } finally {
            mLock.unlock();
        }
    }

    /** 创建下载任务 */
    public void create(DownloadInfo entity) {
        mLock.lock();
        try {
            infoDao.create(entity);
        } finally {
            mLock.unlock();
        }
    }

    /** 更新下载任务 */
    public void update(DownloadInfo entity) {
        mLock.lock();
        try {
            infoDao.update(entity);
        } finally {
            mLock.unlock();
        }
    }

    /** 清空下载任务 */
    public boolean clear() {
        mLock.lock();
        try {
            return infoDao.deleteAll() > 0;
        } finally {
            mLock.unlock();
        }
    }
}