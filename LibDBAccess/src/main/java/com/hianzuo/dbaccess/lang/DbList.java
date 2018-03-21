package com.hianzuo.dbaccess.lang;

import android.support.annotation.NonNull;

import com.flyhand.core.utils.TypeToken;
import com.google.gson.Gson;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ryan
 * @date 2018/2/26.
 */
public class DbList<E> implements Iterable<E>, Serializable {
    private List<E> mList;
    private String mDataStr;
    private transient Type mGenericType;

    public DbList() {
        mGenericType = getClass().getGenericSuperclass();
    }

    public DbList(List<E> list) {
        this();
        this.mList = list;
    }

    public DbList(Type genericType, String mDataStr) {
        this();
        this.mGenericType = genericType;
        this.mDataStr = mDataStr;
    }

    public List<E> getList() {
        initList();
        return mList;
    }

    public String getDataStr() {
        initDataStr();
        return mDataStr;
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        initList();
        return mList.iterator();
    }


    private static final Gson GSON = new Gson();

    private void initList() {
        if (null != mList) {
            return;
        }
        if (null == mDataStr || mDataStr.length() == 0) {
            mList = new ArrayList<>();
            return;
        }
        mList = GSON.fromJson(mDataStr, TypeToken.getListType(mGenericType));
        mDataStr = null;
    }


    private void initDataStr() {
        if (null != mDataStr) {
            return;
        }
        if (null == mList) {
            return;
        }
        mDataStr = GSON.toJson(mList);
        mList = null;
    }

    public void add(E item) {
        getList().add(item);
    }

    public boolean isEmpty() {
        return getList().isEmpty();
    }


    public static <T> void sort(DbList<T> list, Comparator<? super T> c) {
        Collections.sort(list.getList(), c);
    }

    public int size() {
        return getList().size();
    }
}
