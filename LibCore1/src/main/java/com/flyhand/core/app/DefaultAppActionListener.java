package com.flyhand.core.app;

import android.content.Intent;
import android.net.ConnectivityManager;

import com.flyhand.core.apphelper.AppHelper;

/**
 * Created by Ryan
 * On 2016/5/7.
 */
public class DefaultAppActionListener implements AppActionListener {
    @Override
    public void onReceiveBroadcast(String action, Intent intent) {
        if (Intent.ACTION_TIME_TICK.equals(action)) {
            onReceiveTimeTick(intent);
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            onReceiveConnectivityChange(intent);
        } else if (AppHelper.ACTION_ON_NETWORK_CHANGED.equals(action)) {
            onReceiveAppNetworkChange(intent);
        } else {
            onReceive(action, intent);
        }
    }

    public void onReceive(String action, Intent intent) {

    }

    public void onReceiveConnectivityChange(Intent intent) {

    }

    public void onReceiveAppNetworkChange(Intent intent) {
    }

    public void onReceiveTimeTick(Intent intent) {

    }
}
