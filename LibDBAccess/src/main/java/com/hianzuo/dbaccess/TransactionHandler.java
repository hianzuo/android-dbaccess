package com.hianzuo.dbaccess;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by Ryan
 * On 2016/10/18.
 */

public abstract class TransactionHandler {
    private static final ThreadLocal<Set<TransactionCallback>> mTransactionCallbackSet = new ThreadLocal<>();

    private static void initTransactionCallback() throws IllegalStateException {
        if (isTransactionActive()) {
            throw new IllegalStateException("Cannot activate transaction callback - already active");
        }
        mTransactionCallbackSet.set(new LinkedHashSet<TransactionCallback>());
    }

    public static void afterCommit(TransactionCallback callback) {
        if (isTransactionActive()) {
            addCallback(callback);
        } else {
            callback.afterCommit();
        }
    }

    public static void addCallback(TransactionCallback callback) {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Transaction is not active for current thread");
        }
        mTransactionCallbackSet.get().add(callback);
    }

    public static boolean isTransactionActive() {
        return (mTransactionCallbackSet.get() != null);
    }


    private static void clearSynchronization() throws IllegalStateException {
        if (!isTransactionActive()) {
            throw new IllegalStateException("Cannot deactivate transaction synchronization - not active");
        }
        mTransactionCallbackSet.remove();
    }

    static void beginTransaction() {
        if (!isTransactionActive()) {
            initTransactionCallback();
        }
    }

    static void setTransactionSuccessful() {
        if (isTransactionActive()) {
            Set<TransactionCallback> callbacks = mTransactionCallbackSet.get();
            if (null != callbacks) {
                for (TransactionCallback callback : callbacks) {
                    callback.afterCommit();
                }
            }
        }

    }

    static void endTransaction(boolean transactionSuccessful) {
        if (isTransactionActive()) {
            Set<TransactionCallback> callbacks = mTransactionCallbackSet.get();
            if (null != callbacks) {
                for (TransactionCallback callback : callbacks) {
                    callback.afterCompletion(transactionSuccessful ? TransactionCallback.STATUS_COMMITTED : TransactionCallback.STATUS_ROLLED_BACK);
                }
            }
            clearSynchronization();
        }
    }
}
