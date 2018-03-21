package com.flyhand.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Ryan on 14-9-23.
 */
public class ClassFieldChecker {

    public static boolean isAllNull(Object obj) {
        return isAllNull(ClassFieldFetcher.getFields(obj.getClass()), obj);
    }

    public static boolean isAllNull(List<Field> list, Object obj) {
        for (Field field : list) {
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!isStatic) {
                field.setAccessible(true);
                try {
                    Object val = field.get(obj);
                    if (null != val) {
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return true;
    }
}
