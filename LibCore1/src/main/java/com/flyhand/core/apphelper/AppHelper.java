package com.flyhand.core.apphelper;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.NetworkUtils;
/**
 * Created by Ryan
 * On 2016/8/9.
 */
public class AppHelper implements ServiceConnection {
    private Application application;
    private IAppHelperService mIAppHelperService;
    private final AppHelper.LockObj mLock = new AppHelper.LockObj();
    private final Intent mIntent;
    private static AppHelper helper;
    private static final String TAG = "AppHelperService";
    private boolean mConnectedBefore = false;
    public static final String ACTION_ON_NETWORK_CHANGED = "com.flyhand.apphelper.ON_NETWORK_CHANGED";
    public static final IntentFilter ACTION_ON_NETWORK_CHANGED_FILTER = new IntentFilter(ACTION_ON_NETWORK_CHANGED);
    public static final String NETWORK_TYPE_LOCAL = "networkAvailable";
    public static final String NETWORK_TYPE_SERVER = "canAccessServer";
    public static final String NETWORK_TYPE_INTERNET = "canAccessInternet";

    private AppHelper(Application application) {
        this.application = application;
        this.mIntent = new Intent();
        this.mIntent.setAction("com.flyhand.core.apphelper.IAppHelperService");
        this.mIntent.setPackage(application.getPackageName());
        this.bindIService();
    }

    public static synchronized void init(Application application) {
        if (null == helper) {
            helper = new AppHelper(application);
        }
    }

    private void bindIService() {
        Log.d(TAG, "AppHelperService is binding.");
        this.application.bindService(this.mIntent, this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "AppHelperService is connected.");
        this.mIAppHelperService = IAppHelperService.Stub.asInterface(service);
        this.mConnectedBefore = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "AppHelperService is disconnected.");
        this.mIAppHelperService = null;
        this.mLock.waitMillis(1000);
        this.bindIService();
    }


    private boolean __reportError(String title, String content) {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.reportError(title, content);
            } catch (RemoteException ignored) {
            }
        }

        return false;
    }


    private boolean __networkPingServer(String server) {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.networkPingServer(server);
            } catch (RemoteException ignored) {
            }
        }

        return false;
    }

    private boolean __canAccessInternet() {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.canAccessInternet();
            } catch (RemoteException ignored) {
            }
        }
        return networkAvailableLocal();
    }


    private boolean __canAccessServer() {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.canAccessServer();
            } catch (RemoteException ignored) {
            }
        }
        return networkAvailableLocal();
    }

    private boolean __networkAvailable() {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.networkAvailable();
            } catch (RemoteException ignored) {
            }
        }
        return networkAvailableLocal();
    }

    private boolean __pingServerAsync(int timeout) {
        IAppHelperService service = this.mIAppHelperService;
        if (null != service) {
            try {
                return service.pingServerAsync(timeout);
            } catch (Exception ignored) {
            }
        }
        return false;
    }

    public static void reportError(String title, String content) {
        if (null != helper && !helper.__reportError(title, content)) {
            if (helper.mConnectedBefore) {
                Log.d(TAG, "AppHelperService report error failure.");
            } else {
                Log.d(TAG, "AppHelperService is not init.");
            }
        }
    }

    public static void networkPingServer(String host) {
        if (null != helper && !helper.__networkPingServer(host)) {
            if (helper.mConnectedBefore) {
                Log.d(TAG, "AppHelperService networkPingHost failure.");
            } else {
                Log.d(TAG, "AppHelperService is not init.");
            }
        }
    }

    public static boolean canAccessInternet() {
        if (null != helper) {
            return helper.__canAccessInternet();
        } else {
            return networkAvailableLocal();
        }
    }


    public static boolean canAccessServer() {
        if (null != helper) {
            return helper.__canAccessServer();
        } else {
            return networkAvailableLocal();
        }
    }

    public static boolean networkAvailable() {
        if (null != helper) {
            return helper.__networkAvailable();
        } else {
            return networkAvailableLocal();
        }
    }

    private static boolean networkAvailableLocal() {
        return NetworkUtils.isAvailable(AbstractCoreApplication.get());
    }

    public static boolean pingServerAsync(int timeout) {
        if (null != helper) {
            return helper.__pingServerAsync(timeout);
        } else {
            return false;
        }
    }

    private class LockObj {
        private boolean isWaiting = false;

        public LockObj() {
            super();
        }

        public synchronized boolean waitMillis(int millis) {
            if (millis > 0) {
                try {
                    isWaiting = true;
                    wait((long) millis);
                    return true;
                } catch (Exception ignored) {
                    return false;
                } finally {
                    isWaiting = false;
                }
            } else {
                return true;
            }
        }

        public synchronized boolean waiting() {
            return isWaiting;
        }
    }
}
