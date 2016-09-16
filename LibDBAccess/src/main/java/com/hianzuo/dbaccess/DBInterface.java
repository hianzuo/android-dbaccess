package com.hianzuo.dbaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import com.hianzuo.dbaccess.config.DBHelper;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.FromDB;
import com.hianzuo.dbaccess.sql.builder.WhereBuilder;
import com.hianzuo.dbaccess.throwable.DBDataException;
import com.hianzuo.dbaccess.throwable.DBRuntimeException;
import com.hianzuo.dbaccess.util.ClassFieldUtil;
import com.hianzuo.dbaccess.util.CursorUtils;
import com.hianzuo.dbaccess.util.StringUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Ryan
 * User: Ryan
 * Date: 11-12-30
 * Time: 下午3:50
 */
public class DBInterface extends BaseDBInterface {

    public static int updateByWhere(Dto update, String where, String... params) {
        return updateByWhere(null, update, where, params);
    }

    public static int updateByWhere(Database db, Dto update, String where, String... params) {
        ContentValues updateCVS = getContentValues(update, false);
        String tableName = getTableName(update.getClass());
        try {
            return update(db, tableName, updateCVS, where, params);
        } catch (Exception e) {
            throw new DBDataException(update, e);
        }
    }

    public static int updateByWhere(Class<? extends Dto> tClass, Dto update, Dto where) {
        return updateByWhere(null, tClass, update, where);
    }

    public static int updateByWhere(Class<? extends Dto> tClass, String update, String where, String... params) {
        return updateByWhere(null, tClass, update, where, params);
    }

    public static int updateByWhere(Database db, Class<? extends Dto> tClass, Dto update, Dto where) {
        return updateByWhere(db, tClass, update, where, false);
    }

    public static int updateByWhere(Database db, Class<? extends Dto> tClass, Dto update, Dto where, boolean includeNullValue) {
        try {
            lock();
            String tableName = getTableName(tClass);
            if (null == db) db = openWritableDatabase();
            if (isTableExist(db, tableName)) {
                ContentValues updateCVS = getContentValues(update, includeNullValue);
                ContentValues whereCVS = getContentValues(where, false);
                String whereSQL = createWhereSQL(whereCVS, true);
                Object[] objs = getBindArgs(whereCVS);
                String[] args = new String[objs.length];
                for (int i = 0; i < args.length; i++) {
                    args[i] = objs[i].toString();
                }
                try {
                    return update(db, tableName, updateCVS, whereSQL, args);
                } catch (Exception e) {
                    throw new DBDataException(update, e);
                }
            } else {
                return -1;
            }
        } finally {
            unlock();
        }
    }

    public static int updateByWhere(Database db, Class<? extends Dto> tClass, String update, String where, String... params) {
        try {
            lock();
            String tableName = getTableName(tClass);
            if (isTableExist(db, tableName)) {
                return execSQL(db, "UPDATE " + tableName + " SET " + update + " WHERE " + where, params);
            } else {
                return -1;
            }
        } finally {
            unlock();
        }
    }


    public static <T extends Dto> void updateListByWhere(Class<T> tClass, List<? extends Dto> updates,
                                                         List<? extends Dto> wheres) {
        updateListByWhere(null, tClass, updates, wheres);
    }

    public static <T extends Dto> void updateListByWhere(Database db, Class<T> tClass, List<? extends Dto> updates,
                                                         List<? extends Dto> wheres) {
        if (updates.size() != wheres.size()) {
            throw new DBRuntimeException("update list size not equals where list size.");
        }
        boolean inTransaction = false;
        if (null == db) {
            db = openWritableDatabase();
            inTransaction = true;
        }
        String tableName = getTableName(tClass);
        if (isTableExist(db, tableName)) {
            try {
                lock();
                if (inTransaction) db.beginTransaction();
                for (int i = 0; i < updates.size(); i++) {
                    Dto update = updates.get(i);
                    Dto where = wheres.get(i);
                    ContentValues updateCVS = getContentValues(update, false);
                    ContentValues whereCVS = getContentValues(where, false);
                    String updateSQL = createUpdateSQL(updateCVS, tableName, "");
                    String whereSQL = createWhereSQL(whereCVS, true);
                    Object[] objs = getBindArgs(updateCVS, whereCVS);
                    execSQL(db, updateSQL + whereSQL, objs);
                }
                if (inTransaction) db.setTransactionSuccessful();
            } finally {
                if (inTransaction) db.endTransaction();
                unlock();
            }
        }
    }

