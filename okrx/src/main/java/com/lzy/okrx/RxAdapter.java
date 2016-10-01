package com.lzy.okrx;

import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.adapter.CallAdapter;
import com.lzy.okgo.model.Response;

import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.Exceptions;
import rx.schedulers.Schedulers;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/28
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class RxAdapter<T> implements CallAdapter<Observable<T>> {

    public static <T> RxAdapter<T> create() {
        return RxAdapter.ConvertHolder.convert;
    }

    private static class ConvertHolder {

        private static RxAdapter convert = new RxAdapter();
    }

    @Override
    public <R> Observable<T> adapt(Call<R> call) {
        return Observable.create(new CallOnSubscribe<>((Call<T>) call)) //强转,本质，T 与 R 是同一个泛型
                .subscribeOn(Schedulers.io())   //IO线程订阅网络请求
//                .map(new Func1<Response<T>, T>() {
//                    @Override
//                    public T call(Response<T> tResponse) {
//                        return tResponse.body();
//                    }
//                });
                //感觉用上面的map操作也可以完成,但是Retrofit是这么实现的,目前并不清楚具体好处在哪
                .lift(OperatorMapResponseToBodyOrError.<T>instance());
    }

    private static final class CallOnSubscribe<T> implements Observable.OnSubscribe<Response<T>> {
        private final Call<T> originalCall;

        CallOnSubscribe(Call<T> originalCall) {
            this.originalCall = originalCall;
        }

        @Override
        public void call(final Subscriber<? super Response<T>> subscriber) {
            // Since Call is a one-shot type, clone it for each new subscriber.
            Call<T> call = originalCall.clone();

            // Wrap the call in a helper which handles both unsubscription and backpressure.
            RequestArbiter<T> requestArbiter = new RequestArbiter<>(call, subscriber);
            subscriber.add(requestArbiter);
            subscriber.setProducer(requestArbiter);
        }
    }

    private static final class RequestArbiter<T> extends AtomicBoolean implements Subscription, Producer {
        private final Call<T> call;
        private final Subscriber<? super Response<T>> subscriber;

        RequestArbiter(Call<T> call, Subscriber<? super Response<T>> subscriber) {
            this.call = call;
            this.subscriber = subscriber;
        }

        /** 生产事件,将同步请求转化为Rx的事件 */
        @Override
        public void request(long n) {
            if (n < 0) throw new IllegalArgumentException("n < 0: " + n);
            if (n == 0) return; // Nothing to do when requesting 0.
            if (!compareAndSet(false, true)) return; // Request was already triggered.

            try {
                Response<T> response = call.execute();
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(response);
                }
            } catch (Throwable t) {
                Exceptions.throwIfFatal(t);
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(t);
                }
                return;
            }

            if (!subscriber.isUnsubscribed()) {
                subscriber.onCompleted();
            }
        }

        @Override
        public void unsubscribe() {
            call.cancel();
        }

        @Override
        public boolean isUnsubscribed() {
            return call.isCanceled();
        }
    }
}