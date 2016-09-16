package com.hianzuo.dbaccess.sql;

import android.content.ContentValues;
import com.hianzuo.dbaccess.util.ContentValues2xUtil;

/**
 * User: Ryan
 * Date: 14-4-3
 * Time: 上午10:41
 */
public class SQLiteInsertSQLHandler {

    public static String create(String table, String nullColumnHack, ContentValues initialValues) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(table);
        sql.append('(');

        int size = (initialValues != null && initialValues.size() > 0) ? initialValues.size() : 0;
        if (size > 0) {
            int i = 0;
            for (String colName : ContentValues2xUtil.keySet(initialValues)) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                i++;
            }
            sql.append(')');
            sql.append(" VALUES (");
            for (i = 0; i < size; i++) {
                sql.append((i > 0) ? ",?" : "?");
            }
        } else {
            sql.append(nullColumnHack).append(") VALUES (NULL");
        }
        sql.append(')');
        return sql.toString();
    }


}
