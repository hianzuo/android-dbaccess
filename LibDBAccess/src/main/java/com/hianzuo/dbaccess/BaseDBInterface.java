package com.hianzuo.dbaccess;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.os.Build;

import com.flyhand.core.app.AbstractCoreApplication;
import com.hianzuo.dbaccess.config.DBHelper;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;
import com.hianzuo.dbaccess.lang.DbList;
import com.hianzuo.dbaccess.sql.SQLiteDeleteSQLHandler;
import com.hianzuo.dbaccess.sql.builder.SQLBuilder;
import com.hianzuo.dbaccess.sql.builder.WhereBuilder;
import com.hianzuo.dbaccess.store.DBTable;
import com.hianzuo.dbaccess.store.SQLiteTable;
import com.hianzuo.dbaccess.store.SQLiteTableColumn;
import com.hianzuo.dbaccess.throwable.DBDataException;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;
import com.hianzuo.dbaccess.throwable.DBSqlException;
import com.hianzuo.dbaccess.util.ContentValues2xUtil;
import com.hianzuo.dbaccess.util.CursorUtils;
import com.hianzuo.dbaccess.util.SQLMapResult;
import com.hianzuo.dbaccess.util.StringUtil;
import com.google.gson.Gson;
import com.hianzuo.logger.Log;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-12-30
 * Time: Afternoon 5:04
 */
class BaseDBInterface {
    private static final Locker mLock = new Locker();// 锁对象
    private static DBHelper mDBHelper = null;
    private final static Map<Class<? extends Dto>, List<Field>>
            mCacheTableColumnFields = new ConcurrentHashMap<Class<? extends Dto>, List<Field>>();
    private final static HashMap<Class<? extends Dto>, String>
            mCacheTableNames = new HashMap<>();
    public static final Map<String, DBTable> mCacheSQLiteTable = new ConcurrentHashMap<>();


    private synchronized static DBHelper getDatabaseHelper() {
        if (null == BaseDBInterface.mDBHelper) {
            BaseDBInterface.mDBHelper = new DBHelper();
            Database database = openWritableDatabase(BaseDBInterface.mDBHelper);
            BaseDBInterface.mDBHelper.onCreateHelper(database);
        }
        return BaseDBInterface.mDBHelper;
    }

    public static synchronized Database openWritableDatabase() {
        DBHelper helper = getDatabaseHelper();
        return openDatabase(helper, true);
    }

    public static synchronized Database openReadableDatabase() {
        DBHelper helper = getDatabaseHelper();
        return openDatabase(helper, false);
    }

    public static synchronized Database openWritableDatabase(DBHelper helper) {
        return openDatabase(helper, true);
    }

