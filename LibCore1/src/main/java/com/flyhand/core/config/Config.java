package com.flyhand.core.config;

import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.MobileInfoUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-10-21
 * Time: Afternoon 2:47
 */
public class Config {
    public static final String DB_FOLDER = "";
    public static final String DB_NAME = "";

    public static final int VERSION_CODE = getPackageVersionCode();
    public static final String VERSION_NAME = getPackageVersionName();
    public static final String PACKAGE_NAME = getPackageName();
    public static final boolean DEV = false;
    public static final String Device_ID = MobileInfoUtil.GetDeviceID(AbstractCoreApplication.get());
    public static boolean Use_Cache = getUseCache();


    private static int getPackageVersionCode() {
        try {
            PackageManager pm = AbstractCoreApplication.get().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AbstractCoreApplication.get().getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }

    private static boolean getUseCache() {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(AbstractCoreApplication.get());
        return pm.getBoolean("remote_access_use_cache", false);
    }

    private static String getPackageVersionName() {
        try {
            PackageManager pm = AbstractCoreApplication.get().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AbstractCoreApplication.get().getPackageName(), 0);
            return pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }


    private static String getPackageName() {
        return AbstractCoreApplication.get().getPackageName();
    }

    private static int getTargetSdkVersion() {
        try {
            PackageManager pm = AbstractCoreApplication.get().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(AbstractCoreApplication.get().getPackageName(), 0);
            return 3;
        } catch (PackageManager.NameNotFoundException e) {
            return 0;
        }
    }
}
