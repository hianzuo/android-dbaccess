package com.flyhand.core.activity;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Ryan
 * Date: 11-9-28
 * Time: Afternoon 1:26
 */
public class ExActivityManager {
    private static List<Activity> activityList = new ArrayList<>();

    public static void finishActivityWithout(Class<? extends Activity>... types) {
        for (Activity activity : activityList) {
            try {
                boolean isWithout = false;
                for (Class<? extends Activity> type : types) {
                    if (type.isAssignableFrom(activity.getClass())) {
                        isWithout = true;
                        break;
                    }
                }
                if (!isWithout) {
                    activity.finish();
                }
            } catch (Exception e) {
                //
            }
        }
    }

    public static void finishActivity(Class<? extends ExActivity> type) {
        for (Activity activity : activityList) {
            try {
                if (type.isAssignableFrom(activity.getClass())) {
                    activity.finish();
                }
            } catch (Exception e) {
                //
            }
        }
    }

    public static void finishAllActivity(ArrayList<Activity> without) {
        for (Activity activity : activityList) {
            try {
                if (null == without) {
                    activity.finish();
                } else if (!without.contains(activity)) {
                    activity.finish();
                }
            } catch (Exception e) {
                //
            }
        }
    }

    public static boolean exist(Class<? extends Activity> clz) {
        for (Activity activity : activityList) {
            try {
                if (clz.equals(activity.getClass())) {
                    return true;
                }
            } catch (Exception ignored) {
            }
        }
        return false;
    }


    public static void add(ExActivity activity) {
        activityList.add(activity);
    }

    public static void remove(ExActivity activity) {
        activityList.remove(activity);
    }


    public static <T extends ExActivity> T getLastAvailable(Class<T> clazz) {
        int size = activityList.size();
        for (int i = size - 1; i > -1; i--) {
            Activity activity = activityList.get(i);
            if (clazz.isAssignableFrom(activity.getClass())) {
                if (!activity.isFinishing()) {
                    return (T) activity;
                }
            }
        }
        return null;
    }
}
