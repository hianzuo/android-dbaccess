package com.hianzuo.dbaccess.util;

import android.database.Cursor;

/**
 * Created by Ryan on 15/10/15.
 */
public class CursorUtils {
    public static void close(Cursor cursor){
        if(null != cursor && !cursor.isClosed()) cursor.close();
    }
}
