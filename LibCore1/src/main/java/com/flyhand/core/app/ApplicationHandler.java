package com.flyhand.core.app;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-6-7
 * Time: 上午9:42
 */
public abstract class ApplicationHandler {

    public abstract void onCreate(AbstractCoreApplication application);

    public abstract String getApplicationResource(String key);

    public abstract void putForwardParams(String key, Object param);

    public abstract Object takeForwardParams(String key);
}
