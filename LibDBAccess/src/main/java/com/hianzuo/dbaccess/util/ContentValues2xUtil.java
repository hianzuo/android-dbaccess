package com.hianzuo.dbaccess.util;

import android.content.ContentValues;
import android.os.Build;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;

/**
 * Create by Ryan on 14-9-26.
 */
public class ContentValues2xUtil {
    public static Set<String> keySet(ContentValues initialValues) {
        if (Build.VERSION.SDK_INT > 11) {
            return initialValues.keySet();
        } else {
            try {
                Field field = ContentValues.class.getDeclaredField("mValues");
                field.setAccessible(true);
                //noinspection unchecked
                HashMap<String, Object> values = (HashMap<String, Object>)
                        field.get(initialValues);
                return values.keySet();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
