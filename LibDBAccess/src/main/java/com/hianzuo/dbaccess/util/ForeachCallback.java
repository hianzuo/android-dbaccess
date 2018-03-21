package com.hianzuo.dbaccess.util;

import com.hianzuo.dbaccess.Database;

/**
 * Created by Ryan
 * On 2017/2/27.
 */

public interface ForeachCallback<T> {
    void callback(Database db,T t);
}
