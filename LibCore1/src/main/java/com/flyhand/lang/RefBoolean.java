package com.flyhand.lang;

/**
 * Created by Ryan
 * On 2016/11/16.
 */

public class RefBoolean {
    private boolean value;

    public RefBoolean(boolean value) {
        this.value = value;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }
}
