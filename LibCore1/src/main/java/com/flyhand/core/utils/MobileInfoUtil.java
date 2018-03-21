package com.flyhand.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.flyhand.core.app.AbstractCoreApplication;
import com.hianzuo.logger.Log;

/**
 * User: Ryan
 * Date: 11-10-3
 * Time: Afternoon 3:33
 */
public class MobileInfoUtil {
    private static final String TAG = MobileInfoUtil.class.getSimpleName();

    public static StringBuffer GetMobileInfo() {
        PackageManager packageManager = AbstractCoreApplication.get().getPackageManager();
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(AbstractCoreApplication.get().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final StringBuffer buffer = new StringBuffer();
        buffer.append("AppName:").append(RUtils.getRString("app_name"))
                .append("\r\nVersion :").append(Build.VERSION.RELEASE)
                .append("  SDK:").append(Build.VERSION.SDK);
        if (null != packageInfo) {
            buffer.append("\r\nVersionCode:").append(packageInfo.versionCode)
                    .append("   VersionName:").append(packageInfo.versionName);
        }
        if (StringUtil.isNotEmpty(Build.MODEL)) {
            buffer.append("\r\nMiblie MODEL:").append(Build.MODEL);
        }
        if (StringUtil.isNotEmpty(Build.BRAND)) {
            buffer.append(" Miblie BRAND:").append(Build.BRAND);
        }
        if (StringUtil.isNotEmpty(Build.BOARD)) {
            buffer.append(" Miblie BOARD:").append(Build.BOARD);
        }
        return buffer;
    }
    private static String mDeviceId = null;
    public synchronized static String GetDeviceID(Context context) {
        if(null == mDeviceId){
            mDeviceId = new DeviceUuidFactory(context).getDeviceId();
            Log.d(TAG, "GetDeviceID: " + mDeviceId);
        }
        return mDeviceId;
    }

    public static String GetMobileCode() {
        return GetDeviceID(AbstractCoreApplication.get());
    }
}
