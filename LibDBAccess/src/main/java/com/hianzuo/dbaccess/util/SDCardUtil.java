package com.hianzuo.dbaccess.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 12-12-6
 * Time: Afternoon 3:15
 */
public class SDCardUtil {

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
