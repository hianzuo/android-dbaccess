package com.flyhand.core.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

import com.flyhand.core.app.AbstractCoreApplication;

import java.util.UUID;

public class DeviceUuidFactory {
    protected static final String PREFS_FILE = "device_id_nce.xml";
    protected static final String PREFS_DEVICE_ID = "device_id_nce";
    protected static String uuid;

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 短8位UUID思想其实借鉴微博短域名的生成方式，但是其重复概率过高，而且每次生成4个，需要随即选取一个。
     * 本算法利用62个可打印字符，通过随机生成32位UUID，由于UUID都为十六进制，
     * 所以将UUID分成8组，每4个为一组，然后通过模62操作，结果作为索引取出字符，
     * 这样重复率大大降低。
     * <p/>
     * 经测试，在生成一千万个数据也没有出现重复，完全满足大部分需求。
     *
     * @param tempId 32位
     * @return shortUuid
     */
    public static String generateShortUuid(String tempId) {
        StringBuilder shortBuffer = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            String str = tempId.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    public static void removeSp() {
        final SharedPreferences prefs = AbstractCoreApplication.get().getSharedPreferences(PREFS_FILE, 0);
        prefs.edit().remove(PREFS_DEVICE_ID).apply();
    }


    /*10-19 19:45:05.780: INFO/System.out(7884): ddddddddddxxxxxxxxx1:1b5c124a0e82939539ee383774e77633
10-19 19:45:05.800: INFO/System.out(7884): ddddddddddxxxxxxxxx2:93df70c63efcec0d77f27a47c532e474*/
    public DeviceUuidFactory(Context context) {
        if (uuid == null) {
            synchronized (DeviceUuidFactory.class) {
                if (uuid == null) {
                    final SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, 0);
                    final String id = prefs.getString(PREFS_DEVICE_ID, null);
                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = id;
                    } else {
                        //read deviceId
                        uuid = createDeviceId(context);
                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();
                    }
                }
            }
        }
    }

    public String createDeviceId(Context context) {
        TelephonyManager tm = ((TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE));
        String deviceId;
        if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_UNKNOWN) {
            deviceId = null;
        } else {
            deviceId = tm.getDeviceId();
            if (!isDeviceId(deviceId)) {
                deviceId = tm.getSubscriberId();
            }
        }
        if (!isDeviceId(deviceId)) {
            //read androidId
            deviceId = Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
        }
        if (!isDeviceId(deviceId)) {
            //mac address
            deviceId = getMacAddress(context);
        }
        String tempId;
        if (null == deviceId) {
            tempId = MD5Utils.MD5(UUID.randomUUID().toString().replace("-", ""));
        } else {
            tempId = MD5Utils.MD5(deviceId);
        }
        return generateShortUuid(tempId);
    }

    private boolean isDeviceId(String deviceId) {
        if (null == deviceId) {
            deviceId = "";
        }
        deviceId = deviceId.trim();
        return
                !"".equals(deviceId) &&
                        //It appears that all Droid 2 devices share the same DeviceID 9774d56d682e549c
                        !"9774d56d682e549c".equals(deviceId) &&
                        !"012345678912345".equals(deviceId) &&
                        !"000000000000000".equals(deviceId);
    }

    public String getMacAddress(Context context) {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (null != wifi) {
            WifiInfo info = wifi.getConnectionInfo();
            if (null != info) {
                return info.getMacAddress();
            }
        }
        return null;
    }

    /**
     * Returns a unique UUID for the current android device.  As with all UUIDs, this unique ID is "very highly likely"
     * to be unique across all Android devices.  Much more so than ANDROID_ID is.
     * <p/>
     * The UUID is generated by using ANDROID_ID as the base key if appropriate, falling back on
     * TelephonyManager.getDeviceID() if ANDROID_ID is known to be incorrect, and finally falling back
     * on a random UUID that's persisted to SharedPreferences if getDeviceID() does not return a
     * usable value.
     * <p/>
     * In some rare circumstances, this ID may change.  In particular, if the device is factory reset a new device ID
     * may be generated.  In addition, if a user upgrades their phone from certain buggy implementations of Android 2.2
     * to a newer, non-buggy version of Android, the device ID may change.  Or, if a user uninstalls your app on
     * a device that has neither a proper Android ID nor a Device ID, this ID may change on reinstallation.
     * <p/>
     * Note that if the code falls back on using TelephonyManager.getDeviceId(), the resulting ID will NOT
     * change after a factory reset.  Something to be aware of.
     * <p/>
     * Works around a bug in Android 2.2 for many devices when using ANDROID_ID directly.
     *
     * @return a UUID that may be used to uniquely identify your device for most purposes.
     * @see http://code.google.com/p/android/issues/detail?id=10603
     */
    public String getDeviceId() {
        return uuid;
    }
}
