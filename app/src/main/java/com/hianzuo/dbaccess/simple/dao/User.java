package com.hianzuo.dbaccess.simple.dao;

import com.hianzuo.dbaccess.Dto;
import com.hianzuo.dbaccess.inject.Column;
import com.hianzuo.dbaccess.inject.Table;

/**
 * Created by Ryan
 * On 2016/5/29.
 */
@Table(ver = 1)
public class User extends Dto {
    @Column(id = 10)
    private String name;
    @Column(id = 11)
    private Integer age;
    @Column(id = 12,canull = true)
    private String category;

    public User() {
    }

    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", category='" + category + '\'' +
                '}';
    }
}
