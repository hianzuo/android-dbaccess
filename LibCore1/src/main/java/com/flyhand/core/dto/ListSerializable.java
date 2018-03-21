package com.flyhand.core.dto;

import java.io.Serializable;
import java.util.List;

/**
 * User: Ryan
 * Date: 14-4-9
 * Time: 下午5:22
 */
public class ListSerializable<T extends Serializable> implements Serializable {
    private List<T> list;

    public ListSerializable(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }
}
