package com.flyhand.core.utils;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ryan
 * Date: 12-3-31
 * Time: 上午11:51
 */
public class JsonUtil {

    public static <T> ArrayList<T> from(String result, Class<T> t) throws JSONException {
        if (JsonUtil.isJsonArray(result)) {
            return fromJsonArray(t, result);
        } else {
            return fromJsonObject(t, result);
        }
    }

    public static <T> ArrayList<T> fromJsonObject(Class<T> t, String result) throws JSONException {
        JSONObject json = new JSONObject(result);
        return fromJsonObject(t, json);
    }

    public static <T> ArrayList<T> fromJsonObject(Class<T> t, JSONObject json) {
        try {
            final T obj = ClazzUtil.newInstance(t);
            Field[] fields = t.getFields();
            for (Field field : fields) {
                if (json.has(field.getName())) {
                    try {
                        field.setAccessible(true);
                        field.set(obj, getValueFromJson(field.getName(), field, json));
                    } catch (Exception e) {
                        //
                    }
                }
            }
            return new ArrayList<T>() {
                {
                    add(obj);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("can't instance class [" + t.getName() + "]");
        }
    }

    private static <T> ArrayList<T> fromJsonArray(Class<T> t, String result) throws JSONException {
        JSONArray jsonArray = new JSONArray(result);
        return fromJsonArray(t, jsonArray);
    }

    public static <T> ArrayList<T> fromJsonArray(Class<T> t, JSONArray jsonArray) {
        int count = jsonArray.length();
        if (count > 0) {
            ArrayList<T> profiles = new ArrayList<T>();
            for (int i = 0; i < count; i++) {
                T obj;
                try {
                    obj = ClazzUtil.newInstance(t);
                    JSONObject json = jsonArray.getJSONObject(i);
                    Field[] fields = obj.getClass().getFields();
                    for (Field field : fields) {
                        if (json.has(field.getName())) {
                            Object value = null;
                            try {
                                field.setAccessible(true);
                                value = getValueFromJson(field.getName(), field, json);
                                field.set(obj, value);
                            } catch (Exception e) {
                                Log.e("JsonUtil", "field:" + field.getName() + " value:" + value ,e);
                            }
                        }
                    }
                    profiles.add(obj);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return profiles;
        } else {
            return new ArrayList<T>();
        }
    }

    private static Object getValueFromJson(String name, Field field, JSONObject json) throws JSONException {
        try {
            Class<?> type = field.getType();
            if (Integer.class.equals(type) || "int".equals(type.getName())) {
                return json.getInt(name);
            } else if (String.class.equals(type)) {
                return json.getString(name);
            } else if (Long.class.equals(type) || "long".equals(type.getName())) {
                return json.getLong(name);
            } else if (Boolean.class.equals(type) || "boolean".equals(type.getName())) {
                return json.getBoolean(name);
            } else if (Double.class.equals(type) || "double".equals(type.getName())) {
                return json.getDouble(name);
            } else if (type.isAssignableFrom(List.class)) {
                Object obj = json.get(name);
                if (null != obj && obj instanceof JSONArray) {
                    ParameterizedType pt = (ParameterizedType) field.getGenericType();
                    Class<?> gType = (Class<?>) pt.getActualTypeArguments()[0];
                    return fromJsonArray(gType, (JSONArray) obj);
                }
            }
            return json.get(name);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isJsonArray(String result) {
        if (null == result) {
            return false;
        }
        String temp = result.trim();
        return temp.startsWith("[") && temp.endsWith("]");
    }

    public static boolean isJsonObject(String result) {
        if (null == result) {
            return false;
        }
        String temp = result.trim();
        return temp.startsWith("{") && temp.endsWith("}");
    }

    public static boolean isJsonResult(String result) {
        return isJsonArray(result) || isJsonObject(result);
    }

    public static boolean hasError(String result) {
        if (null == result) {
            return true;
        } else if (result.trim().contains("\"errCode\":")) {
            return true;
        }
        if (result.trim().contains("\"err_code\":")) {
            return true;
        } else {
            return !JsonUtil.isJsonResult(result);
        }
    }


}
