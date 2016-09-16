package com.hianzuo.dbaccess.config;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hianzuo.dbaccess.DBInterface;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.store.SQLiteTable;
import com.hianzuo.dbaccess.store.SQLiteTableColumn;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: Ryan
 * Date: 2011-1-14
 * Time: 22:37:57
 */
public class DBHelper extends SQLiteOpenHelper {
    private static DBConfig dbConfig;
    private AtomicInteger mOpenCounter = new AtomicInteger();

    public static void config(DBConfig config) {
        DBHelper.dbConfig = config;
    }

    public static DBConfig getDBConfig() {
        if (null == dbConfig) {
            throw new DBRuntimeException("you must call DBHelper#config first.");
        } else {
            return dbConfig;
        }
    }

    public static Context getContext() {
        return getDBConfig().getContext();
    }

    public DBHelper() {
        super(getContext(), getDBConfig().getDBFile(), null, getDBConfig().getDBVersion());
        Log.e("DBHelper", getDatabaseName());
    }

    public DBHelper(Integer dbVersion) {
        super(getContext(), getDBConfig().getDBFile(), null, dbVersion);
        Log.e("DBHelper1", getDatabaseName());
    }


    private SQLiteDatabase mDefaultWritableDatabase = null;

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase db = mDefaultWritableDatabase;
        if (null == db || !db.isOpen()) {
            mOpenCounter.set(0);
        }
        if (mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDefaultWritableDatabase = super.getWritableDatabase();
        }
        return mDefaultWritableDatabase;
    }

    @Override
    public synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            super.close();
            if (null != mDefaultWritableDatabase) {
                mDefaultWritableDatabase.close();
            }
            mDefaultWritableDatabase = null;
        }
    }

    public synchronized void forceClose() {
        try {
            // Closing database
            super.close();
            if (null != mDefaultWritableDatabase) {
                mDefaultWritableDatabase.close();
            }
            mDefaultWritableDatabase = null;
        } finally {
            mOpenCounter.set(0);
        }
    }

    public static boolean deleteDatabase(Context context) {
        try {
            DBInterface.lock();
            return context.deleteDatabase(getDBConfig().getDBFile());
        } finally {
            DBInterface.unlock();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        onOpen(new Database(db));
        mDefaultWritableDatabase = db;
    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        mDefaultWritableDatabase = db;
        onOpen(new Database(db));
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        try {
            mDefaultWritableDatabase = database;
            Database db = new Database(database);
            this.onOpen(db);
            getDBConfig().onUpgrade(db, oldVersion, newVersion);
            Log.e("DBHelper", "CHECK_STRUCTURE_WHEN_UPDATE:newVersion:" + newVersion + ",oldVersion:" + oldVersion);
            getDBConfig().beforeUpdateDBStructure(db, oldVersion, newVersion);
            //检查更新程序需要的表结构
            checkAndUpdateDBTableStructure(db);
            // 检查更新维护数据的表结构
            getDBConfig().afterUpdateDBStructure(db, oldVersion, newVersion);
        } catch (Exception e) {
            Log.e("DBHelper", e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }


    private boolean isCalledOpen = false;

    private void onOpen(Database database) {
        if (database.isReadOnly()) return;
        if (!isCalledOpen) {
            isCalledOpen = true;
            getDBConfig().onOpen(database);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        onOpen(new Database(db));
    }

    public void onCreateHelper(Database db) {
        if (getDBConfig().getUpdateTableMethod() == UpdateTableMethod.MANUAL) {
            return;
        }
    }

    @SuppressWarnings("unchecked")
    public void checkAndUpdateDBTableStructure(Database db) {
        List<SQLiteTable> tables = DBInterface.readAll(db, SQLiteTable.class);
        Long st = System.currentTimeMillis();
        for (SQLiteTable oldTable : tables) {
            Class<? extends Dto> tClass;
            try {
                tClass = (Class<? extends Dto>) Class.forName(oldTable.getClassName());
            } catch (ClassNotFoundException e) {
                Log.e("DBHelper", "class[" + oldTable.getClassName() + "] not found", e);
                continue;
            }
            SQLiteTable newTable = (SQLiteTable) DBInterface.getSQLiteTable(tClass);
            if (null == newTable) {
                Log.e("DBHelper", "newTable class[" + tClass.getName() + "] is not assignable from Dto");
                continue;
            }
            boolean needUpdate = oldTable.needUpdate(getDBConfig(), tClass, newTable);
            Log.e("DBHelper", "NeedUpdate:" + needUpdate + ",Table:" + newTable.getClassName());
            if (needUpdate) {
                printSQL("Update Dto[" + newTable.getClassName() + "]...");
                long startTime = System.currentTimeMillis();
                newTable.parseFields(DBInterface.getColumnFields(tClass));
                newTable.getSigner();
                updateTableStructure(db, tClass, oldTable, newTable);
                Integer tid = updateSQLiteTable(db, oldTable, newTable);
                newTable.saveOrUpdateColumnList(db, tid);
                printSQL("Update Dto[" + newTable.getClassName() + "] successfully,speed time[" + (System.currentTimeMillis() - startTime) + "].");
            }
        }
        printSQL("check speed time is :" + (System.currentTimeMillis() - st));
    }

    private Integer updateSQLiteTable(Database db, SQLiteTable oldTable, SQLiteTable newTable) {
        SQLiteTable where = Dto.createNullFieldDto(SQLiteTable.class);
        Integer tid = Integer.valueOf(oldTable.getId());
        where.setId(tid);
        Integer nullValue = null;
        newTable.setId(nullValue);
        DBInterface.updateByWhere(db, SQLiteTable.class, newTable, where);
        return tid;
    }


    private void updateTableStructure(Database db, Class<? extends Dto> clz,
                                      SQLiteTable oldTable, SQLiteTable newTable) {
        List<SQLiteTableColumn> newColumns = newTable.getList();
        List<SQLiteTableColumn> oldColumns = oldTable.readColumnList(db);

        List<SQLiteTableColumn> addList = new ArrayList<SQLiteTableColumn>();
        List<SQLiteTableColumn> modifyList = new ArrayList<SQLiteTableColumn>();
        for (SQLiteTableColumn newColumn : newColumns) {
            SQLiteTableColumn column = oldTable.getColumn(newColumn.getCid());
            if (null == column) {
                addList.add(newColumn);
            } else if (!column.equals(newColumn)) {
                modifyList.add(newColumn);
            }
        }
        List<SQLiteTableColumn> deleteList = new ArrayList<>();
        for (SQLiteTableColumn oldColumn : oldColumns) {
            SQLiteTableColumn column = newTable.getColumn(oldColumn.getCid());
            if (null == column) {
                deleteList.add(oldColumn);
            }
        }
        String tableName = DBInterface.getTableName(clz);
        if (!addList.isEmpty()) {
            if (addColumn(db, tableName, oldTable, addList)) {
                checkAndClearOnAddColumn(db, tableName, newTable);
            }
        }
        if (!modifyList.isEmpty()) {
            if (modifyColumn(db, tableName, oldTable, modifyList)) {
                checkAndClearOnAddColumn(db, tableName, newTable);
            }
        }
        if (!deleteList.isEmpty()) {
            deleteColumn(db, tableName, oldTable, deleteList);
        }
    }

    private void deleteColumn(Database db, String tableName, SQLiteTable table, List<SQLiteTableColumn> columns) {
        if (!columns.isEmpty()) {
            printSQL("deleteColumn [" + joinSQLiteTableColumnName(",", columns) + "].");
            table.removeColumn(columns);
            String createTableSQL = table.makeCreateSQLFromColumns(tableName, null);
            String allNewColumns = table.joinAllColumnName(",");
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
            DBInterface.execSQL(db, createTableSQL);
            sql = "INSERT INTO " + tableName + "(" + allNewColumns + ") SELECT " + allNewColumns + " FROM " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
            sql = "DROP TABLE " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
        }
    }

    private boolean modifyColumn(Database db, String tableName, SQLiteTable table, List<SQLiteTableColumn> columns) {
        if (!columns.isEmpty()) {
            printSQL("modifyColumn [" + joinSQLiteTableColumnName(",", columns) + "]");
            String allOldColumns = table.joinAllColumnName(",");
            table.modifyColumn(columns);
            String allNewColumns = table.joinAllColumnName(",");
            String createTableSQL = table.makeCreateSQLFromColumns(tableName, null);
            String sql = "ALTER TABLE " + tableName + " RENAME TO " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
            DBInterface.execSQL(db, createTableSQL);
            sql = "INSERT INTO " + tableName + "(" + allNewColumns + ") SELECT " + allOldColumns + " FROM " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
            sql = "DROP TABLE " + tableName + "_old;";
            DBInterface.execSQL(db, sql);
            return true;
        }
        return false;
    }

    private String joinSQLiteTableColumnName(String delimiter, List<SQLiteTableColumn> columns) {
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (SQLiteTableColumn column : columns) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(column.getName());
        }
        return sb.toString();
    }

    private boolean addColumn(Database db, String tableName, SQLiteTable table, List<SQLiteTableColumn> columns) {
        if (!columns.isEmpty()) {
            printSQL("addColumn [" + joinSQLiteTableColumnName(",", columns) + "].");
            for (SQLiteTableColumn column : columns) {
                String sql = column.makeAddColumnSQL(tableName);
                DBInterface.execSQL(db, sql);
                if (null != table) {
                    table.addColumn(column);
                }
            }
            return true;
        }
        return false;
    }

    private boolean checkAndClearOnAddColumn(Database db, String tableName, SQLiteTable table) {
        if (table.isClearOnAddColumn()) {
            int result = DBInterface.deleteAll(db, tableName);
            return result >= 0;
        }
        return false;
    }

    private void printSQL(String sql) {
        if (getDBConfig().isPrintSQL()) {
            Log.i(getDBConfig().getDBFile(), sql);
        }
    }
}