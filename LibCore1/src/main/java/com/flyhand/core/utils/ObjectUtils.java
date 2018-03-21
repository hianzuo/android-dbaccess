package com.flyhand.core.utils;

/**
 * Created by Ryan
 * On 2017/4/20.
 */

public class ObjectUtils {
    public static boolean equals(Object a, Object b) {
        return eq(a, b);
    }

    public static boolean eq(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public static boolean notEq(Object a, Object b) {
        return !eq(a, b);
    }
}
