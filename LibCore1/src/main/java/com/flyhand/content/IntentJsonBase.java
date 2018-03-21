package com.flyhand.content;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.FileUtils;
import com.flyhand.core.utils.StringUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2015/5/30.
 */
public class IntentJsonBase {
    private static final Gson gson = new Gson();
    private static File baseDir;
    private static final String CHARSET = "utf-8";

    public static void init(AbstractCoreApplication app) {
        baseDir = new File(AbstractCoreApplication.get().getFilesDir(), "IntentGson");
    }

    public static void store(IntentJson obj) {

    }

    public static IntentJson read(Class<? extends IntentJson> type, String key) {
        String content = readContentByKey(key);
        if (StringUtil.isNotEmpty(content)) {
            return gson.fromJson(content, type);
        } else {
            return null;
        }
    }

    private static String readContentByKey(String key) {
        try {
            return FileUtils.readFileToString(new File(baseDir, key), CHARSET);
        } catch (IOException e) {
            return null;
        }
    }

    public static String format(IntentJson obj) {
        return gson.toJson(obj);
    }
}
