package com.hianzuo.dbaccess.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * User: Ryan
 * Date: 14-3-21
 * Time: 上午11:08
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 字段唯一值， 方便更新数据列名等
     * 为了能够在数据结构更新的时候，
     * 能找到相应字段，这个值定义了就不能变换，
     * 而且不能和表中其他字段重复
     *
     * @return 字段唯一标示
     */
    float id();

    String name() default "";

    String type() default "";

    int len() default 0;

    boolean pk() default false;

    boolean aicr() default false;

    boolean canull() default false;

    boolean unique() default false;

    String check() default "";

    String def() default "null";


}

