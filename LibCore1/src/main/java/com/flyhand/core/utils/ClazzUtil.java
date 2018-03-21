package com.flyhand.core.utils;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import dalvik.system.DexFile;

/**
 * On 2017/3/22.
 * @author Ryan
 */

public class ClazzUtil {
    public static <T> T newInstance(Class<T> clz) {
        try {
            Constructor<T> defCons = null;
            try {
                defCons = clz.getDeclaredConstructor();
            } catch (Exception ignored) {
            }
            if (null == defCons) {
                defCons = clz.getConstructor();
            }
            defCons.setAccessible(true);
            return defCons.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取该包下
     * @param context
     * @param pkg
     * @return
     */
    public static List<Class<?>> allInPkg(Context context, String pkg) {
        DexFile dexFile = null;
        List<Class<?>> list = new ArrayList<>();
        try {
            dexFile = new DexFile(context.getPackageCodePath());
            ClassLoader classLoader = context.getClassLoader();
            for (Enumeration<String> iterator = dexFile.entries(); iterator.hasMoreElements(); ) {
                String s = iterator.nextElement();
                if (s.startsWith(pkg)) {
                    Class<?> c;
                    try {
                        //noinspection unchecked
                        c = classLoader.loadClass(s);
                    } catch (Exception e) {
                        continue;
                    }
                    if (!Modifier.isAbstract(c.getModifiers())) {
                        list.add(c);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (null != dexFile) {
                try {
                    dexFile.close();
                } catch (Exception ignored) {
                }
            }
        }
        return list;
    }
}
