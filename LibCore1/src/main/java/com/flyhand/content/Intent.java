package com.flyhand.content;

import android.content.ComponentName;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;

import com.flyhand.core.utils.IOUtils;
import com.flyhand.core.utils.StringUtil;
import com.flyhand.core.utils.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan on 15/5/29.
 */
public class Intent {
    private android.content.Intent osIntent;
    private int $64K = 64 * 1024;
    private static final HashMap<String, Serializable> MAP = new HashMap<String, Serializable>();
    private static final String KEY__$ID = "__$ID";
    private String __$ID;
    private HashMap<String, Serializable> mSerMap;
    private HashMap<String, IntentJson> mJsonMap;
    private static final LinkedHashMap<Integer, CacheIntent> mCacheIntent = new LinkedHashMap<>();
    private final static int MAX_CACHE_SIZE = 50;


    public static final String ACTION_VIEW = android.content.Intent.ACTION_VIEW;
    public static final int FLAG_ACTIVITY_NEW_TASK = android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
    public static final String ACTION_GET_CONTENT = android.content.Intent.ACTION_GET_CONTENT;
    public static final String CATEGORY_OPENABLE = android.content.Intent.CATEGORY_OPENABLE;
    public static final String ACTION_BATTERY_CHANGED = android.content.Intent.ACTION_BATTERY_CHANGED;
    public static final String ACTION_BOOT_COMPLETED = android.content.Intent.ACTION_BOOT_COMPLETED;
    public static final String ACTION_TIME_TICK = android.content.Intent.ACTION_TIME_TICK;
    public static final String ACTION_PACKAGE_RESTARTED = android.content.Intent.ACTION_PACKAGE_RESTARTED;
    public static final String ACTION_MAIN = android.content.Intent.ACTION_MAIN;
    public static final String CATEGORY_HOME = android.content.Intent.CATEGORY_HOME;

    public Intent(String action, Uri uri) {
        this(new android.content.Intent(action, uri));
    }

    public Intent(Context packageContext, Class<?> cls) {
        this(new android.content.Intent(packageContext, cls));
    }


    public Intent() {
        this(new android.content.Intent());
    }

    public Intent(String action) {
        this(new android.content.Intent(action));
    }

    private Intent(android.content.Intent intent) {
        osIntent = intent;
        if (null != intent) {
            __$ID = intent.getStringExtra(KEY__$ID);
            if (StringUtil.isEmpty(__$ID)) {
                __$ID = UUID.randomUUID().toString();
                intent.putExtra(KEY__$ID, __$ID);
            }
        }
    }

    public static Intent create(android.content.Intent intent) {
        if (null == intent) {
            return new Intent();
        }
        CacheIntent cacheIntent = mCacheIntent.get(intent.hashCode());
        if (null != cacheIntent) {
            return cacheIntent.intent;
        }
        removeCacheIntentByExpireTime();
        removeCacheIntentByMaxCount();
        Intent itt = new Intent(intent);
        mCacheIntent.put(intent.hashCode(), new CacheIntent(itt));
        return itt;
    }

    private static void removeCacheIntentByMaxCount() {
        synchronized (mCacheIntent) {
            if (mCacheIntent.size() > MAX_CACHE_SIZE) {
                int i = 0;
                List<Integer> removeKeyList = new ArrayList<>();
                for (Integer key : mCacheIntent.keySet()) {
                    if (i > MAX_CACHE_SIZE) {
                        removeKeyList.add(key);
                    }
                    i++;
                }
                for (Integer key : removeKeyList) {
                    mCacheIntent.remove(key);
                }
            }
        }
    }

    private static void removeCacheIntentByExpireTime() {
        synchronized (mCacheIntent) {
            List<Integer> removeKeyList = new ArrayList<>();
            for (Integer key : mCacheIntent.keySet()) {
                CacheIntent cacheIntent = mCacheIntent.get(key);
                if (cacheIntent.isExpire()) {
                    removeKeyList.add(key);
                }
            }
            for (Integer key : removeKeyList) {
                mCacheIntent.remove(key);
            }
        }
    }

    public Intent setAction(String action) {
        osIntent.setAction(action);
        return this;
    }

    public Intent putExtraStore(String name, IntentJson val) {
        String key = IntentJsonUtil.store(val);
        if (null == mJsonMap) {
            mJsonMap = new HashMap<>();
        }
        mJsonMap.put(key, val);
        osIntent.putExtra(name, key);
        return this;
    }

