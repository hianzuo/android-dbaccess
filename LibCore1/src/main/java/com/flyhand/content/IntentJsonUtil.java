package com.flyhand.content;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.FileUtils;
import com.flyhand.core.utils.SDCardUtil;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Ryan
 * On 2015/5/30.
 */
public class IntentJsonUtil {
    private static final Gson gson = new Gson();
    private static File baseDir;
    private static final String CHARSET = "utf-8";
    private static final Object mLock = new Object();
    private static final LinkedHashMap<String, CacheData> mCacheIntent = new LinkedHashMap<>();
    private final static int MAX_CACHE_SIZE = 200;
    private static long lastDeleteTime = 0;
    private static final String READ_MARK = "_read";

    public static final IntentJsonNULL NULL_VALUE = new IntentJsonNULL();

    public synchronized static void init(AbstractCoreApplication app) {
        baseDir = SDCardUtil.getWriteFileDir(app, "yunpos/IntentGson");
        if (null == baseDir) {
            throw new IntentJsonException("can not create base dir");
        } else {
            clearAllFile();
        }
    }

    public static String store(IntentJson obj) {
        synchronized (mLock) {
            String content = format(obj);
            try {
                return storeByFile(content);
            } catch (Exception e) {
                postExceptionToBugly(e);
                return storeByFile(content);
            }
        }
    }

    private static String storeByFile(String content) {
        String key = UUID.randomUUID().toString();
        File file = new File(baseDir, key);
        if (file.exists()) {
            throw new IntentJsonException("the store IntentJson key[" + key + "] is exist.");
        }
        try {
            if (baseDir.exists() || baseDir.mkdirs()) {
                FileUtils.write(file, content, CHARSET);
                //noinspection ResultOfMethodCallIgnored
                file.setLastModified(System.currentTimeMillis());
                return file.getAbsolutePath();
            } else {
                throw new IntentJsonException("can not create base dir");
            }
        } catch (IOException ex) {
            throw new IntentJsonException("write to file error", ex);
        }
    }

    public static <T extends IntentJson> T read(String key, Type type) {
        String content = readAndRemoveContentByKey(key); //655 ,670
        if (null == content) {
            //noinspection unchecked
            return (T) IntentJsonUtil.NULL_VALUE;
        } else {
            return gson.fromJson(content, type);
        }
    }

    private static String readAndRemoveContentByKey(String key) {
        synchronized (mCacheIntent) {
            try {
                CacheData data = mCacheIntent.get(key);
                if (null != data) {
                    return data.data;
                }
                String content = removeFromFile(key);
                if (null != content) {
                    removeCacheIntentByExpireTime();
                    removeCacheIntentByMaxCount();
                    mCacheIntent.put(key, new CacheData(content));
                    return content;
                } else {
                    return null;
                }
            } catch (Exception e) {
                postExceptionToBugly(e);
                return removeFromFile(key);
            }
        }
    }

    private static void postExceptionToBugly(Exception e) {
        //        CrashReport.postCatchedException(e);
        try {
            Class<?> crashReportClazz = Class.forName("com.tencent.bugly.crashreport.CrashReport");
            Method method = crashReportClazz.getMethod("postCatchedException", Throwable.class);
            method.invoke(null, e);
        } catch (Exception ignored) {
            e.printStackTrace();
        }
    }


    private static void removeCacheIntentByMaxCount() {
        synchronized (mCacheIntent) {
            if (mCacheIntent.size() > MAX_CACHE_SIZE) {
                int i = 0;
                List<String> removeKeyList = new ArrayList<>();
                for (String key : mCacheIntent.keySet()) {
                    if (i > MAX_CACHE_SIZE) {
                        removeKeyList.add(key);
                    }
                    i++;
                }
                for (String key : removeKeyList) {
                    mCacheIntent.remove(key);
                }
            }
        }
    }

    private static void removeCacheIntentByExpireTime() {
        synchronized (mCacheIntent) {
            List<String> removeKeyList = new ArrayList<>();
            for (String key : mCacheIntent.keySet()) {
                CacheData cacheIntent = mCacheIntent.get(key);
                if (cacheIntent.isExpire()) {
                    removeKeyList.add(key);
                }
            }
            for (String key : removeKeyList) {
                mCacheIntent.remove(key);
            }
        }
    }

    private static String removeFromFile(String key) {
        synchronized (mCacheIntent) {
            File unreadFile = new File(key);
            File readFile = new File(key + READ_MARK);
            File file = unreadFile;
            if (!file.exists()) {
                file = readFile;
            }
            boolean existFile = false;
            try {
                if (file.exists()) {
                    existFile = true;
                    //noinspection ResultOfMethodCallIgnored
                    file.setLastModified(System.currentTimeMillis());
                    return FileUtils.readFileToString(file, CHARSET);
                } else {
                    return null;
                }

            } catch (Exception ex) {
                if (ex instanceof FileNotFoundException) {
                    return null;
                } else {
                    throw new IntentJsonException("read content by key[" + key + "] error.", ex);
                }
            } finally {
                try {
                    if (existFile) {
                        if (file == unreadFile) {
                            if (readFile.exists()) {
                                //noinspection ResultOfMethodCallIgnored
                                readFile.delete();
                            }
                            //noinspection ResultOfMethodCallIgnored
                            file.renameTo(readFile);
                        }
                    }
                    file.deleteOnExit();
                    //每隔30分钟删除一次过期的缓存文件
                    clearCacheFileInterval(1800000);
                } catch (Exception ignored) {
                }
            }
        }
    }

    private synchronized static void clearCacheFileInterval(int interval) {
        long pastLastClearTime = System.currentTimeMillis() - lastDeleteTime;
        if (pastLastClearTime > interval) {
            clearAllFile();
        }
    }

    public static synchronized void clearAllFile() {
        if (null == baseDir) {
            return;
        }
        lastDeleteTime = System.currentTimeMillis();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File[] files = baseDir.listFiles();
                long currentTime = System.currentTimeMillis();
                long _3_HOUR = 10800000; //大于3小时的全删除
                long _30_MIN = 1800000; //大于30分钟并且已读的全删除
                for (File file : files) {
                    try {
                        boolean isReadFile = file.getName().endsWith(READ_MARK);
                        long lastTime = file.lastModified();
                        boolean isPastHourTime = currentTime - lastTime > _3_HOUR;
                        boolean isReadAndPastMin = isReadFile && currentTime - lastTime > _30_MIN;
                        if (isPastHourTime || isReadAndPastMin) {
                            FileUtils.deleteQuietly(file);
                            Thread.sleep(5);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        thread.setName("IntentJsonUtil.clearAllFile.Thread");
        thread.setPriority(1);
        thread.start();
    }

    public static String format(IntentJson obj) {
        return gson.toJson(obj);
    }

    private static class CacheData {
        long cacheTime;
        String data;
        private final static int MAX_CACHE_TIME = 20000;

        CacheData(String data) {
            this.data = data;
            this.cacheTime = System.currentTimeMillis();
        }

        boolean isExpire() {
            return (System.currentTimeMillis() - cacheTime) > MAX_CACHE_TIME;
        }
    }

}
