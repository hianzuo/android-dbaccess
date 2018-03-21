package com.hianzuo.dbaccess;

/**
 * Created by Ryan
 * On 2016/10/18.
 */

public abstract class TransactionCallback {
    public static final int STATUS_COMMITTED = 0;
    public static final int STATUS_ROLLED_BACK = 1;
    public static final int STATUS_UNKNOWN = 2;

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
    }
}