    public <T extends IntentJson> T getIntentJson(String name, Type type) {
        String key = osIntent.getStringExtra(name);
        if (StringUtil.isEmpty(key)) {
            return null;
        }
        if (null == mJsonMap) {
            mJsonMap = new HashMap<>();
        }
        @SuppressWarnings("unchecked")
        T t = (T) mJsonMap.get(key);
        if (null == t) {
            t = IntentJsonUtil.read(key, type);
            mJsonMap.put(key, t);
        }
        if (t== IntentJsonUtil.NULL_VALUE) {
            return null;
        }
        return t;
    }


    public <T extends IntentJson> Intent putExtraStore(String name, List<T> list) {
        IntentJsonList t = new IntentJsonList<T>(list);
        return putExtraStore(name, t);
    }

    public <T extends IntentJson> List<T> getListIntentJson(String name, Class<T> type) {
        IntentJsonList<T> jsonList = getIntentJson(name, TypeToken.get(IntentJsonList.class, type));
        return jsonList.getList();
    }


    public Intent putExtra(String name, Serializable ser) {
        Serializable val = copySerializable(ser);
        osIntent.putExtra(name, createSerValue(name, val));
        if (null == mSerMap) {
            mSerMap = new HashMap<>();
        }
        mSerMap.put(name, val);
        return this;
    }

    public Serializable getSerializableExtra(String name) {
        String key = getKey(name);
        Serializable ser = null;
        if (null != mSerMap) {
            ser = mSerMap.get(key);
        }
        if (null == ser) {
            ser = MAP.remove(key);
            if (null == mSerMap) {
                mSerMap = new HashMap<>();
            }
            mSerMap.put(key, ser);
        }
        return ser;
    }

    public Intent putExtra(String name, boolean value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, byte value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, char value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, short value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, int value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, long value) {
        osIntent.putExtra(name, value);
        return this;
    }

