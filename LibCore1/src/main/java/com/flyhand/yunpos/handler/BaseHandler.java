package com.flyhand.yunpos.handler;

import com.flyhand.core.activity.ExActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-12-25
 * Time: 下午4:25
 */
public class BaseHandler {
    protected ExActivity activity;

    public BaseHandler() {
    }

    public BaseHandler(ExActivity activity) {
        this.activity = activity;
    }

    public void setActivity(ExActivity activity) {
        this.activity = activity;
    }

    protected int getRID(String name) {
        return activity.getRID(name);
    }

    protected int getRLayoutID(String name) {
        return activity.getRLayoutID(name);
    }

    protected int getRDrawableID(String name) {
        return activity.getRDrawableID(name);
    }

    protected int getRXmlID(String name) {
        return activity.getRXmlID(name);
    }

    protected int getRRawID(String name) {
        return activity.getRRawID(name);
    }

    protected int getRArrayID(String name) {
        return activity.getRArrayID(name);
    }

    protected int getRColorID(String name) {
        return activity.getRColorID(name);
    }

    protected int getRAnimID(String name) {
        return activity.getRAnimID(name);
    }

    protected int getRMenuID(String name) {
        return activity.getRMenuID(name);
    }

    public ExActivity getExActivity(){
        return activity;
    }
}
