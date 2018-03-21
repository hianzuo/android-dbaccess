package com.flyhand.core.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.flyhand.core.apphelper.AppHelper;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Ryan
 * On 2016/9/1.
 */
public class CoreAppActionReceiver extends BroadcastReceiver {
    private static final HashMap<String, Set<AppActionListener>> mActionListenerMap = new HashMap<>();
    private static CoreAppActionReceiver mReceiver;

    public void onCreate(AbstractCoreApplication application) {
        application.registerReceiver(this, new IntentFilter(Intent.ACTION_TIME_TICK));
        application.registerReceiver(this, new IntentFilter(Intent.ACTION_PACKAGE_RESTARTED));
        application.registerReceiver(this, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        application.registerReceiver(this, AppHelper.ACTION_ON_NETWORK_CHANGED_FILTER);
        application.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_ON"));
        application.registerReceiver(this, new IntentFilter("android.intent.action.SCREEN_OFF"));
        mReceiver = this;
    }

    public static void init(AbstractCoreApplication application) {
        CoreAppActionReceiver receiver = new CoreAppActionReceiver();
        receiver.onCreate(application);
    }

    public static CoreAppActionReceiver get() {
        return mReceiver;
    }

    public void onDestroy(AbstractCoreApplication application) {
        application.unregisterReceiver(this);
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        fireAllListener(intent);
        onReceiveBroadcast(context, intent);
    }

    protected void onReceiveBroadcast(Context context, Intent intent) {
    }

    protected void fireAllListener(android.content.Intent intent) {
        synchronized (mActionListenerMap) {
            String action = intent.getAction();
            Set<AppActionListener> listenerSet = mActionListenerMap.get(action);
            if (null != listenerSet) {
                HashSet<AppActionListener> fireListenerSet = new HashSet<>(listenerSet);
                for (AppActionListener listener : fireListenerSet) {
                    listener.onReceiveBroadcast(action, intent);
                }
            }
        }
    }

    public static void addTimeTick(AppActionListener listener) {
        add(Intent.ACTION_TIME_TICK, listener);
    }

    public static void addConnectivity(AppActionListener listener) {
        add(ConnectivityManager.CONNECTIVITY_ACTION, listener);
    }

    public static void addAppNetwork(AppActionListener listener) {
        add(AppHelper.ACTION_ON_NETWORK_CHANGED, listener);
    }

    public static void add(String action, AppActionListener listener) {
        synchronized (mActionListenerMap) {
            Set<AppActionListener> listenerSet = mActionListenerMap.get(action);
            if (null == listenerSet) {
                listenerSet = new HashSet<>();
                mActionListenerMap.put(action, listenerSet);
            }
            listenerSet.add(listener);
        }
    }

    public synchronized static void remove(AppActionListener listener) {
        synchronized (mActionListenerMap) {
            for (String action : mActionListenerMap.keySet()) {
                if (mActionListenerMap.get(action).remove(listener)) {
                    //remove success.
                }
            }
        }
    }

    public synchronized static void clear() {
        synchronized (mActionListenerMap) {
            mActionListenerMap.clear();
        }
    }


    public static void release(AbstractCoreApplication application) {
        if (null != mReceiver) {
            try {
                application.unregisterReceiver(mReceiver);
            } catch (Exception ignored) {
            }
        }
        clear();
        mReceiver = null;
    }
}
