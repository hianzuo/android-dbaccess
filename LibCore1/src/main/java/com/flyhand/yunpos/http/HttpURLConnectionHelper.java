package com.flyhand.yunpos.http;


import com.hianzuo.logger.Log;

import java.net.HttpURLConnection;
import java.util.HashMap;

/**
 * Created by Ryan
 * On 14/11/30.
 */
public class HttpURLConnectionHelper {
    private static final HashMap<Long, HttpURLConnection> mThreadConn = new HashMap<>();

    public static void set(HttpURLConnection conn) {
        mThreadConn.put(Thread.currentThread().getId(), conn);
    }

    public static HttpURLConnection get() {
        return mThreadConn.get(Thread.currentThread().getId());
    }

    public static HttpURLConnection get(Thread thread) {
        return mThreadConn.get(thread.getId());
    }

    public static void remove() {
        mThreadConn.remove(Thread.currentThread().getId());
    }

    public static void disconnect(Thread thread) {
        HttpURLConnection connection = get(thread);
        if (null != connection) {
            try {
                connection.disconnect();
            } catch (Exception e) {
                Log.eThrowable("HttpURLConnectionHelper", e);
            }
        }
    }
}
