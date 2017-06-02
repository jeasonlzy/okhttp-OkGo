package com.lzy.okgo.model;

import android.os.SystemClock;

import com.lzy.okgo.OkGo;

import java.util.ArrayList;
import java.util.List;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：2017/6/1
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class Progress {
    public long totalSize;              //总字节长度
    public float networkSpeed;          //网速
    public float fraction;              //下载的进度，0-1
    public long currentSize;            //本次下载的大小
    public long lastDownloadLength;     //上次已下载的大小
    private long speedSize;             //每一小段时间间隔的网络流量
    private long lastRefreshTime;       //最后一次刷新的时间

    private List<Float> speedBuffer;

    public Progress() {
        lastRefreshTime = SystemClock.elapsedRealtime();
        speedBuffer = new ArrayList<>();
    }

    public static Progress changeProgress(Progress progress, long writeSize, Action action) {
        return changeProgress(progress, writeSize, progress.totalSize, action);
    }

    public static Progress changeProgress(final Progress progress, long writeSize, long totalSize, final Action action) {
        progress.totalSize = totalSize;
        progress.currentSize += writeSize;
        progress.speedSize += writeSize;

        long currentTime = SystemClock.elapsedRealtime();
        boolean isNotify = (currentTime - progress.lastRefreshTime) >= OkGo.REFRESH_TIME;
        if (isNotify || progress.currentSize == totalSize) {
            long diffTime = currentTime - progress.lastRefreshTime;
            progress.networkSpeed = progress.bufferSpeed(progress.speedSize * 1000.0f / diffTime);
            progress.fraction = (progress.lastDownloadLength + progress.currentSize) * 1.0f / totalSize;
            progress.lastRefreshTime = currentTime;
            progress.speedSize = 0;
            if (action != null) {
                action.call(progress);
            }
        }
        return progress;
    }

    /** 平滑网速，避免抖动过大 */
    private float bufferSpeed(float speed) {
        speedBuffer.add(speed);
        if (speedBuffer.size() > 10) {
            speedBuffer.remove(0);
        }
        float sum = 0;
        for (float speedTemp : speedBuffer) {
            sum += speedTemp;
        }
        return sum / speedBuffer.size();
    }

    public interface Action {
        void call(Progress progress);
    }
}
