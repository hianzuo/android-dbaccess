package com.flyhand.core.utils;

import android.os.Handler;
import android.os.Message;

/**
 * Created by Joe (dingjiyong2008@qq.com).
 * Date: 11-9-24  Afternoon 4:02
 */
public class HandlerUtil {
    public static void send(Handler handler, int what) {
        if (null != handler) {
            handler.sendEmptyMessage(what);
        }
    }

    public static void send(Handler handler, int what, Object obj) {
        if (null != handler) {
            handler.sendMessage(handler.obtainMessage(what, obj));
        }
    }

    public static void send(Handler handler, Message msg) {
        if (null != handler) {
            handler.sendMessage(msg);
        }
    }
}
