package com.flyhand.yunpos.http;

import org.apache.http.NameValuePair;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.LangUtils;

/**
 * Created by Ryan
 * On 15/11/20.
 */
public class BasicNameValuePair implements NameValuePair, com.flyhand.yunpos.http.NameValuePair, Cloneable {
    private String name;
    private String value;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    public BasicNameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public BasicNameValuePair(String name, Long value) {
        this.name = name;
        this.value = null == value ? null : String.valueOf(value);
    }

    public BasicNameValuePair(String name, boolean value) {
        this.name = name;
        this.value = Boolean.toString(value);
    }

    public BasicNameValuePair(String name, Integer value) {
        this.name = name;
        this.value = null == value ? null : String.valueOf(value);
    }

    // don't call complex default formatting for a simple toString
    @Override
    public String toString() {
        if (this.value == null) {
            return name;
        } else {
            int len = this.name.length() + 1 + this.value.length();
            CharArrayBuffer buffer = new CharArrayBuffer(len);
            buffer.append(this.name);
            buffer.append("=");
            buffer.append(this.value);
            return buffer.toString();
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (object == null) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            BasicNameValuePair that = (BasicNameValuePair) object;
            return this.name.equals(that.name)
                    && LangUtils.equals(this.value, that.value);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.name);
        hash = LangUtils.hashCode(hash, this.value);
        return hash;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
