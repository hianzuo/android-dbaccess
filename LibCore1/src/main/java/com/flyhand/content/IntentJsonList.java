package com.flyhand.content;

import java.util.List;

/**
 * Created by Administrator on 2015/5/30.
 */
public class IntentJsonList<T extends IntentJson> implements IntentJson{
    private List<T> list;

    public IntentJsonList(List<T> list) {
        this.list = list;
    }

    public List<T> getList() {
        return list;
    }
}
