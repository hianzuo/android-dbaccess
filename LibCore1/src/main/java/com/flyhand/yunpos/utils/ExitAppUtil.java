package com.flyhand.yunpos.utils;

import android.app.Activity;
import android.content.Context;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.activity.ExActivityManager;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.app.CoreAppActionReceiver;
import com.hianzuo.logger.Log;
import com.hianzuo.logger.LogServiceHelper;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-11-19
 * Time: 下午9:58
 */
public class ExitAppUtil {
    private static final String TAG = ExitAppUtil.class.getSimpleName();

    public static void exitAppImmediately(final Context context) {
        Log.d(TAG, "exitAppImmediately");
        LogServiceHelper.flush();
        CoreAppActionReceiver.release(AbstractCoreApplication.get());
        finishAllActivity(context);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception ignored) {
                }
                killApp();
            }
        });
        thread.setName("ExitAppUtilEx.exitAppImmediately");
        thread.start();
    }

    public static void killApp() {
        Log.d(TAG, "killApp");
        Log.flush();
        System.exit(0);
    }

    @SafeVarargs
    public static void finishActivityWithout(final Class<? extends Activity>... withouts) {
        ExActivityManager.finishActivityWithout(withouts);
    }

    public static void finishActivity(final Class<? extends ExActivity> activityClazz) {
        ExActivityManager.finishActivity(activityClazz);
    }

    public static void finishAllActivity(final Context context) {
        ExActivityManager.finishAllActivity(null);
        if (null != context && context instanceof Activity) {
            ((Activity) context).finish();
        }
    }


    public static void exitAppDelayed(int delayed) {
        AbstractCoreApplication app = AbstractCoreApplication.get();
        app.getUIHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ExitAppUtil.exitAppImmediately(AbstractCoreApplication.get());
            }
        }, delayed);
    }
}
