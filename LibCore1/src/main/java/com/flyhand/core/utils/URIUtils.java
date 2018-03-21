package com.flyhand.core.utils;

import android.net.Uri;

/**
 * User: Ryan
 * Date: 11-10-16
 * Time: Afternoon 9:09
 */
public class URIUtils {
    public static Uri create(String uri) {
        try {
            if (null == uri || "null".equals(uri)) {
                return null;
            }
            return Uri.parse(uri);
        } catch (Exception e) {
            return null;
        }
    }
}
