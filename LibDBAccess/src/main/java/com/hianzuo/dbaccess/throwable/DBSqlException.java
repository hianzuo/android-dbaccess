package com.hianzuo.dbaccess.throwable;

import com.hianzuo.dbaccess.util.StringUtil;

/**
 * Created by Ryan on 15/3/30.
 */
public class DBSqlException extends RuntimeException {
    public DBSqlException(String sql, Throwable throwable) {
        super(sql, throwable);
    }

    public DBSqlException(String sql, Throwable throwable, String... params) {
        super("sql[" + (null == sql ? "NULL" : sql) + "], params[" + StringUtil.join(params, ",") + "]", throwable);
    }
    public DBSqlException(String sql, Throwable throwable, Object... params) {
        super("sql[" + (null == sql ? "NULL" : sql) + "], params[" + StringUtil.join(params, ",") + "]", throwable);
    }
}
