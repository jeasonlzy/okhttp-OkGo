package com.lzy.okserver.task;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.lzy.okserver.download.DownloadThreadPool;

import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/1/19
 * 描    述：完全按照AsyncTask的思路，主要是为了默认创建 PriorityRunnable 对象，和 getRunnable() 方法
 * 修订历史：
 * ================================================
 */
public abstract class PriorityAsyncTask<Params, Progress, Result> {

    private static final String LOG_TAG = "AsyncTask";
    private static final int MESSAGE_POST_RESULT = 0x1;
    private static final int MESSAGE_POST_PROGRESS = 0x2;

    private static final InternalHandler sHandler = new InternalHandler();
    private final WorkerRunnable<Params, Result> mWorker;
    private final FutureTask<Result> mFuture;
    private volatile boolean mExecuteInvoked = false;
    private final AtomicBoolean mCancelled = new AtomicBoolean();
    private final AtomicBoolean mTaskInvoked = new AtomicBoolean();

    //默认的线程池使用外界的
    public static final Executor sDefaultExecutor = new DownloadThreadPool().getExecutor();
    private Priority priority;
    private PriorityRunnable runnableTask;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    //返回需要的任务对象
    public Runnable getRunnable() {
        return runnableTask;
    }

    /**
     * Creates a new asynchronous task. This constructor must be invoked on the UI thread.
     */
    public PriorityAsyncTask() {
        mWorker = new WorkerRunnable<Params, Result>() {
            public Result call() throws Exception {
                mTaskInvoked.set(true);

                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                //noinspection unchecked
                return postResult(doInBackground(mParams));
            }
        };

        mFuture = new FutureTask<Result>(mWorker) {
            @Override
            protected void done() {
                try {
                    postResultIfNotInvoked(get());
                } catch (InterruptedException e) {
                    Log.w(LOG_TAG, e.getMessage());
                } catch (ExecutionException e) {
                    throw new RuntimeException("An error occured while executing doInBackground()", e.getCause());
                } catch (CancellationException e) {
                    postResultIfNotInvoked(null);
                }
            }
        };
    }

    private void postResultIfNotInvoked(Result result) {
        final boolean wasTaskInvoked = mTaskInvoked.get();
        if (!wasTaskInvoked) {
            postResult(result);
        }
    }

