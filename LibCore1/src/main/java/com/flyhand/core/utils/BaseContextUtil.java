package com.flyhand.core.utils;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * Created by Ryan
 * On 2016/8/19.
 */
public class BaseContextUtil {
    public static Context get(Context context) {
        if (null == context) {
            return null;
        }
        if (context instanceof Activity || context instanceof Service || context instanceof Application) {
            return context;
        }
        if (context instanceof ContextWrapper) {
            Context baseContext = ((ContextWrapper) context).getBaseContext();
            return get(baseContext);
        }
        return context;
    }
}
