package com.flyhand.core;


import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.os.IBinder;

import com.flyhand.core.app.CoreAppActionReceiver;
import com.flyhand.core.app.DefaultAppActionListener;
import com.hianzuo.logger.Log;

import java.io.FileDescriptor;
import java.io.PrintWriter;

/**
 * Created by Ryan
 * On 15/6/9.
 *
 * @author Ryan
 */
public abstract class BaseService extends android.app.Service {
    private boolean mDatabaseUpdateDone = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseUpdateDone = false;
        CoreAppActionReceiver.addTimeTick(mAppActionListener);
    }

    private DefaultAppActionListener mAppActionListener =
            new DefaultAppActionListener() {
                @Override
                public void onReceiveTimeTick(Intent intent) {
                    runOnceMinute();
                }
            };

    public void runOnceMinuteInternal() {
        if (!mDatabaseUpdateDone) {
            return;
        }
        runOnceMinute();
    }

    public void runOnceMinute() {
    }

    protected void onDatabaseUpdateDoneInternal() {
        mDatabaseUpdateDone = true;
        onDatabaseUpdateDone();
    }

    public void onDatabaseUpdateDone() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.d("YunPOSService", "super.onStart(intent, startId);");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("YunPOSService", "return super.onStartCommand(intent, flags, startId);");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d("YunPOSService", "super.onDestroy();");
        CoreAppActionReceiver.remove(mAppActionListener);
        Log.flush();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d("YunPOSService", "super.onConfigurationChanged(newConfig);");
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        Log.d("YunPOSService", "super.onLowMemory();");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        Log.d("YunPOSService", "public super.onTrimMemory(level);");
        super.onTrimMemory(level);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("YunPOSService", "public IBinder onBind(Intent intent) {");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("YunPOSService", "return super.onUnbind(intent);");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("YunPOSService", "super.onRebind(intent);");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("YunPOSService", "super.onTaskRemoved(rootIntent);");
        super.onTaskRemoved(rootIntent);
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        Log.d("YunPOSService", "super.dump(fd, writer, args);");
        super.dump(fd, writer, args);
    }

    @Override
    public ComponentName startService(android.content.Intent oldIntent) {
        return super.startService(ExplicitIntentUtil.get(this, oldIntent));
    }

    @Override
    public boolean bindService(android.content.Intent oldIntent, ServiceConnection conn, int flags) {
        return super.bindService(ExplicitIntentUtil.get(this, oldIntent), conn, flags);
    }
}
