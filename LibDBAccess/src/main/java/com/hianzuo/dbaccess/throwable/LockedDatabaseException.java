package com.hianzuo.dbaccess.throwable;

/**
 * Created by Ryan
 * On 2017/4/7.
 */

public class LockedDatabaseException extends RuntimeException {
    private String lockTag;
    public LockedDatabaseException(String lockTag,String detailMessage) {
        super(detailMessage);
        this.lockTag = lockTag;
    }

    public String getLockTag() {
        return lockTag;
    }
}
