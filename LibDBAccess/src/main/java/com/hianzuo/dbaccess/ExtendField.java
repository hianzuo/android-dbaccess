package com.hianzuo.dbaccess;

import com.hianzuo.dbaccess.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Ryan
 * On 2017/6/26.
 */

public abstract class ExtendField {

    private HashMap<String, String> mExtendFieldMap;

    protected HashMap<String, String> getExtendFieldsMap() {
        if (null == mExtendFieldMap) {
            mExtendFieldMap = new HashMap<>();
            String extendFieldJson = getExtendFields();
            if (StringUtil.isNotEmpty(extendFieldJson)) {
                try {
                    JSONObject jo = new JSONObject(extendFieldJson);
                    Iterator<String> keys = jo.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        mExtendFieldMap.put(key, jo.getString(key));
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return mExtendFieldMap;
    }

    public boolean hasExtendField(String name) {
        return getExtendFieldsMap().containsKey(name);
    }

    private String __getExtendField(String name) {
        return getExtendFieldsMap().get(name);
    }

    private static final String DEF_NULL = "[NULL]";

    public String getExtendField(String name) {
        String fieldValue = getExtendField(name, DEF_NULL);
        if (DEF_NULL.equals(fieldValue)) {
            return null;
        } else {
            return fieldValue;
        }
    }

    public String getExtendField(String name, String def) {
        String ret = __getExtendField(name);
        if (StringUtil.isEmpty(ret)) {
            ret = null;
        }
        if (null != ret) {
            return ret;
        }
        if (null != def) {
            return def;
        }
        throw new RuntimeException(name + " extend value is empty");
    }

    public boolean getExtendFieldBoolean(String name) {
        return getExtendFieldBoolean(name, null);
    }

    public boolean getExtendFieldBoolean(String name, Boolean def) {
        Boolean ret = null;
        RuntimeException throwable = null;
        try {
            ret = Boolean.valueOf(__getExtendField(name));
        } catch (RuntimeException e) {
            throwable = e;
        }
        if (null != ret) {
            return ret;
        }
        if (null != def) {
            return def;
        }
        throw throwable;
    }

    public BigDecimal getExtendFieldBigDecimal(String name) {
        return getExtendFieldBigDecimal(name, null);
    }

    public BigDecimal getExtendFieldBigDecimal(String name, BigDecimal def) {
        BigDecimal ret = null;
        RuntimeException throwable = null;
        try {
            ret = new BigDecimal(__getExtendField(name));
        } catch (RuntimeException e) {
            throwable = e;
        }
        if (null != ret) {
            return ret;
        }
        if (null != def) {
            return def;
        }
        throw throwable;
    }

    public void addExtendField(String key, String value) {
        if (null == key || null == value) {
            return;
        }
        HashMap<String, String> fieldsMap = getExtendFieldsMap();
        fieldsMap.put(key, value);
        setExtendFields(new JSONObject(fieldsMap).toString());
    }

    public void addExtendField(HashMap<String, String> fields) {
        if (null == fields) {
            return;
        }
        for (String key : fields.keySet()) {
            addExtendField(key, fields.get(key));
        }
    }

    public String getExtendFields() {
        return null;
    }

    public void setExtendFields(String extendFields) {
    }
}
