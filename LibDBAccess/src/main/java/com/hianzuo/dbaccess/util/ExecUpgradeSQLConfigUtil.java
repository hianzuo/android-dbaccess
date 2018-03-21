package com.hianzuo.dbaccess.util;

import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.model.ExecUpgradeSQLConfig;
import com.hianzuo.dbaccess.store.SQLiteDBConfig;

/**
 * Created by Ryan
 * On 2017/4/6.
 */

public class ExecUpgradeSQLConfigUtil {
    private static final String EXEC_UPGRADE_SQL = "EXEC_UPGRADE_SQL";

    public static ExecUpgradeSQLConfig read(Database db) {
        SQLiteDBConfig config = SQLiteDBConfig.readSQLiteDBConfig(db, EXEC_UPGRADE_SQL);
        if (null != config) {
            return ExecUpgradeSQLConfig.fromString(config.value());
        }
        return null;
    }

    public static void create(Database db, int fromVersion, int state) {
        String value = new ExecUpgradeSQLConfig(fromVersion, state).toString();
        SQLiteDBConfig.createSQLiteDBConfig(db, EXEC_UPGRADE_SQL, value);
    }

    public static int modify(Database db, int fromVersion, int state) {
        String value = new ExecUpgradeSQLConfig(fromVersion, state).toString();
        return SQLiteDBConfig.updateSQLiteDBConfig(db, EXEC_UPGRADE_SQL, value);
    }


    public static void saveUpdate(Database db, int fromVersion, int state) {
        ExecUpgradeSQLConfig config = ExecUpgradeSQLConfigUtil.read(db);
        if (null == config) {
            ExecUpgradeSQLConfigUtil.create(db, fromVersion, state);
        } else {
            if (config.isUpgraded()) {
                ExecUpgradeSQLConfigUtil.modify(db, fromVersion, state);
            } else {
                //之前的版本还没有得到更新
            }
        }
    }
}
