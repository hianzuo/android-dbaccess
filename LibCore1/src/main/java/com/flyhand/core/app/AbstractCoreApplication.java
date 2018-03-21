package com.flyhand.core.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;

import com.flyhand.core.R;
import com.flyhand.core.activity.ExActivity;
import com.flyhand.yunpos.app.ApplicationHandlerImpl;
import com.hianzuo.logger.Log;

/**
 * @author Ryan
 * @date 2016/5/14
 */
public abstract class AbstractCoreApplication extends Application {
    private ApplicationHandler mHandler = new ApplicationHandlerImpl();
    private static AbstractCoreApplication application;
    private Handler uiHandler;
    private DisplayMetrics mOrigDm;
    private Configuration mOrigConf;
    private DisplayMetrics mNewDm;
    private Configuration mNewConf;
    private ExActivity mCurrentActivity;
    private String mProgressName;


    @Override
    public void onCreate() {
        uiHandler = new Handler();
        application = this;
        mProgressName = getCurProcessName();
        super.onCreate();
        mHandler.onCreate(this);
        initOrigDisplayMetrics();
    }

    public Handler getUIHandler() {
        return uiHandler;
    }

    public static AbstractCoreApplication get() {
        return application;
    }

    public void reportError(Throwable ex) {

    }

    public void reportErrorInThread(Exception ex) {

    }

    public static void updateDensity(Context context) {
        if (null != application && null != application.mNewConf && null != application.mNewDm) {
            //noinspection AliDeprecation
            context.getResources().updateConfiguration(application.mNewConf, application.mNewDm);
            Log.d("OrigDisplayMetrics", "update new.density：" + application.mNewDm.density);
        }
    }

    public String getApplicationResource(String key) {
        return mHandler.getApplicationResource(key);
    }

    public void putForwardParams(String key, Object param) {
        mHandler.putForwardParams(key, param);
    }

    public Object takeForwardParams(String key) {
        return mHandler.takeForwardParams(key);
    }

    private void initOrigDisplayMetrics() {
        if (Build.VERSION.SDK_INT >= 17) {
            if (!isTablet(this) && isTabletApp()) {
                Log.d("OrigDisplayMetrics", "is phone");
                DisplayMetrics dm = getResources().getDisplayMetrics();
                mOrigDm = new DisplayMetrics();
                mOrigDm.setTo(dm);

                Configuration config = getResources().getConfiguration();
                mOrigConf = new Configuration();
                mOrigConf.setTo(config);
                Log.d("OrigDisplayMetrics", "displayMetrics.density:" + dm.density);
                Log.d("OrigDisplayMetrics", "displayMetrics.densityDpi" + dm.densityDpi);
                Log.d("OrigDisplayMetrics", "displayMetrics.heightPixels：" + dm.heightPixels);
                Log.d("OrigDisplayMetrics", "displayMetrics.widthPixels：" + dm.widthPixels);
                Log.d("OrigDisplayMetrics", "displayMetrics.scaledDensity：" + dm.scaledDensity);
                Log.d("OrigDisplayMetrics", "displayMetrics.xdpi：" + dm.xdpi);
                Log.d("OrigDisplayMetrics", "displayMetrics.ydpi：" + dm.ydpi);
                mNewDm = new DisplayMetrics();
                mNewDm.setTo(dm);
                mNewConf = new Configuration();
                mNewConf.setTo(config);
                float desiredDensity = dm.density / 2.0f;
                int densityDpi = (int) (desiredDensity * DisplayMetrics.DENSITY_MEDIUM);
                mNewDm.density = desiredDensity;
                mNewDm.scaledDensity = desiredDensity;
                mNewDm.densityDpi = densityDpi;
                mNewConf.densityDpi = densityDpi;
                updateDensity(this);

            } else {
                Log.d("OrigDisplayMetrics", "is tablet");
            }
        } else {
            Log.d("OrigDisplayMetrics", "sdk < 17");
        }
    }

    protected boolean isTabletApp() {
        return false;
    }

    public static boolean isTablet(Context context) {
        return context.getResources().getBoolean(R.bool.isTablet);
    }


    public ExActivity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(ExActivity activity) {
        this.mCurrentActivity = activity;
    }


    public static void removeCallbacks(Runnable runnable) {
        AbstractCoreApplication app = get();
        if (null != app) {
            app.uiHandler.removeCallbacks(runnable);
        }
    }

    public static void postDelayed(Runnable runnable, int delayed) {
        AbstractCoreApplication app = get();
        if (null != app) {
            app.uiHandler.postDelayed(runnable, delayed);
        }
    }

    public static void removeCallback(Runnable runnable) {
        AbstractCoreApplication app = get();
        if (null != app) {
            app.uiHandler.removeCallbacks(runnable);
        }
    }

    public static void post(Runnable runnable) {
        AbstractCoreApplication app = get();
        if (null != app) {
            app.uiHandler.post(runnable);
        }
    }


    public String getCurProcessName() {
        if (null != mProgressName) {
            return mProgressName;
        } else {
            int pid = android.os.Process.myPid();
            ActivityManager mActivityManager = (ActivityManager)
                    getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager
                    .getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    mProgressName = appProcess.processName;
                    break;
                }
            }
        }
        return mProgressName;
    }

    public static boolean inMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    public static boolean isDebugMode() {
        return get().isDebugModeInternal();
    }

    protected boolean isDebugModeInternal() {
        return false;
    }

    public static String progressName() {
        return get().getCurProcessName();
    }
}
