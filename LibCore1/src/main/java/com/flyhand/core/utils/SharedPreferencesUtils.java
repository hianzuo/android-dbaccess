package com.flyhand.core.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * User: Ryan
 * Date: 11-10-16
 * Time: Afternoon 9:42
 */
public class SharedPreferencesUtils {
    public static SharedPreferences getMinJieKaiFaPreferences(Context context) {
        return context.getSharedPreferences("com_ryan_core_minjiekaifa.properties", Context.MODE_PRIVATE);
    }

    public static SharedPreferences getMultiProgress(Context context) {
        return context.getSharedPreferences("yunpos_multi_progress.properties", Context.MODE_MULTI_PROCESS);
    }

    public static SharedPreferences get(Context context, String name) {
        return context.getSharedPreferences(name + ".properties", Context.MODE_MULTI_PROCESS);
    }
}
