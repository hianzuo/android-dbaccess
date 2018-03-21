package com.flyhand.core.utils;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ryan
 * On 14-9-23.
 */
public class ClassFieldFetcher {
    public static List<Field> getFields(Class<?> clazz) {
        List<Field> list = new ArrayList<Field>();
        Set<Field> set = new HashSet<Field>();
        return getFields(list, set, clazz);
    }
    public static Map<String,String> getFieldValueMap(Object obj) {
        HashMap<String,String> map = new HashMap<>();
        if(null == obj) {
            return map;
        }
        List<Field> fields = ClassFieldFetcher.getFields(obj.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            String value = null;
            try {
                value = field.get(obj).toString();
            } catch (Exception ignored) {
            }
            if (null != value) {
                map.put(name, value);
            }
        }
        return map;
    }

    private static List<Field> getFields(List<Field> list, Set<Field> set, Class<?> clazz) {
        if (null == clazz || Object.class.equals(clazz)) {
            return list;//最顶层了
        }
        Field[] declared = clazz.getDeclaredFields();
        for (int i = 0; i < declared.length; i++) {
            Field field = declared[declared.length - i - 1];
            boolean isStatic = Modifier.isStatic(field.getModifiers());
            if (!isStatic) {
                if (!set.contains(field)) {
                    list.add(field);
                    set.add(field);
                }
            }
        }
        return getFields(list, set, clazz.getSuperclass());
    }


}