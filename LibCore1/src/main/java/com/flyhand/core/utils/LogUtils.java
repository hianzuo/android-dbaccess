package com.flyhand.core.utils;

import com.hianzuo.logger.Log;

/**
 * User: Ryan
 * Date: 11-10-13
 * Time: Afternoon 7:11
 */
public class LogUtils {
    public static boolean isPrint = true;
    //    public static boolean isPrint = Config.DEV;
    public static final boolean SEND_EXCUSE_STATE_EMAIL = false;

    public static void log(String s) {
        if (isPrint) {
            System.out.println("Mjkf%:" + Thread.currentThread().getStackTrace()[3] + " %LogMessage% " + s);
        }
    }

    public static void debug(String s) {
        if (isPrint) {
            Log.d("XGN", s);
        }
    }

    public static void log() {
        if (isPrint) {
            System.out.println("Mjkf:" + Thread.currentThread().getStackTrace()[3]);
        }
    }

    /*public static void jy_main(String args[]){
        log();
    }*/
    private static long st = 0;

    public static void markTime() {
        st = System.currentTimeMillis();
    }

    public static void printTime(String s) {
        System.out.println(s + " printTime: " + (System.currentTimeMillis() - st));
        markTime();
    }
}
