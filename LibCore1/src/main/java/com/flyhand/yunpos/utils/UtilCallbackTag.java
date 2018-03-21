package com.flyhand.yunpos.utils;

/**
 * User: Ryan
 * Date: 13-11-29
 * Time: 上午10:58
 */
public abstract class UtilCallbackTag<T, P> implements UtilCallback<T> {
    P p;

    public UtilCallbackTag() {
    }

    public UtilCallbackTag(P p) {
        this.p = p;
    }

    public void setParam(P p) {
        this.p = p;
    }

    @Override
    public void callback(T t) {

    }

    public P getParam() {
        return p;
    }
}
