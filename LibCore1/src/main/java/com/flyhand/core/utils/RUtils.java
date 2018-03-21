package com.flyhand.core.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.flyhand.core.app.AbstractCoreApplication;

import java.util.HashMap;

/**
 * User: Ryan
 * Date: 11-10-12
 * Time: Afternoon 2:30
 */
public class RUtils {
    private static final HashMap<Integer, Integer> maps = new HashMap<>();
    private static final HashMap<String, Class> clzMap = new HashMap<String, Class>();
    private static Class mRClazz = null;
    private static String mResourcePackageName;

    public static void initPreviewLayout(Class rClazz) {
        mRClazz = rClazz;
    }

    public static synchronized void setResourcePackageName(String resourcePackageName) {
        mResourcePackageName = resourcePackageName;
    }

    private synchronized static Class getRClass(Context context, String name) {
        try {
            Class clazz = clzMap.get(name);
            if (null == clazz) {
                if (null != mRClazz) {
                    clazz = Class.forName(mRClazz.getName() + "$" + name);
                } else {
                    if (null == mResourcePackageName) {
                        setResourcePackageName(context.getPackageName());
                    }
                    clazz = Class.forName(mResourcePackageName + ".R$" + name);
                }
                clzMap.put(name, clazz);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Class getRStyleableClz(Context context) {
        return getRClass(context, "styleable");
    }

    private static Class getRStyleClz(Context context) {
        return getRClass(context, "style");

    }

    public static int getIdentifier(String name, String type) {
        Integer keyValue = (type + "_" + name).hashCode();
        Integer res = maps.get(keyValue);
        if (null != res) {
            return res;
        } else {
            Context context = AbstractCoreApplication.get();
            try {
                if ("styleable".equals(type)) {
                    res = (Integer) getRStyleableClz(context).getDeclaredField(name).get("");
                } else if ("style".equals(type)) {
                    res = (Integer) getRStyleClz(context).getDeclaredField(name).get("");
                } else {
                    Class rClass = getRClass(context, type);
                    return (Integer) rClass.getDeclaredField(name).get("");
                }
            } catch (Exception e) {
                res = -1;
                throw new RuntimeException(e);
            }
            if (res > -1) {
                maps.put(keyValue, res);
                return res;
            } else {
                throw new RuntimeException("res id not found in project, type :" + type + " name :" + name);
            }
        }
    }


    public static int getRStringID(String name) {
        return getIdentifier(name, "string");
    }

    public static int getRIntegerID(String name) {
        return getIdentifier(name, "integer");
    }

    public static String getRString(String name) {
        return AbstractCoreApplication.get().getString(getRStringID(name));
    }

    public static String[] getStringArray(String name) {
        return AbstractCoreApplication.get().getResources().getStringArray(getRArrayID(name));
    }

    public static int getRLayoutID(String name) {
        return getIdentifier(name, "layout");
    }

    public static int getRID(String name) {
        return getIdentifier(name, "id");
    }

    public static int getRRawID(String name) {
        return getIdentifier(name, "raw");
    }

    public static int[] getRStyleableIDs(String name) {
        try {
            return (int[]) getRStyleableClz(AbstractCoreApplication.get()).getDeclaredField(name).get("");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{0};
    }

    public static int getRStyleableID(String name) {
        return getIdentifier(name, "styleable");
    }

    public static Drawable getDrawable(String name) {
        return AbstractCoreApplication.get().getResources().getDrawable(getRDrawableID(name));
    }

    public static int getRDrawableID(String name) {
        return getIdentifier(name, "drawable");
    }

    public static int getRXmlID(String name) {
        return getIdentifier(name, "xml");
    }

    public static int getRAnimID(String name) {
        return getIdentifier(name, "anim");
    }

    public static int getRBoolID(String name) {
        return getIdentifier(name, "bool");
    }

    public static int getRMenuID(String name) {
        return getIdentifier(name, "menu");
    }

    public static int getRArrayID(String name) {
        return getIdentifier(name, "array");
    }

    public static int getRColorID(String name) {
        return getIdentifier(name, "color");
    }

    public static int getColor(String name) {
        return AbstractCoreApplication.get().getResources().getColor(getRColorID(name));
    }

    public static int getRStyleID(String name) {
        return getIdentifier(name, "style");
    }
}
