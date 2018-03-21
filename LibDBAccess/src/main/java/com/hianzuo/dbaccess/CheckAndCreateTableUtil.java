package com.hianzuo.dbaccess;

import android.content.Context;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.DexUtil;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ryan
 * @date 14/12/25.
 */
public class CheckAndCreateTableUtil {
    public static void execute(Class<? extends Dto>... classes) {
        execute(null, classes);
    }

    public static void execute(Database db, Class<? extends Dto>... classes) {
        List<Class<? extends Dto>> list = new ArrayList<>();
        Collections.addAll(list, classes);
        execute(db, list);
    }

    public static void execute(Database db, List<Class<? extends Dto>> classes) {
        if (null == db) {
            db = DBInterface.openWritableDatabase();
        }
        db.beginTransaction();
        try {
            for (Class<? extends Dto> aClass : classes) {
                DBInterface.checkAndCreateTable(db, aClass);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void createAllInPackage(final Context context, Database db, final String pkg) {
        createAllInPackageInternal(context, db, pkg);
    }

    private static void createAllInPackageInternal(Context context, Database db, String pkg) {
        try {
            List<String> allClasses = DexUtil.allClasses(context, AbstractCoreApplication.isDebugMode());
            ClassLoader classLoader = context.getClassLoader();
            List<Class<? extends Dto>> list = new ArrayList<Class<? extends Dto>>();
            for (String classStr : allClasses) {
                if (classStr.startsWith(pkg)) {
                    Class<? extends Dto> c;
                    try {
                        //noinspection unchecked
                        c = (Class<? extends Dto>) classLoader.loadClass(classStr);
                    } catch (Exception e) {
                        return;
                    }
                    if (Dto.class.isAssignableFrom(c)) {
                        if (!Modifier.isAbstract(c.getModifiers())) {
                            list.add(c);
                        }
                    }
                }

            }
            execute(db, list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
