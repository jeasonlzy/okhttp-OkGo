package com.lzy.okrx;

import com.lzy.okgo.model.Response;

import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * A version of {@link Observable#map(Func1)} which lets us trigger {@code onError} without having
 * to use {@link Observable#flatMap(Func1)} which breaks producer requests from propagating.
 */
final class OperatorMapResponseToBodyOrError<T> implements Operator<T, Response<T>> {

    private static final OperatorMapResponseToBodyOrError<Object> INSTANCE = new OperatorMapResponseToBodyOrError<>();

    @SuppressWarnings("unchecked") // Safe because of erasure.
    static <R> OperatorMapResponseToBodyOrError<R> instance() {
        return (OperatorMapResponseToBodyOrError<R>) INSTANCE;
    }

    @Override
    public Subscriber<? super Response<T>> call(final Subscriber<? super T> child) {
        return new Subscriber<Response<T>>(child) {
            @Override
            public void onNext(Response<T> response) {
                if (response.isSuccessful()) {
                    child.onNext(response.body());
                } else {
                    child.onError(new HttpException(response));
                }
            }

            @Override
            public void onCompleted() {
                child.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                child.onError(e);
            }
        };
    }
}