    private static Database openDatabase(DBHelper helper, boolean canWrite) {
        try {
            return new Database(canWrite ? helper.getWritableDatabase() : helper.getReadableDatabase());
        } catch (SQLiteException ex) {
            String msg = ex.getMessage();
            helper.close();
            BaseDBInterface.mDBHelper = null;
            if (null != msg && msg.contains("downgrade database from version")) {
                Integer dbVersion = Integer.valueOf(msg.replaceAll("^.*?from version(.*?)to.*$", "$1").trim());
                BaseDBInterface.mDBHelper = new DBHelper(dbVersion);
                return new Database(canWrite ? getDatabaseHelper().getWritableDatabase() : getDatabaseHelper().getReadableDatabase());
            } else {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    public static void forceClose() {
        try {
            if (null != mDBHelper) {
                mDBHelper.forceClose();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            BaseDBInterface.mDBHelper = null;
        }
    }

    /**
     * SQLiteConnectionPool The connection pool for database has been unable to grant a connection to thread
     * SQLiteConnectionPool(14004): Connections: 0 active, 1 idle, 0 available.
     */
    public static void lock() {
        mLock.lock();
    }

    /**
     * SQLiteConnectionPool The connection pool for database has been unable to grant a connection to thread
     * SQLiteConnectionPool(14004): Connections: 0 active, 1 idle, 0 available.
     */
    public static void unlock() {
        mLock.unlock();
    }

    public static boolean isTableExist(Class<? extends Dto> clz) {
        return isTableExist(getTableName(clz));
    }

    public static boolean isTableExist(Database db, Class<? extends Dto> clz) {
        return isTableExist(db, getTableName(clz));
    }

    private static final Map<String, Boolean> mCacheTableExist = new ConcurrentHashMap<String, Boolean>();

    public static boolean isTableExist(String tableName) {
        return isTableExist(null, tableName);
    }

    public static void markTableNotExist(String tableName) {
        synchronized (mCacheTableExist) {
            mCacheTableExist.remove(tableName);
        }
    }

    public static void clearCacheTableExist() {
        mCacheTableExist.clear();
    }

    public static void deleteAllTable() {
        Database db = DBInterface.openWritableDatabase();
        Cursor c = null;
        db.beginTransaction();
        try {
            c = rawQuery(db, "SELECT name FROM sqlite_master WHERE type='table'");
            while (c.moveToNext()) {
                String s = c.getString(0);
                if ("sqlite_sequence".equals(s) || "android_metadata".equals(s)) {
                    continue;
                } else {
                    DBInterface.deleteTableIfExist(db, s);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            if (null != c) {
                c.close();
            }
        }

        DBInterface.clearCacheTableExist();
        DBInterface.clearCacheSQLiteTable();
        DBInterface.clearCacheTableColumnFields();
    }

    public static boolean isTableExist(Database db, String tableName) {
        Boolean result = mCacheTableExist.get(tableName);
        if (null != result && result) {
            return true;
        } else {
            Cursor cursor = null;
            try {
                cursor = rawQuery(db, "PRAGMA table_info('" + tableName + "')");
                if (cursor.moveToFirst()) {
                    Log.e("BaseDBInterface", AbstractCoreApplication.progressName() + " TABLE[" + tableName + "] existed.");
                    mCacheTableExist.put(tableName, true);
                    return true;
                } else {
                    Log.e("BaseDBInterface", AbstractCoreApplication.progressName() + " TABLE[" + tableName + "] not existed.");
                    return false;
                }
            } finally {
                CursorUtils.close(cursor);
            }
        }
    }

    public static <T extends Dto> void checkAndCreateTable(T dto) {
        checkAndCreateTable(dto.getClass());
    }

    public static void checkAndCreateTable(Class<? extends Dto> tClass) {
        String tableName = getTableName(tClass);
        if (!isTableExist(tableName)) {
            createTable(tClass);
        }
    }

    public static void checkAndCreateTable(Database db, Dto dto) {
        checkAndCreateTable(db, dto.getClass());
    }

    public static <T extends Dto> void checkAndCreateTable(Database db, Class<T> clazz) {
        String tableName = getTableName(clazz);
        if (!isTableExist(db, tableName)) {
            createTable(db, clazz);
        }
    }


    public static void createTable(Class<? extends Dto> tClass) {
        createTable(null, tClass);
    }

    public static void createTable(Database db, Class<? extends Dto> tClass) {
        boolean inTransaction = false;
        if (null == db) {
            inTransaction = true;
            db = openWritableDatabase();
        }
        String tableName = "";
        if (inTransaction) {
            db.beginTransaction();
        }
        try {
            tableName = getTableName(tClass);
            DBTable table = getSQLiteTable(tClass);
            if (null == table) {
                throw new DBRuntimeException("class[" + tClass.getName() + "] is not assignable from Dto");
            }
            List<Field> fields = getColumnFields(tClass);
            execSQL(db, table.makeCreateSQLFromColumns(tableName, fields));
            if (!(SQLiteTable.class.equals(tClass) ||
                    SQLiteTableColumn.class.isAssignableFrom(tClass))) {
                int id;
                List<SQLiteTable> list = DBInterface.readNeedByWhere(db, SQLiteTable.class,
                        "id", "className=?", table.getClassName());
                if (list.size() > 0) {
                    id = Integer.valueOf(list.get(0).getId());
                    table.setId(id);
                    table.update(db);
                } else {
                    //防止异常(不是好的解决办法，应找到错误原因) id is not null ,can not save.
                    table.nullId();
                    //-------------------------------------
                    table.getSigner();
                    table.save(db);
                    id = Integer.valueOf(table.getId());
                }
                table.saveOrUpdateColumnList(db, id);
            }
            if (inTransaction) {
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            if (e instanceof SQLiteException) {
                if (null != e.getMessage() && e.getMessage().contains("already exists")) {
                    return;
                }
            }
            throw new DBRuntimeException("can not create table[" + tableName + "]", e);
        } finally {
            if (inTransaction) {
                db.endTransaction();
            }
        }
    }

    public static void clearCacheSQLiteTable() {
        mCacheSQLiteTable.clear();
    }

    public static DBTable getSQLiteTable(Class<? extends Dto> tClass) {
        String className = tClass.getName();
        DBTable table = mCacheSQLiteTable.get(className);
        if (null == table) {
            if (Dto.class.isAssignableFrom(tClass)) {
                table = DBHelper.getDBConfig().newDBTable();
                Table aTable = tClass.getAnnotation(Table.class);
                table = table.create(className, aTable.ver(), aTable.clearOnAddColumn());
                mCacheSQLiteTable.put(className, table);
            }
        }
        return table;
    }


    public static String getTableName(Class<? extends Dto> clz) {
        String tableName = mCacheTableNames.get(clz);
        if (null == tableName) {
            try {
                Table injectTable = clz.getAnnotation(Table.class);
                tableName = injectTable.name();
                if (tableName.trim().length() == 0) {
                    tableName = clz.getSimpleName();
                }
                mCacheTableNames.put(clz, tableName);
                return tableName;
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            return tableName;
        }
    }

    public static Long readLong(String sql, String... params) {
        return readLong(null, sql, params);
    }

    public static Long readLong(Database db, String sql, String... params) {
        Cursor c = null;
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            Long ret = 0L;
            if (c.moveToNext()) {
                ret = c.getLong(0);
            }
            return ret;
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static Integer readInteger(String sql, String... params) {
        return readInteger(null, sql, params);
    }

    public static Integer readInteger(Database db, WhereBuilder builder) {
        return readInteger(db, builder.sql(), builder.params());
    }

    public static Integer readInteger(Database db, String sql, String... params) {
        Cursor c = null;
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            Integer ret = 0;
            if (c.moveToNext()) {
                ret = c.getInt(0);
            }
            return ret;
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static Double readDouble(Database db, String sql, String... params) {
        Cursor c = null;
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            Double ret = 0D;
            if (c.moveToNext()) {
                ret = c.getDouble(0);
            }
            return ret;
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static String readString(Database db, WhereBuilder builder) {
        return readString(db, builder.sql(), builder.params());
    }

    public static String readString(Database db, String sql, String... params) {
        Cursor c = null;
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            Object ret = null;
            if (c.moveToNext()) {

                ret = c.getString(0);
                if (null == ret) {
                    ret = c.getInt(0);
                }
            }
            return null != ret ? ret + "" : null;
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static List<String> readListString(String sql, String... params) {
        return readListString(null, sql, params);
    }

    public static List<String> readListString(Database db, String sql, String... params) {
        Cursor c = null;
        List<String> list = new ArrayList<>();
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            while (c.moveToNext()) {
                String val = CursorUtils.getString(c, 0);
                if (null != val) {
                    list.add(val);
                }
            }
            return list;
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static SQLMapResult readMapResult(String sql, String... params) {
        return readMapResult(null, sql, params);
    }

    public static SQLMapResult readMapResult(Database db, String sql, String... params) {
        Cursor c = null;
        try {
            if (null == db) {
                db = openReadableDatabase();
            }
            c = rawQuery(db, sql, params);
            Map<String, Object> data = new HashMap<>();
            String[] names = c.getColumnNames();
            for (String name : names) {
                data.put(name, CursorUtils.get(c, c.getColumnIndex(name)));
            }
            return new SQLMapResult(data);
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        } finally {
            CursorUtils.close(c);
        }
    }

    public static String readString(String sql, String... params) {
        return readString(null, sql, params);
    }

    public static void execSQL(String sql, Object... params) {
        execSQL(null, sql, params);
    }


    public static int execSQL(Database db, String sql, String... params) {
        Object[] os = new Object[params.length];
        System.arraycopy(params, 0, os, 0, params.length);
        return execSQL(db, sql, os);
    }

    public static int execSQL(Database db, String sql, Object... params) {
        if (null == db) {
            db = openWritableDatabase();
        }
        try {
            SQLiteStatement statement = db.compileStatement(sql);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (null == param) {
                    statement.bindNull(i + 1);
                } else if (param instanceof String) {
                    statement.bindString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    statement.bindDouble(i + 1, (Double) param);
                } else if (param instanceof Long) {
                    statement.bindLong(i + 1, (Long) param);
                } else if (param instanceof Integer) {
                    statement.bindLong(i + 1, (Integer) param);
                } else if (param instanceof Enum) {
                    statement.bindString(i + 1, ((Enum) param).name());
                } else if (param instanceof BigDecimal) {
                    statement.bindString(i + 1, param.toString());
                } else if (param instanceof BigInteger) {
                    statement.bindLong(i + 1, ((BigInteger) param).longValue());
                } else if (param instanceof Float) {
                    statement.bindDouble(i + 1, (Float) param);
                } else if (param instanceof Boolean) {
                    statement.bindString(i + 1, String.valueOf(param));
                } else {
                    throw new RuntimeException("un support bind type " + param.getClass().getName());
                }
            }
            if (Build.VERSION.SDK_INT > 10) {
                return statement.executeUpdateDelete();
            } else {
                return (int) statement.executeInsert();
            }
        } catch (Exception e) {
            throw new DBSqlException(sql, e, params);
        }
    }

    public static Cursor rawQuery(SQLBuilder builder) {
        return rawQuery(null, builder.sql(), builder.params());
    }

    public static Cursor rawQuery(Database db, SQLBuilder builder) {
        return rawQuery(db, builder.sql(), builder.params());
    }

    public static Cursor rawQuery(String sql, String... params) {
        return rawQuery(null, sql, params);
    }

    public static Cursor rawQuery(Database db, String sql, String... params) {
        return rawQuery(db, sql, 1, params);
    }

    private static Cursor rawQuery(Database db, String sql, int tryCount, String... params) {
        if (null == db) {
            db = openReadableDatabase();
        }
        long currentTime = System.currentTimeMillis();
        try {
            return db.rawQuery(sql, params);
        } catch (Exception ex) {
            String eMsg = ex.getMessage();
            if (null != eMsg && eMsg.contains("has no column named")) {
                getDatabaseHelper().checkAndUpdateDBTableStructure(db);
                if (tryCount > 0) {
                    return rawQuery(db, sql, tryCount - 1, params);
                }
            }
            throw ex;
        } finally {
            long speedTime = System.currentTimeMillis() - currentTime;
            if (speedTime > 3000) {
                Log.w("Slowly SQL", "Speed time[" + speedTime + "]:" + sql + " ; params:" + StringUtil.join(params, ","));
            }
        }
    }

    protected static int update(Database db, String table, ContentValues values,
                                String whereClause, String... whereArgs) {
        return update(db, table, values, whereClause, 1, whereArgs);
    }

    private static int update(Database db, String table, ContentValues values,
                              String whereClause, int tryCount, String... whereArgs) {
        if (null == db) {
            db = openWritableDatabase();
        }
        try {
            return db.update(table, values, whereClause, whereArgs);
        } catch (RuntimeException ex) {
            String eMsg = ex.getMessage();
            if (null != eMsg && eMsg.contains("has no column named")) {
                getDatabaseHelper().checkAndUpdateDBTableStructure(db);
                if (tryCount > 0) {
                    return update(db, table, values, whereClause, tryCount - 1, whereArgs);
                }
            }
            throw ex;
        }
    }

    protected static void checkErrorAndHandleBase(Database db, RuntimeException e, Class<? extends Dto> tClass) {
        if (e.getMessage().contains("no such table")) {
            dropTable(db, tClass);
        }
    }

    protected static int delete(Database db, String table,
                                String whereClause, String... whereArgs) {
        if (null == db) {
            db = openWritableDatabase();
        }
        try {
            return db.delete(table, whereClause, whereArgs);
        } catch (Exception e) {
            String sql = SQLiteDeleteSQLHandler.create(table, whereClause);
            throw new DBSqlException(sql, e, whereArgs);
        }
    }


    protected static long insert(Database db, String table, String nullColumnHack, ContentValues values) {
        return insert(db, table, nullColumnHack, values, 1);
    }

    private static long insert(Database db, String table, String nullColumnHack, ContentValues values, int tryCount) {
        if (null == db) {
            db = openWritableDatabase();
        }
        try {
            return db.insert(table, nullColumnHack, values);
        } catch (Exception ex) {
            String eMsg = ex.getMessage();
            if (null != eMsg && eMsg.contains("has no column named")) {
                getDatabaseHelper().checkAndUpdateDBTableStructure(db);
                if (tryCount > 0) {
                    return insert(db, table, nullColumnHack, values, tryCount - 1);
                }
            }
            throw ex;
        }
    }

    public static void clearCacheTableColumnFields() {
        mCacheTableColumnFields.clear();
    }

    public static synchronized List<Field> getColumnFields(Class<? extends Dto> clz) {
        List<Field> list = mCacheTableColumnFields.get(clz);
        if (null != list) {
            return list;
        } else {
            LinkedList<Field> fields = new LinkedList<Field>();
            getAllClassFields(fields, clz);
            mCacheTableColumnFields.put(clz, fields);
            return fields;
        }
    }

    private static void getAllClassFields(LinkedList<Field> allFields, Class<?> clazz) {
        if (Dto.class.isAssignableFrom(clazz) || clazz.equals(Dto.class)) {
            Field[] declared = clazz.getDeclaredFields();
            for (int i = 0; i < declared.length; i++) {
                Field field = declared[declared.length - i - 1];
                if (field.isAnnotationPresent(Column.class)) {
                    if (!containsDtoFields(allFields, field)) {
                        allFields.push(field);
                    }
                }
            }
            Class<?> supperClass = clazz.getSuperclass();
            if (null == supperClass || Object.class.equals(supperClass)) {
                //最顶层了
            } else {
                getAllClassFields(allFields, supperClass);
            }
        }
    }

    private static boolean containsDtoFields(LinkedList<Field> allFields, Field field) {
        for (Field allField : allFields) {
            if (field.getName().equals(allField.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 插入一条数据到数据库中
     *
     * @param dto 需要插入到数据库中的数据
     * @param <T> 需要插入的数据类型
     * @return 插入成功返回大于0, 否则返回式－1
     */
    public static <T extends Dto> int insert(Database db, T dto) {
        try {
            if (null != dto.getIdInteger()) {
                throw new DBDataException("insert dto id is not null", dto);
            }
            checkAndCreateTable(db, dto);
            String tableName = getTableName(dto.getClass());
            ContentValues cvs = getContentValues(dto);
            long id = insert(db, tableName, null, cvs);
            if (-1 != id) {
                dto.setId(String.valueOf(id));
                return 1;
            } else {
                return -1;
            }
        } catch (Exception e) {
            throw new DBDataException(dto, e);
        }
    }

    /**
     * 插入一条数据到数据库中
     *
     * @param dto 需要插入到数据库中的数据
     * @param <T> 需要插入的数据类型
     * @return 插入成功返回大于0, 否则返回式－1
     */
    public static <T extends Dto> int insert(T dto) {
        return insert(null, dto);
    }

    /**
     * 把List数据保存到数据库中
     *
     * @param list 需要保存到表的数据
     * @param <T>  需要保存的类型
     */
    public static <T extends Dto> List<T> insertList(Database db, List<T> list) {
        return insertList(db, list, true);
    }

    /**
     * 把List数据保存到数据库中
     *
     * @param list 需要保存到表的数据
     * @param <T>  需要保存的类型
     */
    public static <T extends Dto> List<T> insertList(Database db, List<T> list, Boolean inTransaction) {
        List<T> retList = new ArrayList<T>();
        if (null != list && list.size() > 0) {
            Class<? extends Dto> tClass = list.get(0).getClass();
            checkAndCreateTable(db, tClass);
            String tableName = getTableName(tClass);
            if (null == db) {
                db = openWritableDatabase();
            }
            if (inTransaction) {
                db.beginTransaction();
            }
            try {
                String sql = null;
                int sqlWSize = 0;
                Object[] bindArgs = null;
                for (T dto : list) {
                    try {
                        ContentValues initialValues = getContentValues(dto);
                        bindArgs = getBindArgs(initialValues);
                        if (sql == null || sqlWSize != bindArgs.length) {
                            sql = createInsertSQL(initialValues, tableName);
                            sqlWSize = StringUtil.countMatches(sql, "?");
                        }
                        execSQL(db, sql, bindArgs);
                        retList.add(dto);
                    } catch (Exception e) {
                        throw new DBSqlException(sql, e, bindArgs);
                    }
                }
                if (inTransaction) {
                    db.setTransactionSuccessful();
                }
            } finally {
                if (inTransaction) {
                    db.endTransaction();
                }
            }
        }
        return retList;
    }

    public static <T extends Dto> List<T> insertList(List<T> list) {
        return insertList(null, list);
    }

    public static <T extends Dto> int saveOrUpdate(final T t) {
        return saveOrUpdate(null, t);
    }

    public static <T extends Dto> int saveOrUpdate(Database db, final T t) {
        ArrayList<T> list = new ArrayList<T>();
        list.add(t);
        int id = saveOrUpdateList(db, list);
        t.setId(id);
        return id;
    }

    public static <T extends Dto> int saveOrUpdateList(List<T> list) {
        return saveOrUpdateList(null, list);
    }

    public static <T extends Dto> int saveOrUpdateList(Database db, List<T> list) {
        return saveOrUpdateList(db, list, 1);
    }

    private static <T extends Dto> int saveOrUpdateList(Database db, List<T> list, int tryCount) {
        int ret = 0;
        if (null != list && list.size() > 0) {
            Class<? extends Dto> dtoClass = list.get(0).getClass();
            checkAndCreateTable(db, dtoClass);
            String tableName = getTableName(dtoClass);
            boolean inTransaction = false;
            if (null == db) {
                inTransaction = true;
                db = openWritableDatabase();
            }
            if (inTransaction) {
                db.beginTransaction();
            }
            try {
                int i = 0;
                for (T dto : list) {
                    ContentValues initialValues = getContentValues(dto);
                    long id;
                    try {
                        id = db.insertWithOnConflict(tableName, null,
                                initialValues, SQLiteDatabase.CONFLICT_REPLACE);
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                        String eMsg = e.getMessage();
                        eMsg = null == eMsg ? "" : eMsg;
                        if (eMsg.contains("no such table")) {
                            id = insertWithOnConflictOnTableNotExist(db, dto.getClass(), tableName, initialValues);
                        } else if (eMsg.contains("has no column named")) {
                            getDatabaseHelper().checkAndUpdateDBTableStructure(db);
                            if (tryCount > 0) {
                                return saveOrUpdateList(db, list, tryCount - 1);
                            } else {
                                throw new DBDataException("saveOrUpdateList dto exception", e, dto);
                            }
                        } else {
                            throw new DBDataException("saveOrUpdateList dto exception", e, dto);
                        }
                    }
                    if (list.size() == 1) {
                        ret = (int) id;
                    } else {
                        ret += 1;
                    }
                    if (DBHelper.getDBConfig().isPrintSQL()) {
                        String sql = insertWithOnConflictSQL(tableName, null,
                                initialValues, SQLiteDatabase.CONFLICT_REPLACE);
                        String dbName = DBHelper.getDBConfig().getDBFile();
                        Log.i(dbName, sql);
                    }
                }
                if (inTransaction) {
                    db.setTransactionSuccessful();
                }
            } finally {
                if (inTransaction) {
                    db.endTransaction();
                }
            }
        }
        return ret;
    }

    private static long insertWithOnConflictOnTableNotExist(Database db, Class<? extends Dto> dtoClass, String tableName, ContentValues initialValues) {
        markTableNotExist(tableName);
        checkAndCreateTable(db, dtoClass);
        return db.insertWithOnConflict(tableName, null,
                initialValues, SQLiteDatabase.CONFLICT_REPLACE);
    }

    protected static String createUpdateSQL(ContentValues values, String tableName) {
        return createUpdateSQL(values, tableName, "id=?");
    }

    protected static String createUpdateSQL(ContentValues values, String tableName, String where) {
        StringBuilder sql = new StringBuilder(120);
        sql.append("UPDATE ");
        sql.append(tableName);
        sql.append(" SET ");
        int i = 0;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            sql.append((i > 0) ? "," : "");
            sql.append(entry.getKey());
            i++;
            sql.append("=?");
        }
        sql.append(" WHERE ").append(where);
        return sql.toString();
    }


    /**
     * 获取需要保存的参数
     *
     * @param cvs ContentValues
     * @return 保存的参数
     */
    public static Object[] getUpdateBindArgs(ContentValues cvs, String id) {
        synchronized (mLock) {
            Object[] bindArgs = null;
            int size = (cvs != null && cvs.size() > 0) ? cvs.size() : 0;
            if (size > 0) {
                bindArgs = new Object[size + 1];
                int i = 0;
                for (Map.Entry<String, Object> entity : cvs.valueSet()) {
                    bindArgs[i++] = entity.getValue();
                }
                bindArgs[size] = id;
            }
            return bindArgs;
        }
    }


    public static <T extends Dto> ContentValues getContentValues(T dto) {
        return getContentValues(dto, true);
    }

    protected static final Gson gson = new Gson();

    public static <T extends Dto> ContentValues getContentValues(T dto, Boolean includeNullValue) {
        List<Field> fields = getColumnFields(dto.getClass());
        ContentValues cvs = new ContentValues();
        for (Field field : fields) {
            if (field.isAnnotationPresent(Column.class)) {
                try {
                    Column column = field.getAnnotation(Column.class);
                    field.setAccessible(true);
                    Object value = field.get(dto);
                    if (!includeNullValue && null == value) {
                        continue;
                    }
                    String columnName = column.name();
                    if (isEmpty(columnName)) {
                        columnName = field.getName();
                    }
                    Class<?> type = field.getType();
                    if (Integer.class.equals(type) || "int".equals(type.getName())) {
                        if ("id".equals(columnName)) {
                            Integer valInt = (Integer) value;
                            if (null != valInt) {
                                cvs.put(columnName, (Integer) value);
                            }
                        } else {
                            cvs.put(columnName, (Integer) value);
                        }
                    } else if (String.class.equals(type)) {
                        cvs.put(columnName, (String) value);
                    } else if (Long.class.equals(type) || "long".equals(type.getName())) {
                        cvs.put(columnName, (Long) value);
                    } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
                        String val = null;
                        if (null != value) {
                            val = value.toString();
                        }
                        cvs.put(columnName, val);
                    } else if (Double.class.equals(type) || "double".equals(type.getName())) {
                        cvs.put(columnName, (Double) value);
                    } else if (Float.class.equals(type) || "float".equals(type.getName())) {
                        cvs.put(columnName, (Float) value);
                    } else if (BigDecimal.class.equals(type)) {
                        String val = null;
                        if (null != value) {
                            val = value.toString();
                        }
                        cvs.put(columnName, val);
                    } else if (BigInteger.class.equals(type)) {
                        String val = null;
                        if (null != value) {
                            val = value.toString();
                        }
                        cvs.put(columnName, val);
                    } else if (Enum.class.isAssignableFrom(type)) {
                        Enum e = null;
                        if (null != value) {
                            e = (Enum) value;
                        }
                        String columnValue = null;
                        if (null != e) {
                            columnValue = e.name();
                        }
                        cvs.put(columnName, columnValue);
                    } else if (DbList.class.isAssignableFrom(type)) {
                        String columnValue = null;
                        if (null != value) {
                            columnValue = ((DbList) value).getDataStr();
                        }
                        cvs.put(columnName, columnValue);
                    } else if (List.class.isAssignableFrom(type)) {
                        if (null != value) {
                            cvs.put(columnName, gson.toJson(value));
                        }
                    } else {
                        throw new RuntimeException("can't support for type " + type.getName());
                    }
                } catch (IllegalAccessException e) {
                    throw new DBRuntimeException(e);
                }
            }
        }
        return cvs;
    }

    private static boolean isEmpty(String str) {
        return null == str || str.trim().length() == 0;
    }

    /**
     * 获取需要保存的参数
     *
     * @param cvsList ContentValues
     * @return 保存的参数
     */
    public static Object[] getBindArgs(ContentValues... cvsList) {
        List<Object> bindArgs = new ArrayList<Object>();
        for (ContentValues cvs : cvsList) {
            int size = (cvs != null && cvs.size() > 0) ? cvs.size() : 0;
            if (size > 0) {
                for (Map.Entry<String, Object> entity : cvs.valueSet()) {
                    bindArgs.add(entity.getValue());
                }
            }
        }
        return bindArgs.toArray(new Object[bindArgs.size()]);
    }

    /**
     * 创建一条插入SQL语句
     *
     * @param cvs       ContentValues
     * @param tableName 表名
     * @return 保存的参数
     */
    public static String createInsertSQL(ContentValues cvs, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(" INTO ");
        sql.append(tableName);
        sql.append('(');

        int size = (cvs != null && cvs.size() > 0) ? cvs.size() : 0;
        if (size > 0) {
            int i = 0;
            for (Map.Entry<String, Object> entry : cvs.valueSet()) {
                sql.append((i > 0) ? "," : "");
                sql.append(entry.getKey());
                i++;
            }
            sql.append(')');
            sql.append(" VALUES (");
            for (i = 0; i < size; i++) {
                sql.append((i > 0) ? ",?" : "?");
            }
        } else {
            sql.append(") VALUES (NULL");
        }
        sql.append(')');
        return sql.toString();
    }

    public static String insertWithOnConflictSQL(String table, String nullColumnHack,
                                                 ContentValues initialValues, int conflictAlgorithm) {
        String[] CONFLICT_VALUES = new String[]
                {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT");
        sql.append(CONFLICT_VALUES[conflictAlgorithm]);
        sql.append(" INTO ");
        sql.append(table);
        sql.append('(');
        Object[] bindArgs;
        int size = (initialValues != null && initialValues.size() > 0) ? initialValues.size() : 0;
        if (size > 0) {
            bindArgs = new Object[size];
            int i = 0;
            for (String colName : ContentValues2xUtil.keySet(initialValues)) {
                sql.append((i > 0) ? "," : "");
                sql.append(colName);
                bindArgs[i++] = initialValues.get(colName);
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

    public static void dropTable(Database db, Class<? extends Dto> clazz) {
        if (null == db) {
            db = openWritableDatabase();
        }
        String tid = DBInterface.readString(db, "select id from app_table_list where className=?", clazz.getName());
        if (null != tid) {
            DBInterface.deleteByWhere(db, SQLiteTableColumn.class, "tid=?", tid);
        }
        DBInterface.deleteByWhere(db, SQLiteTable.class, "id=?", tid);
        String tableName = DBInterface.getTableName(clazz);
        if (StringUtil.isNotEmpty(tableName)) {
            db.execSQL("DROP TABLE IF EXISTS " + tableName + ";");
        }
        mCacheTableNames.remove(clazz);
        markTableNotExist(tableName);
    }

    public static int renameTableClazz(Database db, Class<? extends Dto> newClazz, String oldClassName) {
        return DBInterface.updateByWhere(db, SQLiteTable.class,
                "className=?", "className=?",
                newClazz.getName(), oldClassName);
    }

    private static class Locker {
        public void lock() {

        }

        public void unlock() {

        }
    }
}

