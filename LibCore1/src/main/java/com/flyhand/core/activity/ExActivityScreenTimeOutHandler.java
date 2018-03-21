package com.flyhand.core.activity;

import android.os.Handler;
import android.os.Message;

import com.hianzuo.logger.Log;

import java.util.HashSet;

/**
 * Created by Ryan on 14/11/11.
 */
public class ExActivityScreenTimeOutHandler {
    private transient Handler mScreenTimeOutHandler;
    private int HANDLER_VALUE = 110;
    private long last_time_out_val = -1;
    private long time_out_val = 0;
    private long lastUIEventTime = System.currentTimeMillis();
    private HashSet<Watcher> watchers = new HashSet<Watcher>();
    private final String TAG = "ExActivityScreenTimeOutHandler";
    private static volatile ExActivityScreenTimeOutHandler handler;

    private ExActivityScreenTimeOutHandler() {
    }

    //改变屏幕的时候，或者改变超时时间的时候调用
    private void __onResume(long timeOutVal) {
        this.time_out_val = timeOutVal;
        Log.v(TAG, "onResume:" + timeOutVal);
        onValueChanged();
    }

    //触发屏幕事件的时候调用
    private void __onUIEvent() {
        lastUIEventTime = System.currentTimeMillis();
    }

    public void onValueChanged() {
        if (time_out_val != last_time_out_val) {
            last_time_out_val = time_out_val;

            if (null == this.mScreenTimeOutHandler) {
                this.mScreenTimeOutHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        //离上次未触发屏幕已经过了多少时间
                        final long pastTimeMillis = System.currentTimeMillis() - lastUIEventTime;
                        Log.v(TAG, "handleMessage:last_time_out_val["
                                + last_time_out_val + "]pastTimeMillis[" + pastTimeMillis + "]");
                        if (last_time_out_val > 0) {
                            if (pastTimeMillis >= last_time_out_val) {
                                fireOnScreenTimeOut();
                            } else {
                                //还差多少毫秒触发屏幕超时
                                final long eventTimeOutMillis = (last_time_out_val - pastTimeMillis);
                                Log.v(TAG, "handleMessage:eventTimeOutMillis[" + eventTimeOutMillis + "]");
                                mScreenTimeOutHandler.sendEmptyMessageDelayed(HANDLER_VALUE, eventTimeOutMillis);
                            }
                        }


                    }
                };
            }
            //离上次未触发屏幕已经过了多少时间
            final long pastTimeMillis = System.currentTimeMillis() - lastUIEventTime;
            //还差多少毫秒触发屏幕超时
            final long eventTimeOutMillis = (last_time_out_val - pastTimeMillis);
            Log.v(TAG, "离上次未触发屏幕已经过了多少时间:" + pastTimeMillis);
            Log.v(TAG, "还差多少毫秒触发屏幕超时:" + eventTimeOutMillis);

            this.mScreenTimeOutHandler.removeMessages(HANDLER_VALUE);
            this.mScreenTimeOutHandler.sendEmptyMessageDelayed(HANDLER_VALUE, eventTimeOutMillis);
        }
    }

    private void fireOnScreenTimeOut() {
        for (Watcher watcher : watchers) {
            watcher.onScreenTimeOut(last_time_out_val);
        }
    }

    private void __addWatcher(Watcher watcher) {
        watchers.add(watcher);
    }

    private void __removeWatcher(Watcher watcher) {
        watchers.remove(watcher);
    }

    private synchronized static void init() {
        if (null == handler) {
            handler = new ExActivityScreenTimeOutHandler();
        }
    }

    public synchronized static void addWatcher(Watcher watcher) {
        init();
        handler.__addWatcher(watcher);
    }


    public synchronized static void onUIEvent() {
        init();
        handler.__onUIEvent();
    }

    public synchronized static void onResume(long timeOut) {
        init();
        handler.__onResume(timeOut);
    }

    public synchronized static void removeWatcher(Watcher watcher) {
        init();
        handler.__removeWatcher(watcher);
    }

    public static interface Watcher {
        void onScreenTimeOut(long timeout);
    }
}
