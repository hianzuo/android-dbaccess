package com.flyhand.core.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * User: Ryan
 * Date: 11-10-15
 * Time: Afternoon 8:18
 */
public class AppkeyUtils {
    private static final String MINJIEKAIFA_KEY = "MINJIEKAIFA_APPKEY";

    public static String Get(Context context) {
        try {
            ApplicationInfo ai = context.getPackageManager().
                    getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            return bundle.getString(MINJIEKAIFA_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("PackageManager.NameNotFoundException(" + MINJIEKAIFA_KEY + ")");
        }
    }

}
