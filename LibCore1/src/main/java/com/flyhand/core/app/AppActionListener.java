package com.flyhand.core.app;

/**
 * Created by Ryan
 * On 2016/5/7.
 */
public interface AppActionListener {
    void onReceiveBroadcast(String action, android.content.Intent intent);
}