    public Intent putExtra(String name, float value) {
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, double value) {
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, String value) {
        if (checkLargeSize(value)) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, boolean[] value) {
        if (value.length > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, byte[] value) {
        if (value.length > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, short[] value) {
        if (value.length * 3 > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, char[] value) {
        if (value.length > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, int[] value) {
        if (value.length * 2 > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, long[] value) {
        if (value.length * 4 > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, float[] value) {
        if (value.length * 2 > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, double[] value) {
        if (value.length * 8 > $64K) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }


    public Intent putExtra(String name, String[] value) {
        if (checkLargeSize(value)) {
            throw new ContentToLargeException("");
        }
        osIntent.putExtra(name, value);
        return this;
    }

    private boolean checkLargeSize(String... value) {
        int size = 0;
        for (String s : value) {
            size += s.length();
            if (size > $64K) {
                return true;
            }
        }
        return false;
    }

    private String createSerValue(String name, Serializable val) {
        String key = getKey(name);
        if (MAP.containsKey(key)) {
            throw new KeyExistInIntentException("the key[" + name + "] is contains.");
        }
        MAP.put(key, val);
        return key;
    }

    private String getKey(String name) {
        return __$ID + "#ser_" + name;
    }

    public Intent setClass(Context context, Class<?> cls) {
        osIntent.setClass(context, cls);
        return this;
    }

    public android.content.Intent get() {
        return osIntent;
    }

    public Uri getData() {
        return osIntent.getData();
    }

    public Intent putExtra(String name, Uri uri) {
        osIntent.putExtra(name, uri);
        return this;
    }

    public Intent addCategory(String category) {
        osIntent.addCategory(category);
        return this;
    }

    public Intent setClassName(String packageName, String className) {
        osIntent.setClassName(packageName, className);
        return this;
    }

    public Intent setComponent(ComponentName component) {
        osIntent.setComponent(component);
        return this;
    }

    public static Intent createChooser(android.content.Intent intent, CharSequence title) {
        return new Intent(android.content.Intent.createChooser(intent, title));
    }

    public static Intent createChooser(Intent intent, CharSequence title) {
        return new Intent(android.content.Intent.createChooser(intent.get(), title));
    }

    public Intent setType(String type) {
        osIntent.setType(type);
        return this;
    }

    public Intent setDataAndType(Uri data, String type) {
        osIntent.setDataAndType(data, type);
        return this;
    }

    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return osIntent.getBooleanExtra(name, defaultValue);
    }

    public byte getByteExtra(String name, byte defaultValue) {
        return osIntent.getByteExtra(name, defaultValue);
    }

    public short getShortExtra(String name, short defaultValue) {
        return osIntent.getShortExtra(name, defaultValue);
    }

    public char getCharExtra(String name, char defaultValue) {
        return osIntent.getCharExtra(name, defaultValue);
    }

    public int getIntExtra(String name, int defaultValue) {
        return osIntent.getIntExtra(name, defaultValue);
    }

    public long getLongExtra(String name, long defaultValue) {
        return osIntent.getLongExtra(name, defaultValue);
    }

    public float getFloatExtra(String name, float defaultValue) {
        return osIntent.getFloatExtra(name, defaultValue);
    }

    public double getDoubleExtra(String name, double defaultValue) {
        return osIntent.getDoubleExtra(name, defaultValue);
    }

    public String getStringExtra(String name) {
        return osIntent.getStringExtra(name);
    }

    public CharSequence getCharSequenceExtra(String name) {
        return osIntent.getCharSequenceExtra(name);
    }

    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        return osIntent.getIntegerArrayListExtra(name);
    }

    public ArrayList<String> getStringArrayListExtra(String name) {
        return osIntent.getStringArrayListExtra(name);
    }

    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        return osIntent.getCharSequenceArrayListExtra(name);
    }

    public boolean[] getBooleanArrayExtra(String name) {
        return osIntent.getBooleanArrayExtra(name);
    }

    public byte[] getByteArrayExtra(String name) {
        return osIntent.getByteArrayExtra(name);
    }

    public short[] getShortArrayExtra(String name) {
        return osIntent.getShortArrayExtra(name);
    }

    public char[] getCharArrayExtra(String name) {
        return osIntent.getCharArrayExtra(name);
    }

    public int[] getIntArrayExtra(String name) {
        return osIntent.getIntArrayExtra(name);
    }

    public long[] getLongArrayExtra(String name) {
        return osIntent.getLongArrayExtra(name);
    }

    public float[] getFloatArrayExtra(String name) {
        return osIntent.getFloatArrayExtra(name);
    }

    public double[] getDoubleArrayExtra(String name) {
        return osIntent.getDoubleArrayExtra(name);
    }

    public String[] getStringArrayExtra(String name) {
        return osIntent.getStringArrayExtra(name);
    }

    public CharSequence[] getCharSequenceArrayExtra(String name) {
        return osIntent.getCharSequenceArrayExtra(name);
    }

    public Bundle getExtras() {
        return osIntent.getExtras();
    }

    public int getFlags() {
        return osIntent.getFlags();
    }

    public String getPackage() {
        return osIntent.getPackage();
    }

    public ComponentName getComponent() {
        return osIntent.getComponent();
    }

    public Rect getSourceBounds() {
        return osIntent.getSourceBounds();
    }

    public Intent setFlags(int flags) {
        osIntent.setFlags(flags);
        return this;
    }

    public String getAction() {
        return osIntent.getAction();
    }

    public Intent addFlags(int flags) {
        osIntent.addFlags(flags);
        return this;
    }

    public Intent setPackage(String pack) {
        osIntent.setPackage(pack);
        return this;
    }

    public Intent putExtras(Bundle extras) {
        osIntent.putExtras(extras);
        return this;
    }

    public static class ContentToLargeException extends RuntimeException {
        public ContentToLargeException(String detailMessage) {
            super(detailMessage);
        }
    }

    public static class KeyExistInIntentException extends RuntimeException {
        public KeyExistInIntentException(String detailMessage) {
            super(detailMessage);
        }
    }


    public static <T extends Serializable> T copySerializable(T obj) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;

        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;

        Object o = null;
        //如果子类没有继承该接口，这一步会报错
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            bais = new ByteArrayInputStream(baos.toByteArray());
            ois = new ObjectInputStream(bais);

            o = ois.readObject();
            //noinspection unchecked
            return (T) o;
        } catch (Exception e) {
            throw new RuntimeException("对象中包含了没有继承序列化的对象", e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(oos);
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(ois);
        }
    }

    private static class CacheIntent {
        long cacheTime;
        Intent intent;
        private final static int MAX_CACHE_TIME = 10000;

        CacheIntent(Intent intent) {
            this.intent = intent;
            this.cacheTime = System.currentTimeMillis();
        }

        boolean isExpire() {
            return (System.currentTimeMillis() - cacheTime) > MAX_CACHE_TIME;
        }
    }
}
