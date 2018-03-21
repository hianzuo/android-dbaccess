package com.flyhand.core.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.flyhand.core.app.AbstractCoreApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-12-2
 * Time: Afternoon 3:24
 */
public class NetworkUtils {
    private final static LinkedList<NetworkStatusChangeListener> listeners =
            new LinkedList<>();
    private static boolean started = false;
    private static final boolean DEBUG = false;
    private static final String TAG = "NetworkUtils";

    public static void start() {
        if (!started) {
            started = true;
            AbstractCoreApplication.get().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, android.content.Intent intent) {
                    ArrayList<NetworkStatusChangeListener> list =
                            new ArrayList<NetworkStatusChangeListener>();
                    synchronized (listeners) {
                        if (listeners.size() > 0) {
                            for (NetworkStatusChangeListener listener : listeners) {
                                list.add(listener);
                            }
                        }
                    }
                    ConnectivityManager cm = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    for (NetworkStatusChangeListener listener : list) {
                        if (null != info) {
                            listener.onNetworkAvailable(info);
                        } else {
                            listener.onNetworkDisable();
                        }
                    }
                }
            }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

    }

    public static boolean isAvailable(Context context) {
        context = context.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] allNetworkInfo = connectivity.getAllNetworkInfo();
            List<NetworkInfo> networkInfoList = new ArrayList<>();
            if (null != allNetworkInfo) {
                Collections.addAll(networkInfoList, allNetworkInfo);
            }
            //商米T1没有返回有线网络连接，但是getActiveNetworkInfo返回了。
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            if (null != activeNetworkInfo) {
                networkInfoList.add(0, activeNetworkInfo);
            }
            for (NetworkInfo networkInfo : networkInfoList) {
                if (DEBUG) {
                    Log.d(TAG, "NetworkInfo: " + networkInfo.toString());
                }
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    if (DEBUG) {
                        Log.d(TAG, "isAvailable true");
                    }
                    return true;
                }
            }
        }
        if (DEBUG) {
            Log.d(TAG, "isAvailable false");
        }
        return false;
    }

    public static void addListener(NetworkStatusChangeListener listener) {
        NetworkUtils.start();
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public static void removeListener(NetworkStatusChangeListener listener) {
        NetworkUtils.start();
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public static interface NetworkStatusChangeListener {
        void onNetworkAvailable(NetworkInfo info);

        void onNetworkDisable();
    }
}
