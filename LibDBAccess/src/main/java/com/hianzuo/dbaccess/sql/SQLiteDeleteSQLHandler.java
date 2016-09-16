package com.hianzuo.dbaccess.sql;

import android.text.TextUtils;

/**
 * User: Ryan
 * Date: 14-4-3
 * Time: 上午10:41
 */
public class SQLiteDeleteSQLHandler {
    public static String create(String table, String whereClause) {
        return "DELETE FROM " + table + (!TextUtils.isEmpty(whereClause) ? " WHERE " + whereClause : "");
    }
}
