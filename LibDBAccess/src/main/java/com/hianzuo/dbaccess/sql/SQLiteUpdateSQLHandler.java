package com.hianzuo.dbaccess.sql;

import android.content.ContentValues;
import android.text.TextUtils;
import com.hianzuo.dbaccess.util.ContentValues2xUtil;

/**
 * User: Ryan
 * Date: 14-4-3
 * Time: 上午10:41
 */
public class SQLiteUpdateSQLHandler {
    public static String create(String table, ContentValues values,
                                String whereClause) {
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("Empty values");
        }
        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(table);
        sql.append(" SET ");
        int i = 0;
        for (String colName : ContentValues2xUtil.keySet(values)) {
            sql.append((i > 0) ? "," : "");
            sql.append(colName);
            i++;
            sql.append("=?");
        }
        if (!TextUtils.isEmpty(whereClause)) {
            sql.append(" WHERE ");
            sql.append(whereClause);
        }
        return sql.toString();
    }
}
