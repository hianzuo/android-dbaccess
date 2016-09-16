package com.hianzuo.dbaccess.inject;

import java.lang.annotation.*;

/**
 * User: Ryan
 * Date: 14-3-21
 * Time: 上午11:05
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Table {
    /**
     * 表名字，如果返回空值，就使用类的名字做为表名字
     *
     * @return 表名字
     */
    String name() default "";

    /**
     * @return 版本号
     */
    int ver() default 1;

    /**
     * 当新加入增表字段的时候，清除表数据
     * <p/>
     * 因为某些表的数据是以服务器端为准的，
     * 因为我们根据最后更新时间来更新数据，
     * 所以，新增的字段的数据始终没有下载。
     * <p/>
     * 如果clearOnAddColumn的值为真的话，
     * 那么就将在新增字段的时候，会清除表的数据
     * 来保证新增的字段也会下载到服务器端的数据
     *
     * @return 是否在新增字段的时候清除数据
     */
    boolean clearOnAddColumn() default false;

}

