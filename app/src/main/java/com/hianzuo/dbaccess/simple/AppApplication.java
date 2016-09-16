package com.hianzuo.dbaccess.simple;

import android.app.Application;

import com.hianzuo.dbaccess.config.DBHelper;

/**
 * Created by Ryan
 * On 2016/5/29.
 */
public class AppApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DBHelper.config(new AppDatabaseConfig(this));
    }
}
