package com.hianzuo.dbaccess.simple;

import android.content.Context;
import android.os.Environment;

import com.hianzuo.dbaccess.CheckAndCreateTableUtil;
import com.hianzuo.dbaccess.Database;
import com.hianzuo.dbaccess.config.DBConfig;
import com.hianzuo.dbaccess.config.UpdateTableMethod;
import com.hianzuo.dbaccess.util.SDCardUtil;

import java.io.File;

/**
 * Created by Ryan
 * On 2016/5/29.
 */
public class AppDatabaseConfig extends DBConfig {

    public AppDatabaseConfig(Context context) {
        super(context);
    }

    @Override
    public Boolean isPrintSQL() {
        return true;
    }

    @Override
    public UpdateTableMethod getUpdateTableMethod() {
        return UpdateTableMethod.AUTO_CHECK;
    }

    @Override
    public void onOpen(Database db) {
        super.onOpen(db);
        CheckAndCreateTableUtil.createAllInPackage(getContext(),
                db, "com.hianzuo.dbaccess.simple.dao");
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public String getDBFile() {
        String dbAccess = SDCardUtil.getWriteDir(getContext(), "DBAccess");
        return new File(dbAccess, "app.db").getAbsolutePath();
    }

}
