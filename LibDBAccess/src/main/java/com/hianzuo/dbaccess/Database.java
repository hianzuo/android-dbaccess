package com.hianzuo.dbaccess;

import android.content.ContentValues;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.SystemClock;

import com.hianzuo.dbaccess.config.DBHelper;
import com.hianzuo.dbaccess.sql.SQLiteDeleteSQLHandler;
import com.hianzuo.dbaccess.sql.SQLiteInsertSQLHandler;
import com.hianzuo.dbaccess.sql.SQLiteUpdateSQLHandler;
import com.hianzuo.dbaccess.throwable.LockedDatabaseException;
import com.hianzuo.dbaccess.throwable.UnLockedDatabaseException;
import com.hianzuo.logger.Log;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Ryan
 * On 14/12/17.
 */
public class Database {
    private android.database.sqlite.SQLiteDatabase db;
    private static String packageName;
    private AtomicInteger inTransactionCount = new AtomicInteger(0);
    private boolean isTransactionSuccessful = true;

    public Database(android.database.sqlite.SQLiteDatabase db) {
        if (null == packageName) {
            packageName = DBHelper.getContext().getPackageName();
        }
        if (null == db) {
            throw new IllegalArgumentException("db is null");
        }
        this.db = db;
    }

    public void beginTransaction() {
        beginTransaction("DBLockByTransaction");
    }

    public void beginTransaction(String lockTag) {
        lock(lockTag);
        if (inTransactionCount.incrementAndGet() == 1) {
            db.beginTransaction();
            isTransactionSuccessful = false;
            TransactionHandler.beginTransaction();
        }
    }

    public void setTransactionSuccessful() {
        if (inTransactionCount.get() == 1) {
            db.setTransactionSuccessful();
            isTransactionSuccessful = true;
            TransactionHandler.setTransactionSuccessful();
        }
    }

    public void endTransaction() {
        endTransaction(true);
    }

    private void endTransaction(boolean unlock) {
        if (inTransactionCount.decrementAndGet() == 0) {
            db.endTransaction();
            TransactionHandler.endTransaction(isTransactionSuccessful);
            isTransactionSuccessful = false;
        }
        if (unlock) {
            if (!hasTransaction()) {
                unlock();
            }
        }
    }

    public void execSQL(String sql) {
        checkLocked();
        printSQL(sql);
        db.execSQL(sql);
    }

    public SQLiteStatement compileStatement(String sql) {
        checkLocked();
        printSQL(sql);
        return db.compileStatement(sql);
    }


    private static void printSQL(String sql) {
        if (DBHelper.getDBConfig().isPrintSQL()) {
            Log.d("DB_SQL", sql);
        }
    }

