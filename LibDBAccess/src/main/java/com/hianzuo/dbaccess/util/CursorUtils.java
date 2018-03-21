package com.hianzuo.dbaccess.util;

import android.database.Cursor;
import android.util.Base64;

/**
 * Created by Ryan on 15/10/15.
 */
public class CursorUtils {
    public static void close(Cursor cursor) {
        if (null != cursor && !cursor.isClosed()) {
            cursor.close();
        }
    }

    public static String getString(Cursor cursor, String name) {
        int columnIndex = cursor.getColumnIndex(name);
        return getString(cursor, columnIndex);
    }

    public static String getString(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else {
            int type = cursor.getType(columnIndex);
            switch (type) {
                case Cursor.FIELD_TYPE_BLOB:
                    try {
                        return "[BLOD]" + new String(cursor.getBlob(columnIndex), "utf-8");
                    } catch (Exception e) {
                        try {
                            return "[BLOD]" + Base64.encodeToString(cursor.getBlob(columnIndex), Base64.DEFAULT);
                        } catch (Exception e1) {
                            return "[BLOD]";
                        }
                    }
                case Cursor.FIELD_TYPE_FLOAT:
                    return String.valueOf(cursor.getDouble(columnIndex));
                case Cursor.FIELD_TYPE_INTEGER:
                    return String.valueOf(cursor.getLong(columnIndex));
                case Cursor.FIELD_TYPE_STRING:
                    return cursor.getString(columnIndex);
                default:
                    return cursor.getString(columnIndex);
            }
        }
    }

    public static Object get(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        } else {
            int type = cursor.getType(columnIndex);
            switch (type) {
                case Cursor.FIELD_TYPE_BLOB:
                    return cursor.getBlob(columnIndex);
                case Cursor.FIELD_TYPE_FLOAT:
                    return cursor.getDouble(columnIndex);
                case Cursor.FIELD_TYPE_INTEGER:
                    return cursor.getLong(columnIndex);
                case Cursor.FIELD_TYPE_STRING:
                    return cursor.getString(columnIndex);
                default:
                    return cursor.getString(columnIndex);
            }
        }
    }
}