    private static String createWhereSQL(ContentValues values, Boolean withoutWhere) {
        StringBuilder sql = new StringBuilder(120);
        if (!withoutWhere) {
            sql.append("WHERE ");
        }
        int i = 0;
        for (Map.Entry<String, Object> entry : values.valueSet()) {
            sql.append((i > 0) ? "," : "");
            sql.append(entry.getKey());
            i++;
            sql.append("=?");
        }
        return sql.toString();
    }


    /**
     * insert a motel if the max id row is not equals the insert motel
     *
     * @param dto insert motel
     * @param <T> an  generic type extends from motel
     * @return if the value than 0 means that insert motel success.
     * if the value is -2 means that the max id row is equals the insert motel.
     * if the value is -1 means that has error occur or other else.
     */
    public static <T extends Dto> int insertWithoutEqualsMaxId(T dto) {
        checkAndCreateTable(dto);
        String tableName = getTableName(dto.getClass());
        Database db = openWritableDatabase();
        int ret;
        try {
            lock();
            db.beginTransaction();
            Class clz = dto.getClass();
            if (dto.equals(readByMaxId(clz))) {
                ret = -1;
            } else {
                long id = insert(db, tableName, null, getContentValues(dto));
                if (-1 != id) {
                    dto.setId(String.valueOf(id));
                    ret = 1;
                } else {
                    ret = -1;
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            throw new DBDataException(dto, e);
        } finally {
            db.endTransaction();
            unlock();
        }
        return ret;
    }

    /**
     * 根据ID删除类的数据
     *
     * @param clz 需要删除的类
     * @param id  需要删除条目的ID
     * @return 删除成功返回大于0 否则返回式－1
     */
    public static int delete(Class<? extends Dto> clz, String id) {
        return delete(null, clz, id);
    }

    /**
     * 根据ID删除类的数据
     *
     * @param clz 需要删除的类
     * @param id  需要删除条目的ID
     * @return 删除成功返回大于0 否则返回式－1
     */
    public static int delete(Database db, Class<? extends Dto> clz, String id) {
        try {
            lock();
            if (null == db) db = openWritableDatabase();
            String tableName = getTableName(clz);
            if (isTableExist(db, tableName)) {
                return delete(db, tableName, "id = ?", String.valueOf(id));
            }
            return -1;
        } finally {
            unlock();
        }
    }

    /**
     * 删除类的全部数据
     *
     * @param clz 需要删除的类
     * @return 删除成功返回大于0 否则返回式－1
     */
    public static int deleteAll(Class<? extends Dto> clz) {
        return deleteAll(null, clz);
    }

    /**
     * 删除类的全部数据
     *
     * @param clz 需要删除的类
     * @return 删除成功返回大于0 否则返回式－1
     */
    public static int deleteAll(Database database, Class<? extends Dto> clz) {
        String tableName = getTableName(clz);
        return deleteAll(database, tableName);
    }

    /**
     * 删除类的全部数据
     *
     * @param tableName 表名
     * @return 删除成功返回大于0 否则返回式－1
     */
    public static int deleteAll(Database database, String tableName) {
        if (null == database) database = openWritableDatabase();
        if (isTableExist(database, tableName)) {
            return delete(database, tableName, null);
        }
        return -1;
    }

    /**
     * 删除ID最小的那条数据
     *
     * @param clz 需要删除的类
     */
    public static void deleteByMinId(Class<? extends Dto> clz) {
        String tableName = getTableName(clz);
        Database db = openWritableDatabase();
        Cursor cursor = null;
        try {
            db.beginTransaction();
            String sql = "SELECT MIN(id) FROM " + tableName;
            cursor = rawQuery(db, sql);
            int lastId = -1;
            if (cursor.moveToNext()) {
                lastId = cursor.getInt(0);
            }
            if (-1 != lastId) {
                sql = "DELETE FROM " + tableName + " WHERE id = ?";
                execSQL(db, sql, new String[]{String.valueOf(lastId)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            CursorUtils.close(cursor);

        }
    }

    /**
     * 执行大量删除后，应该调用这个方法压缩数据库，以释放空间
     */
    public static void compressionDB() {
        compressionDB(null);
    }

    /**
     * 执行大量删除后，应该调用这个方法压缩数据库，以释放空间
     */
    public static void compressionDB(Database db) {
        /*if (null == db) db = openWritableDatabase();
        try {
            lock();
            db.execSQL("VACUUM");
        } finally {
            unlock();
        }*/
    }

    /**
     * 读取ID最大的那条数据
     *
     * @param clz 需要读取的类
     * @param <T> 类型继承自Dto
     * @return 返回读取到的数据
     */
    public static <T extends Dto> T readByMaxId(Class<T> clz) {
        String tableName = getTableName(clz);
        T t = null;
        if (isTableExist(tableName)) {
            Database db = openReadableDatabase();
            String sql = "SELECT MAX(id) FROM " + tableName;
            int id;
            Cursor cursor = null;
            try {
                cursor = rawQuery(db, sql);
                id = -1;
                if (cursor.moveToNext()) {
                    id = cursor.getInt(0);
                }
            } finally {
                CursorUtils.close(cursor);
            }
            if (-1 != id) {
                //noinspection unchecked
                t = read(clz, String.valueOf(id));
            }
        }
        return t;
    }

    /**
     * 根据ID更新数据
     *
     * @param dto 需要更新的数据类
     * @param id  数据条目ID
     * @param <T> 类型继承自Dto
     * @return 更新成功返回大于0 否则返回式－1
     */
    public static <T extends Dto> int update(T dto, String id) {
        return update(null, dto, id);
    }

    /**
     * 根据ID更新数据
     *
     * @param dto 需要更新的数据类
     * @param id  数据条目ID
     * @param <T> 类型继承自Dto
     * @return 更新成功返回大于0 否则返回式－1
     */
    public static <T extends Dto> int update(Database db, T dto, String id) {
        Class<? extends Dto> tClazz = dto.getClass();
        String tableName = getTableName(tClazz);
        if (null == db) db = openWritableDatabase();
        if (isTableExist(db, tableName)) {
            try {
                return update(db, tableName, getContentValues(dto), "id = ?", String.valueOf(id));
            } catch (Exception e) {
                throw new DBDataException(dto, e);
            }
        }
        return -1;
    }

    /**
     * 根据ID读取数据
     *
     * @param clz 需要读取的类
     * @param id  数据条目的ID
     * @param <T> 类型继承自Dto
     * @return 读取到的Dto对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends Dto> T read(Class<T> clz, String id) {
        return read(null, clz, id);
    }

    /**
     * 根据ID读取数据
     *
     * @param clz 需要读取的类
     * @param id  数据条目的ID
     * @param <T> 类型继承自Dto
     * @return 读取到的Dto对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends Dto> T read(Database db, Class<T> clz, String id) {
        String tableName = getTableName(clz);
        if (null == db) db = openReadableDatabase();
        if (isTableExist(tableName)) {
            Cursor cursor = null;
            try {
                T ret = null;
                cursor = rawQuery(db, "select * from " + tableName + " where id = ?", id);
                if (cursor.moveToNext()) {
                    HashMap<String, String> data = new HashMap<String, String>();
                    String[] names = cursor.getColumnNames();
                    for (String name : names) {
                        data.put(name, cursor.getString(cursor.getColumnIndex(name)));
                    }
                    ret = getDtoFromTableData(clz, getColumnFields(clz), data);
                }
                return ret;
            } finally {
                CursorUtils.close(cursor);
            }
        }
        return null;
    }

    private static <M> M getDtoFromTableData(Class<M> clz, Collection<Field> fields, HashMap<String, String> data) {
        M ret;
        try {
            ret = clz.newInstance();
        } catch (Exception e) {
            String tableName = "", className = "";
            if (Dto.class.isAssignableFrom(clz)) {
                //noinspection unchecked
                tableName = getTableName((Class<? extends Dto>) clz);
            }
            className = clz.getName();
            throw new DBRuntimeException("No Constructor for class[" + className + "] tableName[" + tableName + "], Error msg:" + e.getMessage(), e);
        }
        try {
            for (Field field : fields) {
                String columnName = getColumnName(field);
                if (null != columnName) {
                    String val = data.get(columnName);
                    field.setAccessible(true);
                    field.set(ret, getFieldObjectValue(field, val));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            String tableName = "", className = "";
            if (Dto.class.isAssignableFrom(clz)) {
                //noinspection unchecked
                tableName = getTableName((Class<? extends Dto>) clz);
            }
            className = clz.getName();
            throw new DBRuntimeException("ClassName[" + className + "],TableName[" + tableName + "], Error msg:" + e.getMessage(), e);
        }
        return ret;
    }

    private final static HashMap<Integer, String> mFieldColumnName = new HashMap<Integer, String>();

    private static String getColumnName(Field field) {
        String columnName = mFieldColumnName.get(field.hashCode());
        if (null != columnName) {
            return columnName;
        } else {
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                columnName = column.name();
                if (StringUtil.isEmpty(columnName)) {
                    columnName = field.getName();
                }
            } else if (field.isAnnotationPresent(FromDB.class)) {
                FromDB fdb = field.getAnnotation(FromDB.class);
                columnName = getFromDBColumnName(fdb, field);
            }
            mFieldColumnName.put(field.hashCode(), columnName);
            return columnName;
        }
    }

    public static String columnToString(Column column) {
        return "column info=id" + column.id() + ",type:" + column.type() + ",len:" +
                column.len() + ",pk:" + column.pk() + ",aicr:" + column.aicr() + ",canull:" +
                column.canull() + ",unique:" + column.unique() + ",def:" + column.def();
    }

    /**
     * 读取类型的全部数据
     *
     * @param clz 需要读取的类
     * @param <T> 类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readAll(Class<T> clz) {
        String tableName = getTableName(clz);
        return readBySQL(clz, clz, getColumnFields(clz), "SELECT * FROM " + tableName);
    }

    /**
     * 读取类型的全部数据
     *
     * @param clz 需要读取的类
     * @param <T> 类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readAll(Database db, Class<T> clz) {
        String tableName = getTableName(clz);
        return readBySQL(db, clz, clz, getColumnFields(clz), "SELECT * FROM " + tableName);
    }

    public static <M, T extends Dto> List<M> readAll(Class<T> tClass, Class<M> mClass) {
        return readByWhere(tClass, mClass, "1=1");
    }

    public static <M, T extends Dto> List<M> readByWhere(Class<T> tClass, Class<M> mClass, String where, String... params) {
        return readByWhere(null, tClass, mClass, 1, Integer.MAX_VALUE, where, params);
    }

    public static <M, T extends Dto> List<M> readByWhere(Database db, Class<T> tClass, Class<M> mClass,
                                                         Integer pageIndex, Integer pageCount,
                                                         String where, String... params) {
        Field[] list = mClass.getDeclaredFields();
        HashMap<String, Field> map = new HashMap<String, Field>();
        for (Field field : list) {
            if (field.isAnnotationPresent(FromDB.class)) {
                FromDB fdb = field.getAnnotation(FromDB.class);
                String columnName = getFromDBColumnName(fdb, field);
                map.put(columnName, field);
            }
        }
        String need = StringUtil.join(map.keySet(), ",");
        final Integer firstIndex = (pageIndex * pageCount) - pageCount;
        String tableName = getTableName(tClass);
        String sql = "SELECT " + need + " FROM " + tableName + " WHERE " + where;
        if (!where.toUpperCase().contains("LIMIT")) {
            sql = sql + " LIMIT " + pageCount + " OFFSET " + firstIndex;
        }
        return readBySQL(db, tClass, mClass, map.values(), sql, params);
    }

    private static String getFromDBColumnName(FromDB fdb, Field field) {
        String name = fdb.column();
        if (StringUtil.isEmpty(name)) {
            name = field.getName();
        }
        return name;
    }

    /**
     * 根据条件读取
     *
     * @param clz    需要读取的类
     * @param where  读取条件
     * @param params 参数
     * @param <T>    类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Class<T> clz, String where, String... params) {
        Database db = null;
        return readByWhere(db, clz, where, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz   需要读取的类
     * @param where 参数
     * @param <T>   类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Class<T> clz, WhereBuilder where) {
        Database db = null;
        return readByWhere(db, clz, where.sql(), where.params());
    }

    /**
     * 根据条件读取
     *
     * @param clz    需要读取的类
     * @param where  读取条件
     * @param params 参数
     * @param <T>    类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Database db, Class<T> clz, String where, String... params) {
        return readByWhere(db, clz, where, 1, Integer.MAX_VALUE, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz   需要读取的类
     * @param where 参数
     * @param <T>   类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Database db, Class<T> clz, WhereBuilder where) {
        return readByWhere(db, clz, where.sql(), 1, Integer.MAX_VALUE, where.params());
    }

    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param where     读取条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @param params    参数
     * @param <T>       类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Class<T> clz, String where, Integer pageIndex, Integer pageCount, String... params) {
        Database db = null;
        return readByWhere(db, clz, where, pageIndex, pageCount, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param where     读取条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @param <T>       类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Class<T> clz, WhereBuilder where, Integer pageIndex, Integer pageCount) {
        Database db = null;
        return readByWhere(db, clz, where.sql(), pageIndex, pageCount, where.params());
    }


    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param where     读取条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @param <T>       类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Database db, Class<T> clz, String where, Integer pageIndex, Integer pageCount, String... params) {
        final Integer firstIndex = (pageIndex * pageCount) - pageCount;
        String tableName = getTableName(clz);
        String sql = "SELECT * FROM " + tableName + " WHERE " + where;
        if (!where.toUpperCase().contains("LIMIT")) {
            sql = sql + " LIMIT " + pageCount + " OFFSET " + firstIndex;
        }
        return readBySQL(db, clz, clz, getColumnFields(clz), sql, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param where     读取条件
     * @param pageIndex 页码
     * @param pageCount 每页数量
     * @param <T>       类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readByWhere(Database db, Class<T> clz, WhereBuilder where, Integer pageIndex, Integer pageCount) {
        final Integer firstIndex = (pageIndex * pageCount) - pageCount;
        String tableName = getTableName(clz);
        String whereSQL = where.sql();
        String sql = "SELECT * FROM " + tableName + " WHERE " + whereSQL;
        if (!whereSQL.toUpperCase().contains("LIMIT")) {
            sql = sql + " LIMIT " + pageCount + " OFFSET " + firstIndex;
        }
        return readBySQL(db, clz, clz, getColumnFields(clz), sql, where.params());
    }

    /**
     * 根据条件读取
     *
     * @param clz    需要读取的类
     * @param where  读取条件
     * @param params 参数
     * @param <T>    类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readNeedByWhere(Class<T> clz, String need, String where, String... params) {
        return readNeedByWhere(null, clz, need, where, 1, Integer.MAX_VALUE, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz    需要读取的类
     * @param where  读取条件
     * @param params 参数
     * @param <T>    类型继承自Dto
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readNeedByWhere(Database db, Class<T> clz, String need, String where, String... params) {
        return readNeedByWhere(db, clz, need, where, 1, Integer.MAX_VALUE, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param need      需要读取的字段
     * @param where     读取条件
     * @param pageIndex 读取页码
     * @param pageCount 每页个数
     * @param params    参数
     * @param <T>       读取类型
     * @return 读取到的数据
     */
    public static <T extends Dto> List<T> readNeedByWhere(Class<T> clz, String need, String where, Integer pageIndex, Integer pageCount, String... params) {
        Database db = null;
        return readNeedByWhere(db, clz, need, where, pageIndex, pageCount, params);
    }

    /**
     * 根据条件读取
     *
     * @param clz       需要读取的类
     * @param need      需要读取的字段
     * @param where     读取条件
     * @param pageIndex 读取页码
     * @param pageCount 每页个数
     * @param params    参数
     * @param <T>       读取类型
     * @return 读取到的数据
     */
    public static <T extends Dto> List<T> readNeedByWhere(Database db, Class<T> clz, String need, String where, Integer pageIndex, Integer pageCount, String... params) {
        final Integer firstIndex = (pageIndex * pageCount) - pageCount;
        String tableName = getTableName(clz);
        String sql = "SELECT " + need + " FROM " + tableName + " WHERE " + where;
        if (!where.toUpperCase().contains("LIMIT")) {
            sql = sql + " LIMIT " + pageCount + " OFFSET " + firstIndex;
        }
        return readBySQL(db, clz, clz, getColumnFields(clz), sql, params);
    }

    /**
     * 根据条件更新或者插入数据
     *
     * @param dto    需要更新列表
     * @param where  更新条件
     * @param params 参数
     */

    public static boolean updateOrInsertByWhere(Dto dto, String where, String... params) {
        String tableName = getTableName(dto.getClass());
        checkAndCreateTable(dto.getClass());
        Database db = openWritableDatabase();
        boolean isExist = existByWhere(dto.getClass(), where, params);
        try {
            if (isExist) {
                return update(db, tableName, getContentValues(dto), where, params) > 0;
            } else {
                return insert(db, dto) > 0;
            }
        } catch (Exception e) {
            throw new DBDataException(dto, e);
        }
    }

    /**
     * 根据条件判断数据是否存在
     *
     * @param tClass 需要判断的表
     * @param where  需要判断的条件
     * @param params 需要判断的条件值
     * @return 是否存在
     */
    public static boolean existByWhere(Class<? extends Dto> tClass, String where, String... params) {
        return existByWhere(null, tClass, where, params);
    }

    /**
     * 根据条件判断数据是否存在
     *
     * @param tClass 需要判断的表
     * @param where  需要判断的条件
     * @param params 需要判断的条件值
     * @return 是否存在
     */
    public static boolean existByWhere(Database db, Class<? extends Dto> tClass, String where, String... params) {
        Cursor cursor = null;
        try {
            if (null == db) db = openReadableDatabase();
            String tableName = getTableName(tClass);
            try {
                cursor = rawQuery(db, "select 1 from " + tableName + " where " + where, params);
            } catch (SQLiteException e) {
                checkErrorAndHandleBase(db, e, tClass);
                if (e.getMessage().contains("no such table")) {
                    return false;
                } else {
                    throw e;
                }
            }
            return cursor.moveToNext();
        } finally {
            CursorUtils.close(cursor);
        }
    }

    /**
     * 读取相应的类的数据
     *
     * @param clz    需要读取的表
     * @param sql    SQL语句
     * @param params 参数
     * @param <T>    表类型 （继承自Dto）
     * @return 读取到相应类型的List对象
     */
    public static <T extends Dto> List<T> readBySQL(Class<T> clz, String sql, String... params) {
        return readBySQL(clz, clz, getColumnFields(clz), sql, params);
    }

    /**
     * 读取相应的类的数据
     *
     * @param clz    需要读取的表
     * @param retClz 读取到返回的数据类型
     * @param fields 需要赋值的字段，从 retClz中获取到的
     * @param sql    SQL语句
     * @param params 参数
     * @param <M>    返回的数据类型
     * @param <T>    表类型 （继承自Dto）
     * @return 读取到相应类型的List对象
     */
    private static <M, T extends Dto> List<M> readBySQL(Class<T> clz, Class<M> retClz,
                                                        Collection<Field> fields, String sql, String... params) {
        return readBySQL(null, clz, retClz, fields, sql, params);
    }

    private static <M, T extends Dto> List<M> readBySQL(Database db, Class<T> tClass, Class<M> retClz,
                                                        Collection<Field> fields, String sql, String... params) {
        List<M> lists = new ArrayList<M>();
        String tableName = getTableName(tClass);
        if (null == db) db = openReadableDatabase();
        if (isTableExist(db, tableName)) {
            Cursor cursor = null;
            try {
                cursor = rawQuery(db, sql, params);
                lists.addAll(cursorToClassList(retClz, fields, cursor));
            } finally {
                CursorUtils.close(cursor);
            }
        }
        return lists;
    }

    public static <M> List<M> cursorToClassList(Class<M> retClz, Cursor cursor) {
        if (Dto.class.isAssignableFrom(retClz)) {
            //noinspection unchecked
            Class<? extends Dto> dtoClz = (Class<? extends Dto>) retClz;
            return cursorToClassList(retClz, getColumnFields(dtoClz), cursor);
        } else {
            return cursorToClassList(retClz, ClassFieldUtil.getFields(retClz), cursor);
        }
    }

    public static <M> List<M> cursorToClassList(Class<M> retClz, Field[] fields, Cursor cursor) {
        List<Field> list = new ArrayList<Field>();
        Collections.addAll(list, fields);
        return cursorToClassList(retClz, list, cursor);
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("############.########");

    public static <M> List<M> cursorToClassList(Class<M> retClz, Collection<Field> fields, Cursor cursor) {
        List<M> lists = new ArrayList<M>();
        while (cursor.moveToNext()) {
            HashMap<String, String> data = new HashMap<String, String>();
            String[] names = cursor.getColumnNames();
            for (String name : names) {
                int index = cursor.getColumnIndex(name);
                int type = cursor.getType(index);
                String val;
                if (type == Cursor.FIELD_TYPE_FLOAT) {
                    double d = cursor.getDouble(index);
                    if (d != 0) {
                        val = DECIMAL_FORMAT.format(d);
                    } else {
                        val = DECIMAL_FORMAT.format(cursor.getFloat(index));
                    }
                } else {
                    val = cursor.getString(index);
                }
                data.put(name, val);
            }
            lists.add(getDtoFromTableData(retClz, fields, data));
        }
        return lists;
    }

    /**
     * 获取相应的类有多少行数据
     *
     * @param tClass 需删除的类
     * @return 多少行数据
     */
    public static int getRowCount(Class<? extends Dto> tClass) {
        String tableName = getTableName(tClass);
        if (isTableExist(tableName)) {
            Database db = openReadableDatabase();
            String sql = "select count(id) from " + tableName;
            Cursor cursor = null;
            try {
                cursor = rawQuery(db, sql);
                if (cursor.moveToNext()) {
                    return cursor.getInt(0);
                }
            } finally {
                CursorUtils.close(cursor);
            }

        }
        return -1;
    }

    /**
     * 删除数据根据传入的Where条件
     *
     * @param tClass  需删除的类
     * @param where   where条件
     * @param objects 参数
     */
    public static int deleteByWhere(Class<? extends Dto> tClass, String where, Object... objects) {
        return deleteByWhere(null, tClass, where, objects);
    }

    /**
     * 删除数据根据传入的Where条件
     *
     * @param tClass  需删除的类
     * @param where   where条件
     * @param objects 参数
     */
    public static int deleteByWhere(Database db, Class<? extends Dto> tClass, String where, Object... objects) {
        String tableName = getTableName(tClass);
        if (isTableExist(db, tableName)) {
            if (null == db) db = openWritableDatabase();
            String sql = "delete from " + tableName + " where " + where;
            return execSQL(db, sql, objects);
        } else {
            return -1;
        }
    }


    public static int readMaxVersionCode(Class<? extends Dto> tClass) {
        String tableName = getTableName(tClass);
        if (isTableExist(tableName)) {
            Cursor cursor = null;
            try {
                cursor = rawQuery("SELECT MAX(versionCode) FROM " + tableName + " WHERE AND state=?", "0");
                if (cursor.moveToNext()) {
                    return cursor.getInt(0);
                } else {
                    return 0;
                }
            } catch (Exception e) {
                return 0;
            } finally {
                CursorUtils.close(cursor);
            }
        }
        return 0;
    }

    public static int cursorToNumber(Cursor cursor) {
        String s = cursorToString(cursor);
        if ("".equals(s)) return 0;
        return Integer.parseInt(s);
    }

    public static long cursorToLong(Cursor cursor) {
        String s = cursorToString(cursor);
        if (null == s || "".equals(s) || "null".equals(s.trim())) return 0l;
        return Long.parseLong(s);
    }

    public static String cursorToString(Cursor cursor) {
        if (null == cursor || cursor.isClosed()) {
            return "";
        }
        try {
            if (cursor.moveToNext()) {
                return cursor.getString(0);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        } finally {
            CursorUtils.close(cursor);
        }
    }

    private static Object getFieldObjectValue(Field field, String val) {
        Class<?> type = field.getType();
        if (Integer.class.equals(type) || "int".equals(type.getName())) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return Integer.valueOf(val);
            }
        } else if (String.class.equals(type)) {
            return val;
        } else if (Long.class.equals(type) || "long".equals(type.getName())) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return Long.valueOf(val);
            }
        } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return Boolean.valueOf(val);
            }
        } else if (Double.class.equals(type) || "double".equals(type.getName())) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return Double.valueOf(val);
            }
        } else if (Float.class.equals(type) || "float".equals(type.getName())) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return Float.valueOf(val);
            }
        } else if (BigDecimal.class.equals(type)) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return new BigDecimal(val);
            }
        } else if (BigInteger.class.equals(type)) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                return new BigInteger(val);
            }
        } else if (Enum.class.isAssignableFrom(type)) {
            if (null == val || val.length() == 0) {
                return null;
            } else {
                //noinspection unchecked
                Class<Enum> typeClazz = (Class<Enum>) type;
                return Enum.valueOf(typeClazz, val);
            }
        } else {
            throw new RuntimeException("can't support for type " + type.getName());
        }
    }

    public static void deleteTableIfExist(Database db, String tableName) {
        try {
            lock();
            if (null == db) db = openWritableDatabase();
            execSQL(db, "DROP TABLE IF EXISTS " + tableName);
        } finally {
            unlock();
        }
    }

    public static boolean deleteDatabase(Context context) {
        return DBHelper.deleteDatabase(context);
    }


}