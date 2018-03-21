package com.flyhand.core.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Ryan on 14-9-23.
 */
public class ClassNullFieldCreator {
    private static String abc;

    public static <T> T create(Class<T> clz) {
        return create(null, clz);
    }

    public static <T> T create(List<Field> fields, Class<T> clz) {
        T t = ClazzUtil.newInstance(clz);
        if (null == fields) {
            fields = ClassFieldFetcher.getFields(clz);
        }
        for (Field field : fields) {
            try {
                Class<?> type = field.getType();
                if (!type.isPrimitive()) {
                    boolean isStatic = Modifier.isStatic(field.getModifiers());
                    boolean isFinal = Modifier.isFinal(field.getModifiers());
                    if (!isStatic && !isFinal) {
                        field.setAccessible(true);
                        field.set(t, null);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException("field:" + field.getName(), e);
            }
        }
        return t;
    }

}
