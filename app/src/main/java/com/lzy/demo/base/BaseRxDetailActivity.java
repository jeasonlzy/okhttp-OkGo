package com.lzy.demo.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/10/1
 * 描    述：统一管理所有的订阅生命周期
 * 修订历史：
 * ================================================
 */
public abstract class BaseRxDetailActivity extends BaseDetailActivity {

    private CompositeSubscription compositeSubscription;

    public void addSubscribe(Subscription subscription) {
        if (compositeSubscription == null) {
            compositeSubscription = new CompositeSubscription();
        }
        compositeSubscription.add(subscription);
    }

    public void unSubscribe() {
        if (compositeSubscription != null) compositeSubscription.unsubscribe();
    }
}
