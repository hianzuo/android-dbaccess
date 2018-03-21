package com.hianzuo.dbaccess.throwable;

/**
 * Created by Ryan
 * On 2017/5/9.
 */

public class UnLockedDatabaseException extends RuntimeException {
    public UnLockedDatabaseException(String detailMessage) {
        super(detailMessage);
    }
}
