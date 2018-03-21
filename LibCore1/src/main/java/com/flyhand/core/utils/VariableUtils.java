package com.flyhand.core.utils;

/**
 * @author Ryan
 * @date 2017/10/24.
 */

public class VariableUtils {
    public static String getUnderLineStyleFiledName(String name) {
        StringBuilder sb = new StringBuilder();
        for (char c : name.toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                if (sb.length() > 0) {
                    sb.append('_');
                }
                sb.append((char) (c + 32));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String getHumpStyleFiledName(String name) {
        return getHumpStyleFiledName(name, false);
    }

    public static String getHumpStyleFiledName(String name, boolean lowerFirstChar) {
        StringBuilder sb = new StringBuilder();
        boolean underLine = true;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                underLine = true;
            } else {
                sb.append(c);
                if (c >= 'a' && c <= 'z') {
                    if (underLine) {
                        sb.deleteCharAt(sb.length() - 1).append((char) (c - 32));
                    }
                }
                underLine = false;
            }
        }
        if (lowerFirstChar && sb.length() > 0) {
            char c = sb.charAt(0);
            if (c > 'A' && c <= 'Z') {
                sb.setCharAt(0, (char) (c + 32));
            }
        }
        return sb.toString();
    }
}
