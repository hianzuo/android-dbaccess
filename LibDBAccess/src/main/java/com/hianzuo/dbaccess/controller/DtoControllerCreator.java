package com.hianzuo.dbaccess.controller;

import com.hianzuo.dbaccess.Dto;

/**
 * User: Ryan
 * Date: 14-3-25
 * Time: 上午11:39
 */
public class DtoControllerCreator{
    public <T extends Dto> DtoController<T> create(Class<T> clz) {
        return new DtoController<T>(clz);
    }
}
