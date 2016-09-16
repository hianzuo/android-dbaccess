package com.hianzuo.dbaccess;

import android.content.Context;
import dalvik.system.DexFile;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * Created by Ryan on 14/12/25.
 */
public class CheckAndCreateTableUtil {
    public static void execute(Class<? extends Dto>... classes) {
        execute(null, classes);
    }

    public static void execute(Database db, Class<? extends Dto>... classes) {
        List<Class<? extends Dto>> list = new ArrayList<Class<? extends Dto>>();
        Collections.addAll(list, classes);
        execute(db, list);
    }

    public static void execute(Database db, List<Class<? extends Dto>> classes) {
        if (null == db) db = DBInterface.openWritableDatabase();
        try {
            db.beginTransaction();
            for (Class<? extends Dto> aClass : classes) {
                DBInterface.checkAndCreateTable(db, aClass);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public static void createAllInPackage(final Context context, Database db, final String pkg) {
        __createAllInPackage(context, db, pkg);
    }

    private static void __createAllInPackage(Context context, Database db, String pkg) {
        try {
            DexFile df = new DexFile(context.getPackageCodePath());
            List<Class<? extends Dto>> list = new ArrayList<Class<? extends Dto>>();
            ClassLoader classLoader = context.getClassLoader();
            for (Enumeration<String> iterator = df.entries(); iterator.hasMoreElements(); ) {
                String s = iterator.nextElement();
                if (s.startsWith(pkg)) {
                    Class<? extends Dto> c;
                    try {
                        //noinspection unchecked
                        c = (Class<? extends Dto>) classLoader.loadClass(s);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
