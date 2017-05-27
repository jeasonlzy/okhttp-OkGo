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

import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.model.Response;

import rx.Observable.OnSubscribe;
import rx.Subscriber;
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
public final class BodyOnSubscribe<T> implements OnSubscribe<T> {
    private final OnSubscribe<Response<T>> upstream;

    public BodyOnSubscribe(OnSubscribe<Response<T>> upstream) {
        this.upstream = upstream;
    }

    @Override
    public void call(Subscriber<? super T> subscriber) {
        upstream.call(new BodySubscriber<>(subscriber));
    }

    private static class BodySubscriber<R> extends Subscriber<Response<R>> {

        private final Subscriber<? super R> subscriber;
        private boolean subscriberTerminated;

        BodySubscriber(Subscriber<? super R> subscriber) {
            super(subscriber);
            this.subscriber = subscriber;
        }

        @Override
        public void onNext(Response<R> response) {
            if (response.isSuccessful()) {
                subscriber.onNext(response.body());
            } else {
                subscriberTerminated = true;
                Throwable t = new HttpException(response);
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

        @Override
        public void onError(Throwable throwable) {
            if (!subscriberTerminated) {
                subscriber.onError(throwable);
            } else {
                Throwable broken = new AssertionError("This should never happen! Report as a bug with the full stacktrace.");
                //noinspection UnnecessaryInitCause Two-arg AssertionError constructor is 1.7+ only.
                broken.initCause(throwable);
                RxJavaHooks.getOnError().call(broken);
            }
        }

        @Override
        public void onCompleted() {
            if (!subscriberTerminated) {
                subscriber.onCompleted();
            } else {
                Throwable broken = new AssertionError("This should never happen! Report as a bug with the full stacktrace.");
                RxJavaHooks.getOnError().call(broken);
            }
        }
    }
}
