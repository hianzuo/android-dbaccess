package com.flyhand.core.apphelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Html;
import android.view.Window;
import android.view.WindowManager;

import com.flyhand.core.BaseService;
import com.flyhand.core.app.AbstractCoreApplication;


/**
 * Created by Ryan
 * On 2016/8/9.
 */
public class AppHelperService extends BaseService {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, BaseService.START_FLAG_REDELIVERY, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkHelper.init(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return new IAppHelperService.Stub() {
            @Override
            public boolean reportError(String title, String error) {
                return alertError(title, error);
            }

            @Override
            public boolean networkPingServer(String server) throws RemoteException {
                NetworkHelper.pingServer(server);
                return true;
            }

            @Override
            public boolean canAccessInternet() throws RemoteException {
                return NetworkHelper.canAccessInternet();
            }

            @Override
            public boolean canAccessServer() throws RemoteException {
                return NetworkHelper.canAccessServer();
            }

            @Override
            public boolean networkAvailable() throws RemoteException {
                return NetworkHelper.networkAvailable();
            }

            @Override
            public boolean pingServerAsync(int timeout) throws RemoteException {
                return NetworkHelper.pingServerAsync(timeout);
            }
        };
    }

    private boolean alertError(final String title, final String error) {
        try {
            AbstractCoreApplication.get().getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    try {
                        AlertDialog alertDialog = new AlertDialog.Builder(AbstractCoreApplication.get(), android.R.style.Theme_DeviceDefault_Light_Dialog)
                                .setTitle(title)
                                .setMessage(Html.fromHtml(formatToHtml(error, true)))
                                .create();
                        Window window = alertDialog.getWindow();
                        if (null != window) {
                            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
//                            alertDialog.getWindow().setLayout(ViewUtilsBase.getDipPx(resources, 900), -2);
                            alertDialog.getWindow().setLayout(-1, -2);
                        }
                    } catch (Exception ignored) {
                    }
                }
            });
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static final String[] LIGHT_PN_ARRAY = new String[]{
            "com.flyhand.",
            "org.posyunsv.",
            "com.ryan.",
            "com.friendlyarm.",
            "com.nostra13.",
            "yuku.ambilwarna.",
    };

    private static String formatToHtml(String content, boolean isError) {
        StringBuilder sb = new StringBuilder("");
        String[] lines = content.split("\r\n|\r|\n|<br>|<br/>");
        for (String line : lines) {
            line = line.replace("\t", "　");
            if (isError) {
                if (line.contains("<font") && line.contains("</font>")) {
                    sb.append(line).append("<br/>");
                } else {
                    if (isExceptionLine(line)) {
                        sb.append("<font color=\"#00008b\">").append(line).append("</font><br/>");
                    } else if (containsLightPackageName(line)) {
                        sb.append("<font color=\"blue\">").append(line).append("</font><br/>");
                    } else {
                        sb.append("<font color=\"#056900\">").append(line).append("</font><br/>");
                    }
                }
            } else {
                sb.append(line).append("<br/>");
            }
        }
        return sb.toString();
    }

    private static boolean isExceptionLine(String line) {
        return null != line && line.contains("Exception:");
    }

    private static boolean containsLightPackageName(String line) {
        if (null == line) {
            return false;
        }
        for (String pn : LIGHT_PN_ARRAY) {
            if (line.startsWith("　at " + pn)) {
                return true;
            }
        }
        return false;
    }
}