    private Result postResult(Result result) {
        @SuppressWarnings("unchecked") Message message = sHandler.obtainMessage(MESSAGE_POST_RESULT, new AsyncTaskResult<Result>(this, result));
        message.sendToTarget();
        return result;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    protected abstract Result doInBackground(Params... params);

    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    protected void onPreExecute() {
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param result The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onPostExecute(Result result) {
    }

    /**
     * Runs on the UI thread after {@link #publishProgress} is invoked.
     * The specified values are the values passed to {@link #publishProgress}.
     *
     * @param values The values indicating progress.
     * @see #publishProgress
     * @see #doInBackground
     */
    @SuppressWarnings({"UnusedDeclaration"})
    protected void onProgressUpdate(Progress... values) {
    }

    /**
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     * <p/>
     * <p>The default implementation simply invokes {@link #onCancelled()} and
     * ignores the result. If you write your own implementation, do not call
     * <code>super.onCancelled(result)</code>.</p>
     *
     * @param result The result, if any, computed in
     *               {@link #doInBackground(Object[])}, can be null
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    @SuppressWarnings({"UnusedParameters"})
    protected void onCancelled(Result result) {
        onCancelled();
    }

    /**
     * <p>Applications should preferably override {@link #onCancelled(Object)}.
     * This method is invoked by the default implementation of
     * {@link #onCancelled(Object)}.</p>
     * <p/>
     * <p>Runs on the UI thread after {@link #cancel(boolean)} is invoked and
     * {@link #doInBackground(Object[])} has finished.</p>
     *
     * @see #onCancelled(Object)
     * @see #cancel(boolean)
     * @see #isCancelled()
     */
    protected void onCancelled() {
    }

    /**
     * Returns <tt>true</tt> if this task was cancelled before it completed
     * normally. If you are calling {@link #cancel(boolean)} on the task,
     * the value returned by this method should be checked periodically from
     * {@link #doInBackground(Object[])} to end the task as soon as possible.
     *
     * @return <tt>true</tt> if task was cancelled before it completed
     * @see #cancel(boolean)
     */
    public final boolean isCancelled() {
        return mCancelled.get();
    }

    /**
     * @param mayInterruptIfRunning <tt>true</tt> if the thread executing this
     *                              task should be interrupted; otherwise, in-progress tasks are allowed
     *                              to complete.
     * @return <tt>false</tt> if the task could not be cancelled,
     * typically because it has already completed normally;
     * <tt>true</tt> otherwise
     * @see #isCancelled()
     * @see #onCancelled(Object)
     */
    public final boolean cancel(boolean mayInterruptIfRunning) {
        mCancelled.set(true);
        return mFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Waits if necessary for the computation to complete, and then
     * retrieves its result.
     *
     * @return The computed result.
     * @throws CancellationException If the computation was cancelled.
     * @throws ExecutionException    If the computation threw an exception.
     * @throws InterruptedException  If the current thread was interrupted
     *                               while waiting.
     */
    public final Result get() throws InterruptedException, ExecutionException {
        return mFuture.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation
     * to complete, and then retrieves its result.
     *
     * @param timeout Time to wait before cancelling the operation.
     * @param unit    The time unit for the timeout.
     * @return The computed result.
     * @throws CancellationException If the computation was cancelled.
     * @throws ExecutionException    If the computation threw an exception.
     * @throws InterruptedException  If the current thread was interrupted
     *                               while waiting.
     * @throws TimeoutException      If the wait timed out.
     */
    public final Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return mFuture.get(timeout, unit);
    }

    /**
     * @param params The parameters of the task.
     * @return This instance of AsyncTask.
     * @throws IllegalStateException If execute has invoked.
     * @see #executeOnExecutor(Executor, Object[])
     * @see #execute(Runnable)
     */
    public final PriorityAsyncTask<Params, Progress, Result> execute(Params... params) {
        return executeOnExecutor(sDefaultExecutor, params);
    }

    /**
     * @param exec   The executor to use.
     * @param params The parameters of the task.
     * @return This instance of AsyncTask.
     * @throws IllegalStateException If execute has invoked.
     * @see #execute(Object[])
     */
    public final PriorityAsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec, Params... params) {
        if (mExecuteInvoked) {
            throw new IllegalStateException("Cannot execute task:" + " the task is already executed.");
        }

        mExecuteInvoked = true;

        onPreExecute();

        mWorker.mParams = params;
        runnableTask = new PriorityRunnable(priority, mFuture);
        exec.execute(runnableTask);

        return this;
    }

    /**
     * Convenience version of {@link #execute(Object...)} for use with
     * a simple Runnable object. See {@link #execute(Object[])} for more
     * information on the order of execution.
     *
     * @see #execute(Object[])
     * @see #executeOnExecutor(Executor, Object[])
     */
    public static void execute(Runnable runnable) {
        execute(runnable, Priority.DEFAULT);
    }

    /**
     * Convenience version of {@link #execute(Object...)} for use with
     * a simple Runnable object. See {@link #execute(Object[])} for more
     * information on the order of execution.
     *
     * @see #execute(Object[])
     * @see #executeOnExecutor(Executor, Object[])
     */
    public static void execute(Runnable runnable, Priority priority) {
        sDefaultExecutor.execute(new PriorityRunnable(priority, runnable));
    }

    /**
     * This method can be invoked from {@link #doInBackground} to
     * publish updates on the UI thread while the background computation is
     * still running. Each call to this method will trigger the execution of
     * {@link #onProgressUpdate} on the UI thread.
     * {@link #onProgressUpdate} will note be called if the task has been
     * canceled.
     *
     * @param values The progress values to update the UI with.
     * @see #onProgressUpdate
     * @see #doInBackground
     */
    protected final void publishProgress(Progress... values) {
        if (!isCancelled()) {
            sHandler.obtainMessage(MESSAGE_POST_PROGRESS, new AsyncTaskResult<Progress>(this, values)).sendToTarget();
        }
    }

    private void finish(Result result) {
        if (isCancelled()) {
            onCancelled(result);
        } else {
            onPostExecute(result);
        }
    }

    private static class InternalHandler extends Handler {

        private InternalHandler() {
            super(Looper.getMainLooper());
        }

        @SuppressWarnings({"unchecked", "RawUseOfParameterizedType"})
        @Override
        public void handleMessage(Message msg) {
            AsyncTaskResult<?> result = (AsyncTaskResult<?>) msg.obj;
            switch (msg.what) {
                case MESSAGE_POST_RESULT:
                    // There is only one result
                    result.mTask.finish(result.mData[0]);
                    break;
                case MESSAGE_POST_PROGRESS:
                    result.mTask.onProgressUpdate(result.mData);
                    break;
            }
        }
    }

    private static abstract class WorkerRunnable<Params, Result> implements Callable<Result> {
        Params[] mParams;
    }

    @SuppressWarnings({"RawUseOfParameterizedType"})
    private static class AsyncTaskResult<Data> {
        final PriorityAsyncTask mTask;
        final Data[] mData;

        AsyncTaskResult(PriorityAsyncTask task, Data... data) {
            mTask = task;
            mData = data;
        }
    }
}
