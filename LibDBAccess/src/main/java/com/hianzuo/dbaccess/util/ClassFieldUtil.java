package com.hianzuo.dbaccess.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ryan on 14/11/18.
 */
public class ClassFieldUtil {
    private final static Map<Class<?>, List<Field>>
            mCacheColumnFields = new ConcurrentHashMap<Class<?>, List<Field>>();

    public static void clearCache() {
        mCacheColumnFields.clear();
    }

    public static void clearCache(Class<?> clz) {
        mCacheColumnFields.remove(clz);
    }

    public static List<Field> getFields(Class<?> clz) {
        return __getFields(clz, null, null);
    }

    public static List<Field> getAccessibleFields(Class<?> clz) {
        return __getFields(clz, true, null);
    }

    public static List<Field> getFields(Class<?> clz, Filter filter) {
        return __getFields(clz, null, filter);
    }

    private static List<Field> __getFields(Class<?> clz, Boolean accessible, Filter filter) {
        List<Field> columnFields = __getColumnFields(clz);
        List<Field> result = new ArrayList<Field>();
        for (Field field : columnFields) {
            if (null != accessible && accessible) {
                field.setAccessible(true);
            }
            if (null == filter || filter.filter(field)) {
                result.add(field);
            }
        }
        return result;
    }

    private static List<Field> __getColumnFields(Class<?> clz) {
        synchronized (mCacheColumnFields) {
            List<Field> list = mCacheColumnFields.get(clz);
            if (null != list && !list.isEmpty()) {
                return list;
            } else {
                LinkedList<Field> fields = new LinkedList<Field>();
                __getAllClassFields(fields, clz);
                mCacheColumnFields.put(clz, fields);
                return fields;
            }
        }
    }

    private static void __getAllClassFields(LinkedList<Field> allFields, Class<?> clazz) {
        if (null == clazz || Object.class.equals(clazz)) {
            //最顶层了
        } else {
            Field[] declared = clazz.getDeclaredFields();
            for (int i = 0; i < declared.length; i++) {
                Field field = declared[declared.length - i - 1];
                if (!__containsFields(allFields, field)) {
                    allFields.push(field);
                }
            }
            __getAllClassFields(allFields, clazz.getSuperclass());
        }
    }

    private static boolean __containsFields(LinkedList<Field> allFields, Field field) {
        for (Field allField : allFields) {
            if (field.getName().equals(allField.getName())) {
                return true;
            }
        }
        return false;
    }

    public static class Filter {
        boolean filter(Field field) {
            return true;
        }
    }
}
