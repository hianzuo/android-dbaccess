package com.flyhand.lang;

/**
 * On 2016/11/16.
 *
 * @author Ryan
 */

public class RefInteger {
    private Integer value;

    public RefInteger(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
