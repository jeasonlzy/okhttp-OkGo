/*
 * Copyright 2016 jeasonlzy(廖子尧)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lzy.okrx.subscribe;

import com.lzy.okgo.adapter.Call;
import com.lzy.okgo.model.Response;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Producer;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.CompositeException;
import rx.exceptions.Exceptions;
import rx.exceptions.OnCompletedFailedException;
import rx.exceptions.OnErrorFailedException;
import rx.exceptions.OnErrorNotImplementedException;
import rx.plugins.RxJavaHooks;

/**
 * ================================================
 * 作    者：jeasonlzy（廖子尧）Github地址：https://github.com/jeasonlzy
 * 版    本：1.0
 * 创建日期：16/9/11
 * 描    述：
 * 修订历史：
 * ================================================
 */
final class CallArbiter<T> extends AtomicInteger implements Subscription, Producer {
    private static final long serialVersionUID = 613435323949233509L;

    private static final int STATE_WAITING = 0;
    private static final int STATE_REQUESTED = 1;
    private static final int STATE_HAS_RESPONSE = 2;
    private static final int STATE_TERMINATED = 3;

    private final Call<T> call;
    private final Subscriber<? super Response<T>> subscriber;

    private volatile LinkedList<Response<T>> responseList;

    CallArbiter(Call<T> call, Subscriber<? super Response<T>> subscriber) {
        super(STATE_WAITING);
        responseList = new LinkedList<>();

        this.call = call;
        this.subscriber = subscriber;
    }

    @Override
    public void unsubscribe() {
        call.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return call.isCanceled();
    }

    @Override
    public void request(long amount) {
        if (amount == 0) {
            return;
        }
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    if (compareAndSet(STATE_WAITING, STATE_REQUESTED)) {
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_HAS_RESPONSE:
                    if (STATE_HAS_RESPONSE == get()) {
                        emitResponse(responseList);
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_REQUESTED:
                case STATE_TERMINATED:
                    return; // Nothing to do.

                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    void emitNext(Response<T> response) {
        while (true) {
            int state = get();
            switch (state) {
                case STATE_WAITING:
                    synchronized (this) {
                        responseList.add(response);
                    }
                    if (compareAndSet(STATE_WAITING, STATE_HAS_RESPONSE)) {
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_REQUESTED:
                    synchronized (this) {
                        responseList.add(response);
                    }
                    if (STATE_REQUESTED == get()) {
                        emitResponse(responseList);
                        return;
                    }
                    break; // State transition failed. Try again.

                case STATE_HAS_RESPONSE:
                case STATE_TERMINATED:
                    throw new AssertionError();

                default:
                    throw new IllegalStateException("Unknown state: " + state);
            }
        }
    }

    private void emitResponse(List<Response<T>> responseList) {
        try {
            synchronized (this) {
                Iterator<Response<T>> iterator = responseList.iterator();
                while (iterator.hasNext()) {
                    Response<T> next = iterator.next();
                    iterator.remove();
                    if (!isUnsubscribed()) {
                        subscriber.onNext(next);
                    } else {
                        return;
                    }
                }
            }
        } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
            RxJavaHooks.getOnError().call(e);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            try {
                subscriber.onError(t);
            } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
                RxJavaHooks.getOnError().call(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaHooks.getOnError().call(new CompositeException(t, inner));
            }
        }
    }

    void emitComplete() {
        set(STATE_TERMINATED);
        try {
            if (!isUnsubscribed()) {
                subscriber.onCompleted();
            }
        } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
            RxJavaHooks.getOnError().call(e);
        } catch (Throwable t) {
            Exceptions.throwIfFatal(t);
            RxJavaHooks.getOnError().call(t);
        }
    }

    void emitError(Throwable t) {
        set(STATE_TERMINATED);
        if (!isUnsubscribed()) {
            try {
                subscriber.onError(t);
            } catch (OnCompletedFailedException | OnErrorFailedException | OnErrorNotImplementedException e) {
                RxJavaHooks.getOnError().call(e);
            } catch (Throwable inner) {
                Exceptions.throwIfFatal(inner);
                RxJavaHooks.getOnError().call(new CompositeException(t, inner));
            }
        }
    }
}
