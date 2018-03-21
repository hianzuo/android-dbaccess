package com.hianzuo.dbaccess.store;

import com.hianzuo.dbaccess.DBInterface;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;

import java.util.List;

/**
 * User: Ryan
 * Date: 14-3-31
 * Time: 下午5:21
 */
@Table(ver = 4, name = "app_db_config")
public class SQLiteDBConfig extends Dto {
    @Column(id = 1, unique = true)
    private String _key;
    @Column(id = 2, canull = true)
    private String _value;

    public String key() {
        return _key;
    }

    public String value() {
        return _value;
    }

    public void key(String _key) {
        this._key = _key;
    }

    public void value(String _value) {
        this._value = _value;
    }

    public static int updateSQLiteDBConfig(Database db, String key, String value) {
        return DBInterface.updateByWhere(db, SQLiteDBConfig.class, "_value=?", "_key=?", value, key);
    }

    public static SQLiteDBConfig createSQLiteDBConfig(Database db, String key, String value) {
        SQLiteDBConfig config = new SQLiteDBConfig();
        config.key(key);
        config.value(value);
        DBInterface.saveOrUpdate(db, config);
        return readSQLiteDBConfig(db, key);
    }

    public static SQLiteDBConfig readSQLiteDBConfig(Database db, String key) {
        List<SQLiteDBConfig> configs = DBInterface.readByWhere(db, SQLiteDBConfig.class, "_key=?", 1, 1, key);
        if (null != configs && configs.size() > 0) {
            return configs.get(0);
        }
        return null;
    }


}
