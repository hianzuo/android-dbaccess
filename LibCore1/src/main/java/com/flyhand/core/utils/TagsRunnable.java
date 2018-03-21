package com.flyhand.core.utils;

/**
 * User: Ryan
 * Date: 12-6-1
 * Time: Afternoon 5:26
 */
public abstract class TagsRunnable implements Runnable {
    private Object t;
    private Object t1;
    private Object t2;
    private Object t3;

    public <T> void setTag(T tag) {
        this.t = tag;
    }

    public <T> T getTag() {
        return (T) t;
    }

    public <T> void setTag1(T tag) {
        this.t1 = tag;
    }

    public <T> T getTag1() {
        return (T) t1;
    }

    public <T> void setTag2(T tag) {
        this.t2 = tag;
    }

    public <T> T getTag2() {
        return (T) t2;
    }

    public <T> void setTag3(T tag) {
        this.t3 = tag;
    }

    public <T> T getTag3() {
        return (T) t3;
    }
}