    public Cursor rawQuery(String sql, String[] params) {
        try {
            printSQL(sql);
            return db.rawQuery(sql, params);
        } catch (SQLiteException e) {
            if (e.getMessage().contains("no such table")) {
                return new EmptyCursor();
            } else {
                throw e;
            }
        }
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        checkLocked();
        printSQL(SQLiteUpdateSQLHandler.create(table, values, whereClause));
        return db.updateWithOnConflict(table, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_NONE);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        checkLocked();
        printSQL(SQLiteDeleteSQLHandler.create(table, whereClause));
        return db.delete(table, whereClause, whereArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        checkLocked();
        printSQL(SQLiteInsertSQLHandler.create(table, nullColumnHack, values));
        return db.insertWithOnConflict(table, nullColumnHack, values, SQLiteDatabase.CONFLICT_NONE);
    }

    public long insertWithOnConflict(String table, String nullColumnHack,
                                     ContentValues values, int conflictReplace) {
        checkLocked();
        printSQL(SQLiteInsertSQLHandler.create(table, nullColumnHack, values));
        return db.insertWithOnConflict(table, nullColumnHack, values, conflictReplace);
    }

    private static String callers() {
        StackTraceElement[] elements = new Throwable().getStackTrace();
        StringBuilder sb = new StringBuilder();
        for (int i = elements.length - 1; i > -1; i--) {
            StackTraceElement element = elements[i];
            if (element.getClassName().startsWith(packageName)) {
                sb.append(element.getMethodName()).append("(")
                        .append(element.getFileName()).append(":")
                        .append(element.getLineNumber()).append(")\n");
            }
        }
        return sb.toString().trim() + "\n";
    }

    public String getPath() {
        return db.getPath();
    }

    public boolean isReadOnly() {
        return db.isReadOnly();
    }

    public boolean hasTransaction() {
        return inTransactionCount.get() > 0;
    }

    public void setSuccessfulCommitAndBegin() {
        forceCommitTransaction(this);
    }

    public static void forceCommitTransaction(Database db) {
        if (db.hasTransaction()) {
            db.setTransactionSuccessful();
            db.endTransaction(false);
            db.beginTransaction();
        }
    }

    public int getVersion() {
        return db.getVersion();
    }

    private void checkLocked() {
        checkLocked(30, 60);
    }

    private void checkLocked(int logWaitSec, int errorWaitSec) {
        if (null == mLockThread) {
            return;
        }
        if (Thread.currentThread() == mLockThread) {
            return;
        }
        long startWaitTime = SystemClock.elapsedRealtime();
        int startWaitLimit = 50;
        while (true) {
            if (null != mLockThread) {
                //noinspection SynchronizeOnNonFinalField
                synchronized (mLockThread) {
                    try {
                        mLockThread.wait(startWaitLimit);
                    } catch (Exception ignored) {
                    }
                }
                if (null == mLockThread) {
                    break;
                }
                startWaitLimit += 50;
                int runTime = (int) ((SystemClock.elapsedRealtime() - startWaitTime) / 1000);
                if (runTime > errorWaitSec) {
                    throw new LockedDatabaseException(mLockTag, "db lock in other thread(" + mLockThread.getName() + ") more than " + runTime + " sec.");
                } else if (runTime > logWaitSec) {
                    Log.w(mLockTag, "db lock in other thread(" + mLockThread.getName() + ") more than " + runTime + " sec.");
                }
            } else {
                break;
            }
        }
    }

    private static Thread mLockThread;
    private static String mLockTag;

    private synchronized void lock(String lockTag) {
        Thread currentThread = Thread.currentThread();
        if (null == mLockThread || currentThread == mLockThread) {
            mLockTag = lockTag;
            mLockThread = currentThread;
        } else {
            checkLocked(20, 50);
        }
    }

    private synchronized void unlock() {
        if (null == mLockThread) {
            return;
        }
        Thread currentThread = Thread.currentThread();
        if (currentThread == mLockThread) {
            mLockThread = null;
            mLockTag = null;
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (currentThread) {
                currentThread.notifyAll();
            }
            try {
                //必须要进行睡眠，其他线程好获得执行权
                Thread.sleep(1);
            } catch (Exception ignored) {
            }
        } else {
            throw new UnLockedDatabaseException("db can not unlock in thread(" + currentThread.getName() + ") current lock thread is (" + mLockThread.getName() + ") lock tag is (" + mLockTag + ").");
        }
    }

    public void close() {
        checkLocked();
        if (null != db && db.isOpen()) {
            db.close();
        }
    }

    private static class EmptyCursor extends AbstractCursor {
        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public String[] getColumnNames() {
            return new String[0];
        }

        @Override
        public String getString(int column) {
            return null;
        }

        @Override
        public short getShort(int column) {
            return 0;
        }

        @Override
        public int getInt(int column) {
            return 0;
        }

        @Override
        public long getLong(int column) {
            return 0;
        }

        @Override
        public float getFloat(int column) {
            return 0;
        }

        @Override
        public double getDouble(int column) {
            return 0;
        }

        @Override
        public boolean isNull(int column) {
            return false;
        }
    }
}
