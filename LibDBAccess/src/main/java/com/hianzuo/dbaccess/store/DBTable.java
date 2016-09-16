package com.hianzuo.dbaccess.store;

import com.hianzuo.dbaccess.Database;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Ryan on 2015/7/6.
 */
public interface DBTable {
    DBTable create(String className, int ver, boolean clearOnAddColumn);

    String makeCreateSQLFromColumns(String tableName, List<Field> fields);

    String getClassName();

    void setId(Integer id);

    int update(Database db);

    void nullId();

    String getSigner();

    int save(Database db);

    String getId();

    void saveOrUpdateColumnList(Database db, int id);
}
