package com.hianzuo.dbaccess.config;

/**
 * User: Ryan
 * Date: 14-4-2
 * Time: 下午7:35
 */
public enum UpdateTableMethod {
    /**
     * 根据表的版本号检查更新(推荐使用，在更改了表结构后修改) @see @Table(ver=1),
     */
    VERSION,
    /**
     * 自动检查Dto有没有更新，如果有，就更新数据库表
     */
    AUTO_CHECK,
    /**
     * 手动更新， 最原始的方式
     */
    MANUAL
}
