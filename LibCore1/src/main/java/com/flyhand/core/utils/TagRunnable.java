package com.flyhand.core.utils;

/**
 * Created by Ryan
 * Date: 11-9-24  Afternoon 3:11
 */
public abstract class TagRunnable<T> implements Runnable {
    T tag;

    public TagRunnable setTag(T tag) {
        this.tag = tag;
        return this;
    }

    public T getTag() {
        return tag;

    }
}
