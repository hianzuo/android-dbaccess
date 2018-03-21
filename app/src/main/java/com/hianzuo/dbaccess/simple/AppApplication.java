package com.hianzuo.dbaccess.simple;

import android.app.Application;

import com.flyhand.core.app.AbstractCoreApplication;
import com.hianzuo.dbaccess.config.DBHelper;

/**
 * Created by Ryan
 * On 2016/5/29.
 */
public class AppApplication extends AbstractCoreApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper.config(new AppDatabaseConfig(this));
    }
}
