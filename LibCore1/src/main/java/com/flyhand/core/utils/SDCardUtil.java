package com.flyhand.core.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Environment;

import com.flyhand.core.app.AbstractCoreApplication;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 12-12-6
 * Time: Afternoon 3:15
 */
public class SDCardUtil {
    private final static Object mLock = new Object();
    private static final String APP_KEY = "mjkf_external_mounts_1123";
    private static String store_directory;
    private static List<String> all_sdcard_mounts;
    private static HashSet<SDCardListener> listeners = new HashSet<SDCardListener>();


    public static void init() {
        registerSDCardListener();
    }

    // 监听类
    private static final BroadcastReceiver sdcardListener = new BroadcastReceiver() {
        boolean isRemoved = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
                isRemoved = false;
                String mounted = intent.getData().toString().substring("file://".length());
                for (SDCardListener listener : listeners) {
                    listener.onMounted(mounted);
                }
            } else if (Intent.ACTION_MEDIA_BAD_REMOVAL.equals(action)) {
                isRemoved = true;
                store_directory = null;
                all_sdcard_mounts = null;
                for (SDCardListener listener : listeners) {
                    listener.onRemoved();
                }
            } else if (Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)) {
                if (isRemoved) {
                    removeExternalMountsFromApp(context);
                } else {
                    String mounted = intent.getData().toString().substring("file://".length());
                    ArrayList<String> mList = new ArrayList<String>();
                    mList.add(mounted);
                    saveExternalMountsToApp(context, mList);
                    getAllSDCardMounts(context);
                    getStoreDirectory(context);
                    store_directory = null;
                    all_sdcard_mounts = null;
                    for (SDCardListener listener : listeners) {
                        listener.onMountedFinish(mounted);
                    }
                }
            }
        }
    };

    private static HashSet<String> getExternalMounts(Context context) {
        synchronized (mLock) {
            HashSet<String> result = readExternalMountsFromApp(context);
            if (null == result) {
                result = readExternalMountsFromDrive();
                if (null != result) {
                    saveExternalMountsToApp(context, result);
                }
            }
            return result;
        }
    }

    // 注册监听
    private static void registerSDCardListener() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addDataScheme("file");
        AbstractCoreApplication.get().registerReceiver(sdcardListener, intentFilter);
    }

    private static void saveExternalMountsToApp(Context context, Collection<String> result) {
        synchronized (mLock) {
            StringBuilder sb = new StringBuilder();
            for (String s : result) {
                sb.append(s).append("\n");
            }
            SharedPreferences sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(context);
            sp.edit().putString(APP_KEY, sb.toString()).commit();
        }

    }

    private static HashSet<String> readExternalMountsFromApp(Context context) {
        synchronized (mLock) {
            SharedPreferences sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(context);
            String s = sp.getString(APP_KEY, "");
            if (!"".equals(s)) {
                HashSet<String> hs = new HashSet<String>();
                String[] ss = s.split("\n");
                Collections.addAll(hs, ss);
                return hs;
            }
            return null;
        }
    }

    private static void removeExternalMountsFromApp(Context context) {
        synchronized (mLock) {
            SharedPreferences sp = SharedPreferencesUtils.getMinJieKaiFaPreferences(context);
            sp.edit().remove(APP_KEY).commit();
        }
    }

    private static HashSet<String> readExternalMountsFromDrive() {
        final HashSet<String> out = new HashSet<String>();
        final boolean[] get_result = new boolean[]{false};
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                get_result[0] = false;
                String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
                String s = "";
                Process process = null;
                InputStream is = null;
                try {
                    process = new ProcessBuilder().command("mount")
                            .redirectErrorStream(true).start();
                    process.waitFor();
                    is = process.getInputStream();
                    final byte[] buffer = new byte[1024];
                    while (is.read(buffer) != -1) {
                        s = s + new String(buffer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    StreamUtil.CloseInputStream(is);
                    if (null != process) {
                        process.destroy();
                    }
                }
                if (!"".equals(s)) {
                    // parse output
                    get_result[0] = true;
                    final String[] lines = s.split("\n");
                    for (String line : lines) {
                        if (!line.toLowerCase(Locale.US).contains("asec")) {
                            if (line.matches(reg)) {
                                String[] parts = line.split(" ");
                                for (String part : parts) {
                                    if (part.startsWith("/")) {
                                        if (!part.toLowerCase(Locale.US).contains("vold")) {
                                            out.add(part);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        thread.setName("SDCardUtil.readExternalMountsFromDrive");
        thread.setDaemon(true);
        thread.setPriority(7);
        thread.start();
        try {
            thread.join(1000);
            if (!get_result[0]) {
                thread.interrupt();
                thread.stop();
                return null;
            } else {
                return out;
            }
        } catch (Exception e) {
            return null;
        }
    }


    public static List<String> getAllSDCardMounts(Context context) {
        if (null == all_sdcard_mounts) {
            HashSet<String> hList = getExternalMounts(context);
            ArrayList<String> list = new ArrayList<String>();
            if (null != hList) {
                for (String s : hList) {
                    list.add(s);
                }
                all_sdcard_mounts = list;
            }
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String mounted = Environment.getExternalStorageDirectory().getAbsolutePath();
                if (!list.contains(mounted)) {
                    list.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                }
                return list;
            }
        }
        return all_sdcard_mounts;
    }

    public static File getStoreDirectory(Context context) {
        if (null == store_directory) {
            HashSet<String> hList = getExternalMounts(context);
            if (null != hList && !hList.isEmpty()) {
                store_directory = hList.iterator().next();
            } else if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return Environment.getExternalStorageDirectory();
            }
        }
        if (null != store_directory) {
            return new File(store_directory);
        }
        return null;
    }

    public static interface SDCardListener {
        void onMounted(String sdcardPath);

        void onMountedFinish(String sdcardPath);

        void onRemoved();
    }

    public static void addSDCardListener(SDCardListener listener) {
        if (null == listener) {
            return;
        }
        listeners.add(listener);
    }

    public static void removeSDCardListener(SDCardListener listener) {
        if (null == listener) {
            return;
        }
        listeners.remove(listener);
    }

    public static String getWriteDir(Context context,String path) {
        File writeFileDir = getWriteFileDir(context,path);
        if (null != writeFileDir) {
            return writeFileDir.getAbsolutePath();
        } else {
            return null;
        }
    }

    public static File getWriteFileDir(Context context,String path) {
        String state = Environment.getExternalStorageState();
        Boolean isSdCardEnable = false;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            isSdCardEnable = true;
        }
        File fileDir = null;
        if (isSdCardEnable) {
            File sdCardDir = Environment.getExternalStorageDirectory();
            fileDir = new File(sdCardDir, path);
            if (fileDir.exists() || fileDir.mkdirs()) {
                if (!fileDir.canWrite()) {
                    fileDir = null;
                }
            } else {
                fileDir = null;
            }
        }
        if (null == fileDir) {
            fileDir = new File(context.getFilesDir(), path);
            if (fileDir.exists() || fileDir.mkdirs()) {
                if (!fileDir.canWrite()) {
                    fileDir = null;
                }
            } else {
                fileDir = null;
            }
        }
        return fileDir;
    }
}
