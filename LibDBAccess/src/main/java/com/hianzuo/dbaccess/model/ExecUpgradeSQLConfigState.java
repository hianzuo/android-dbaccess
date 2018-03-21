package com.hianzuo.dbaccess.model;

/**
 * Created by Ryan
 * On 2017/4/6.
 */

public abstract class ExecUpgradeSQLConfigState {
    public static final int STATE_WAITING = 1;  //待升级
    public static final int STATE_DONE = 32;      //升级完成

}
