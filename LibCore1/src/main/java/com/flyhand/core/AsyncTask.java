package com.flyhand.core;

import com.hianzuo.logger.Log;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ryan
 * On 2016/4/20.
 */
public abstract class AsyncTask<Params, Progress, Result> {
    private static final String TAG = "AsyncTask";
    private long startTime;
    protected String caller;
    private android.os.AsyncTask<Params, Progress, Result> mTarget;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 1;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 3;
    private static final int KEEP_ALIVE = 1;
    private static final BlockingQueue<Runnable> sPoolWorkQueue =
            new LinkedBlockingQueue<Runnable>(128);
    public static final Executor SERIAL_EXECUTOR = new SerialExecutor();
    private static final ThreadFactory sThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, "com.flyhand.core.AsyncTask #" + mCount.getAndIncrement());
            thread.setPriority(1);
            return thread;
        }
    };

    public static final Executor THREAD_POOL_EXECUTOR
            = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
            TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    protected AsyncTask() {
        caller = callers();
        mTarget = new android.os.AsyncTask<Params, Progress, Result>() {
            @SafeVarargs
            @Override
            protected final Result doInBackground(Params... params) {
                return AsyncTask.this.doInBackground(params);
            }

            @Override
            protected void onPreExecute() {
                AsyncTask.this.onPreExecute();
            }

            @Override
            protected void onPostExecute(Result result) {
                AsyncTask.this.onPostExecute(result);
            }

            @SafeVarargs
            @Override
            protected final void onProgressUpdate(Progress... values) {
                AsyncTask.this.onProgressUpdate(values);
            }

            @Override
            protected void onCancelled(Result result) {
                AsyncTask.this.onCancelled(result);
            }

            @Override
            protected void onCancelled() {
                AsyncTask.this.onCancelled();
            }
        };
    }

    @SuppressWarnings("unchecked")
    protected Result doInBackground(Params... params) {
        return null;
    }

    protected void onPreExecute() {
        startTime = System.currentTimeMillis();
    }

    protected void onPostExecute(Result result) {
        long speedTime = System.currentTimeMillis() - startTime;
        if (speedTime > 2500) {
            Log.d(TAG, "Spend:" + speedTime + ",Caller:" + caller);
        }
    }

    @SafeVarargs
    protected final void onProgressUpdate(Progress... values) {
    }

    protected void onCancelled(Result result) {
    }

    protected void onCancelled() {
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> execute(Params... params) {
        executeOnExecutor(SERIAL_EXECUTOR, params);
        return this;
    }

    @SafeVarargs
    public final AsyncTask<Params, Progress, Result> executeOnExecutor(Executor exec,
                                                                       Params... params) {
        mTarget.executeOnExecutor(exec, params);
        return this;
    }


    public static String callers() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        int maxLine = 2;
        StringBuilder sb = new StringBuilder("");
        int line = 0;
        HashSet<String> set = new HashSet<>();
        for (int i = 3; i < elements.length; i++) {
            StackTraceElement element = elements[i];
            if (element.getClassName().startsWith("com.flyhand")) {
                String fileNameNumber = element.getFileName() + ":" + element.getLineNumber();
                if (!set.contains(fileNameNumber)) {
                    set.add(fileNameNumber);
                    sb.append("(").append(fileNameNumber).append(")").append(" >> ");
                    if ((++line) >= maxLine) {
                        break;
                    }
                }
            }
        }
        set.clear();
        if (line > 0) {
            sb.delete(sb.length() - 4, sb.length());
        }
        return sb.toString().trim();
    }


    private static class SerialExecutor implements Executor {
        final ArrayDeque<Runnable> mTasks = new ArrayDeque<Runnable>();
        Runnable mActive;

        @Override
        public synchronized void execute(final Runnable r) {
            mTasks.offer(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.run();
                    } finally {
                        scheduleNext();
                    }
                }
            });
            if (mActive == null) {
                scheduleNext();
            }
        }

        protected synchronized void scheduleNext() {
            if ((mActive = mTasks.poll()) != null) {
                THREAD_POOL_EXECUTOR.execute(mActive);
            }
        }
    }

}
