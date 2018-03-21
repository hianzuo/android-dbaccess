package com.flyhand.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.flyhand.core.utils.ClazzUtil;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-9-1
 * Time: 下午12:42
 */
public class ParcelableUtil {
    public static <T extends Parcelable> void writeToParcel(Parcel parcel, T obj) {
        try {
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                String fieldStr = field.toString();
                if (fieldStr.contains(" static ") || fieldStr.contains(" final ")) {
                    continue;
                }
                field.setAccessible(true);
                Class<?> type = field.getType();
                Object value = field.get(obj);
                if (Integer.class.equals(type) || "int".equals(type.getName())) {
                    if (null != value && value instanceof Integer) {
                        parcel.writeInt((Integer) value);
                    } else {
                        parcel.writeInt(0);
                    }
                } else if (String.class.equals(type)) {
                    if (null != value && value instanceof String) {
                        parcel.writeString((String) value);
                    } else {
                        parcel.writeString("");
                    }
                } else if (Long.class.equals(type) || "long".equals(type.getName())) {
                    if (null != value && value instanceof Long) {
                        parcel.writeLong((Long) value);
                    } else {
                        parcel.writeLong(0L);
                    }
                } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
                    if (null != value && value instanceof Boolean) {
                        byte bte = (byte) (((Boolean) value) ? 1 : 0);
                        parcel.writeByte(bte);
                    } else {
                        parcel.writeByte((byte) 0);
                    }
                } else if (Double.class.equals(type) || "double".equals(type.getName())) {
                    if (null != value && value instanceof Double) {
                        parcel.writeDouble((Double) value);
                    } else {
                        parcel.writeDouble(0d);
                    }
                } else if (Float.class.equals(type) || "float".equals(type.getName())) {
                    if (null != value && value instanceof Float) {
                        parcel.writeFloat((Float) value);
                    } else {
                        parcel.writeFloat(0f);
                    }
                } else if (BigDecimal.class.equals(type)) {
                    if (null != value && value instanceof BigDecimal) {
                        parcel.writeString(value.toString());
                    } else {
                        parcel.writeString("BigDecimal_NULL");
                    }
                } else if (type.isAssignableFrom(List.class)) {
                    if (null != value && value instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Parcelable> list = (List<Parcelable>) value;
                        parcel.writeTypedList(list);
                    } else {
                        parcel.writeString("BigDecimal_NULL");
                    }
                } else {
                    throw new RuntimeException("can't support for type " + type.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T extends Parcelable> T readFromParcel(Parcel parcel, T obj) {
        try {
            Field[] fields = obj.getClass().getFields();
            for (Field field : fields) {
                String fieldStr = field.toString();
                if (fieldStr.contains(" static ") || fieldStr.contains(" final ")) {
                    continue;
                }
                field.setAccessible(true);
                Class<?> type = field.getType();
                if (Integer.class.equals(type) || "int".equals(type.getName())) {
                    field.set(obj, parcel.readInt());
                } else if (String.class.equals(type)) {
                    field.set(obj, parcel.readString());
                } else if (Long.class.equals(type) || "long".equals(type.getName())) {
                    field.set(obj, parcel.readLong());
                } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
                    field.set(obj, (parcel.readByte() == 1));
                } else if (Double.class.equals(type) || "double".equals(type.getName())) {
                    field.set(obj, parcel.readDouble());
                } else if (Float.class.equals(type) || "float".equals(type.getName())) {
                    field.set(obj, parcel.readFloat());
                } else if (BigDecimal.class.equals(type)) {
                    String value = parcel.readString();
                    if ("BigDecimal_NULL".equals(value)) {
                        field.set(obj, null);
                    } else {
                        field.set(obj, new BigDecimal(value));
                    }
                } else if (type.isAssignableFrom(List.class)) {
                    Class<? extends Parcelable> genericType = getFirstGenericType(field);
                    //noinspection unchecked
                    Parcelable.Creator<Parcelable> creator = (Parcelable.Creator<Parcelable>)
                            genericType.getField("CREATOR").get(null);
                    List<Parcelable> list = new ArrayList<Parcelable>();
                    parcel.readTypedList(list, creator);
                    field.set(obj, list);
                } else {
                    throw new RuntimeException("can't support for type " + type.getName());
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T extends Parcelable> T readFromParcel(Parcel parcel, Class<? extends T> clz) {
        try {
            T obj = ClazzUtil.newInstance(clz);
            return readFromParcel(parcel, obj);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private static Class<? extends Parcelable> getFirstGenericType(Field field) {
        Type genericFieldType = field.getGenericType();
        Class<? extends Parcelable> fieldArgClass = null;
        if (genericFieldType instanceof ParameterizedType) {
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type fieldArgType = aType.getActualTypeArguments()[0];
            //noinspection unchecked
            fieldArgClass = (Class<? extends Parcelable>) fieldArgType;
        }
        if (null == fieldArgClass) {
            throw new RuntimeException("the field is not generic type");
        } else {
            return fieldArgClass;
        }
    }
}
