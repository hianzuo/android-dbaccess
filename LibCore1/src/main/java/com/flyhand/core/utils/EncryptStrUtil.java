package com.flyhand.core.utils;

/**
 * Created by Ryan
 * On 2017/3/30.
 */

public class EncryptStrUtil {

    public static String name(String str, boolean encrypt) {
        if (encrypt) {
            return name(str);
        }
        return str;
    }

    public static String name(String str) {
        if (null != str && str.length() > 1) {
            if (str.length() == 2) {
                return str.substring(0, 1) + "*";
            } else {
                return number(str);
            }
        }
        return str;
    }


    public static String number(String number) {
        if (null != number && number.length() > 2) {
            int length = number.length() + 1;
            int encryptLen = length / 3 + length % 3;
            int start = length / 3;
            return number.substring(0, start) +
                    encryptChar('*', encryptLen) +
                    number.substring(start + encryptLen);
        }
        return number;
    }


    private static String encryptChar(char c, int encryptLen) {
        String chars = "";
        for (int i = 0; i < encryptLen; i++) {
            chars += c;
        }
        return chars;
    }

}
