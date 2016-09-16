package com.hianzuo.dbaccess.config;

import android.content.Context;
import android.content.pm.PackageManager;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.controller.DtoControllerCreator;
import com.hianzuo.dbaccess.store.DBTable;
import com.hianzuo.dbaccess.store.SQLiteTable;

/**
 * User: Ryan
 * Date: 14-4-2
 * Time: 下午7:32
 */
public class DBConfig {
    private Context context;

    public DBConfig(Context context) {
        this.context = context;
    }

    protected String getDBName() {
        return "app.db";
    }

    public String getDBFile() {
        return getDBName();
    }

    public int getDBVersion() {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    public UpdateTableMethod getUpdateTableMethod() {
        return UpdateTableMethod.AUTO_CHECK;
    }

    public DtoControllerCreator getDtoControllerCreator() {
        return new DtoControllerCreator();
    }

    public Boolean isPrintSQL() {
        return false;
    }

    public void onUpgrade(Database db, int oldVersion, int newVersion) {
    }

    public Context getContext() {
        return context;
    }

    public void afterUpdateDBStructure(Database db, int oldVersion, int newVersion) {
    }

    public void beforeUpdateDBStructure(Database db, int oldVersion, int newVersion) {
    }

    public void onOpen(Database db) {
        System.out.println("DBHelper.onOpen:" + db.getPath());
    }

    public DBTable newDBTable() {
        return new SQLiteTable();
    }
}
