package com.hianzuo.dbaccess.throwable;

/**
 * User: Ryan
 * Date: 14-3-21
 * Time: 下午4:51
 */
public class DBRuntimeException extends RuntimeException {
    public DBRuntimeException(String detailMessage) {
        super(detailMessage);
    }

    public DBRuntimeException(Throwable throwable) {
        super(throwable);
    }

    public DBRuntimeException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
