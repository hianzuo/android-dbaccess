package com.flyhand.yunpos.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-11-29
 * Time: 上午10:58
 */
public interface UtilNetworkCallback<T> {
    void callback(T t);

    void error(String msg);
}
