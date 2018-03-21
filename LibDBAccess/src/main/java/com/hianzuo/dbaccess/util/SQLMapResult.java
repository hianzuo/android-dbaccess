package com.hianzuo.dbaccess.util;

import java.util.Map;

/**
 * Created by Ryan
 * On 2017/6/28.
 */

public class SQLMapResult {
    Map<String, Object> data;

    public SQLMapResult(Map<String, Object> data) {
        this.data = data;
    }

    public int getInt(String key) {
        return getInt(key, null);
    }


    public int getInt(String key, Integer def) {
        Object o = data.get(key);
        if (null != o) {
            if (o instanceof Integer) {
                return (int) o;
            } else {
                return Integer.valueOf(o.toString());
            }
        }
        if (null != def) {
            return def;
        }
        throw new NullPointerException("key " + key + " not exist.");
    }


    public String getString(String key) {
        return getString(key, null);
    }

    public String getString(String key, String def) {
        Object o = data.get(key);
        if (null != o) {
            if (o instanceof String) {
                return (String) o;
            } else {
                return o.toString();
            }
        }
        if (null != def) {
            return def;
        }
        throw new NullPointerException("key " + key + " not exist.");
    }
}
