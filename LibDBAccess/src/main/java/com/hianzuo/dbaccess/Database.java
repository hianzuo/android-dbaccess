package com.hianzuo.dbaccess;

import android.content.ContentValues;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import com.hianzuo.dbaccess.config.DBHelper;

/**
 * Created by Ryan on 14/12/17.
 */
public class Database {
    private android.database.sqlite.SQLiteDatabase db;
    private static String packageName;
    private int inTransactionCount = 0;
    private int mThisHashCode;

    public Database(android.database.sqlite.SQLiteDatabase db) {
        if (null == packageName) {
            packageName = DBHelper.getContext().getPackageName();
        }
        if (null == db) throw new IllegalArgumentException("db is null");
        this.mThisHashCode = hashCode();
        this.db = db;
    }

    public void beginTransaction() {
        // Log.e("Database(" + mThisHashCode + ")", "\nbeginTransaction(inTransactionCount:" + inTransactionCount + ")\n" + callers());
        inTransactionCount++;
        if (inTransactionCount == 1) {
            db.beginTransaction();
        }
    }

    public void setTransactionSuccessful() {
        //  Log.e("Database(" + mThisHashCode + ")", "\nsetTransactionSuccessful(inTransactionCount:" + inTransactionCount + ")\n" + callers());
        if (1 == inTransactionCount) {
            db.setTransactionSuccessful();
        }
    }

    public void endTransaction() {
        // Log.e("Database(" + mThisHashCode + ")", "\nendTransaction(inTransactionCount:" + inTransactionCount + ")\n" + callers());
        if (1 == inTransactionCount) {
            db.endTransaction();
        }
        inTransactionCount--;
    }

    public void execSQL(String sql) {
        db.execSQL(sql);
    }

    public SQLiteStatement compileStatement(String sql) {
        return db.compileStatement(sql);
    }

    public Cursor rawQuery(String sql, String[] params) {
        try {
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
        return db.updateWithOnConflict(table, values, whereClause, whereArgs, SQLiteDatabase.CONFLICT_NONE);
    }

    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    public long insert(String table, String nullColumnHack, ContentValues values) {
        return db.insertWithOnConflict(table, nullColumnHack, values, SQLiteDatabase.CONFLICT_NONE);
    }

    public long insertWithOnConflict(String tableName, String nullColumnHack,
                                     ContentValues initialValues, int conflictReplace) {
        return db.insertWithOnConflict(tableName, nullColumnHack, initialValues, conflictReplace);
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
        return inTransactionCount > 0;
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
