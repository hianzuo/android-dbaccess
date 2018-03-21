package com.flyhand.core.apphelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.ThreadFactoryUtil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Ryan
 * On 2016/8/26.
 */
public abstract class NetworkHelper {
    private static final int PING_INTERVAL = 10000;
    private static int mPingCount = 0;
    private static final String PING_INTERNET_HOST = "pos.flyhand.com";
    private static String PING_SERVER_HOST = "pos.flyhand.com";
    private static int PING_SERVER_PORT = 80;
    private static final int WHAT_FOR_PING_HOST = 85307;
    private static boolean mNetworkAvailable = false;
    private static boolean mCanAccessServer = false;
    private static boolean mCanAccessInternet = false;
    private static boolean mIsInited = false;
    private static final ExecutorService mExecutorService =  ThreadFactoryUtil.createSingle(NetworkHelper.class,true,1);
    private static InternetChecker mInternetChecker;
    private static Handler mUIHandler;
    private static final String TAG = "NetworkHelper";
    private static final boolean DEBUG = true;


    private NetworkHelper() {
    }

    public static void pingServer(String pingServer) {
        int port = 80;
        String host = PING_SERVER_HOST;
        if (pingServer.contains(":")) {
            int i = pingServer.lastIndexOf(":");
            try {
                port = Integer.parseInt(pingServer.substring(i + 1));
                host = pingServer.substring(0, i);
            } catch (Exception ignored) {
            }
        } else {
            host = pingServer;
        }
        if (null == host || host.length() == 0) {
            return;
        }
        if (!PING_SERVER_HOST.equals(host) || PING_SERVER_PORT != port) {
            PING_SERVER_HOST = host;
            PING_SERVER_PORT = port;
            __checkInternetAccess();
        }
    }

    public synchronized static void init(Context context) {
        if (!mIsInited) {
            mIsInited = true;
            context = context.getApplicationContext();
            setNetworkAvailable(__isAvailable(context));
            if (mNetworkAvailable) {
                __checkInternetAccess();
            }
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, android.content.Intent intent) {
                    ConnectivityManager cm = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo info = cm.getActiveNetworkInfo();
                    if (null != info) {
                        setNetworkAvailable(true);
                        setCanAccessServer(false);
                        setCanAccessInternet(false);
                        __checkInternetAccess();
                    } else {
                        setNetworkAvailable(false);
                        setCanAccessServer(false);
                        setCanAccessInternet(false);
                    }
                }
            }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            mUIHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == WHAT_FOR_PING_HOST) {
                        mPingCount++;
                        if (null == mInternetChecker || mInternetChecker.isDone()) {
                            if (mNetworkAvailable) {
                                if (!mCanAccessInternet || !mCanAccessServer || mPingCount % 2 == 0) {
                                    mInternetChecker = new InternetChecker();
                                    mExecutorService.execute(mInternetChecker);
                                }
                            }
                        }
                        sendPingActionDelayed(PING_INTERVAL);
                    }
                }
            };
            sendPingActionDelayed(PING_INTERVAL);
        }
    }

    private static void sendPingActionDelayed(int delayed) {
        mUIHandler.removeMessages(WHAT_FOR_PING_HOST);
        mUIHandler.sendEmptyMessageDelayed(WHAT_FOR_PING_HOST, delayed);
    }

    private static void __checkInternetAccess() {
        __stopCurrentInternetChecker();
        mInternetChecker = new InternetChecker();
        mExecutorService.execute(mInternetChecker);
    }

    private static void __stopCurrentInternetChecker() {
        if (null != mInternetChecker && !mInternetChecker.isDone()) {
            mInternetChecker.cancel();
            mInternetChecker.interrupt();
        }
    }

    public static boolean canAccessInternet() {
        return mCanAccessInternet;
    }

    public static boolean canAccessServer() {
        return mCanAccessServer;
    }

    public static boolean networkAvailable() {
        return mNetworkAvailable;
    }

    public static boolean pingServerAsync(int timeout) {
        mInternetChecker.execute(timeout);
        return mCanAccessServer;
    }

    private static boolean __isAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] allNetworkInfo = connectivity.getAllNetworkInfo();
            List<NetworkInfo> networkInfoList = new ArrayList<>();
            if (null != allNetworkInfo) {
                Collections.addAll(networkInfoList, allNetworkInfo);
            }
            //商米T1没有返回有线网络连接，但是getActiveNetworkInfo返回了。
            NetworkInfo activeNetworkInfo = connectivity.getActiveNetworkInfo();
            if (null != activeNetworkInfo) {
                networkInfoList.add(0, activeNetworkInfo);
            }
            for (NetworkInfo networkInfo : networkInfoList) {
                if (DEBUG) {
                    Log.d(TAG, "NetworkInfo: " + networkInfo.toString());
                }
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    if (DEBUG) {
                        Log.d(TAG, "isAvailable true");
                    }
                    return true;
                }
            }
        }
        if (DEBUG) {
            Log.d(TAG, "isAvailable false");
        }
        return false;
    }

    private static synchronized void setNetworkAvailable(boolean flag) {
        if (flag != mNetworkAvailable) {
            mNetworkAvailable = flag;
            onNetworkChanged();
        }
    }


    private static synchronized void setCanAccessServer(boolean flag) {
        if (flag != mCanAccessServer) {
            mCanAccessServer = flag;
            onNetworkChanged();
        }

    }

    private static synchronized void setCanAccessInternet(boolean flag) {
        if (flag != mCanAccessInternet) {
            mCanAccessInternet = flag;
            onNetworkChanged();
        }
    }

    private static Runnable networkChangedRunner = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(AppHelper.ACTION_ON_NETWORK_CHANGED);
            intent.putExtra(AppHelper.NETWORK_TYPE_LOCAL, mNetworkAvailable);
            intent.putExtra(AppHelper.NETWORK_TYPE_SERVER, mCanAccessServer);
            intent.putExtra(AppHelper.NETWORK_TYPE_INTERNET, mCanAccessInternet);
            AbstractCoreApplication.get().sendBroadcast(intent);
        }
    };

    private static void onNetworkChanged() {
        Handler handler = AbstractCoreApplication.get().getUIHandler();
        handler.removeCallbacks(networkChangedRunner);
        handler.postDelayed(networkChangedRunner, 100);
    }

    private static class InternetChecker implements Runnable {
        boolean isCancel = false;
        boolean isDone = false;
        Thread mCheckThread;

        public boolean isCancel() {
            return isCancel;
        }

        public boolean isDone() {
            return isDone;
        }

        public void cancel() {
            isCancel = true;
        }

        public void interrupt() {
            if (null != mCheckThread) {
                try {
                    mCheckThread.interrupt();
                } catch (Exception ignored) {
                }
            }
        }

        @Override
        public final void run() {
            try {
                isDone = false;
                try {
                    mCheckThread = Thread.currentThread();
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
                if (!isCancel()) {
                    execute();
                }
            } finally {
                isDone = true;
            }

        }

        public void execute() {
            execute(5000);
        }

        public void execute(int timeout) {
            try {
                boolean hostReachable = isHostReachable(PING_SERVER_HOST, PING_SERVER_PORT, timeout);
                com.hianzuo.logger.Log.d(TAG, "host[" + PING_SERVER_HOST + "][" + PING_SERVER_PORT + "] reachable: " + hostReachable);
                setCanAccessServer(hostReachable);
                if (isLANRequest(PING_SERVER_HOST)) {
                    setCanAccessInternet(isHostReachable(PING_INTERNET_HOST, 80, timeout));
                } else {
                    setCanAccessInternet(mCanAccessServer);
                }
            } catch (Exception ignored) {
            }
        }


        private static boolean isLANRequest(String requestUrl) {
            return null != requestUrl && (
                    requestUrl.contains("192.") ||
                            requestUrl.contains("172.")
            );
        }

        private boolean isHostReachablePing(String host) {
            try {
                Runtime runtime = Runtime.getRuntime();
                Process proc = runtime.exec("ping -w 1 -c 1 " + host);
                proc.waitFor();
                int exit = proc.exitValue();
                if (exit == 0) {
                    return true;
                }
            } catch (Exception ignored) {
            }
            return false;
        }

        private boolean isHostReachableUrlConnection(String host, int timeout) {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://" + host);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Android Application");
                conn.setRequestProperty("Connection", "close");
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
                conn.connect();
                return (conn.getResponseCode() == 200);
            } catch (Exception ignored) {
            } finally {
                if (null != conn) {
                    conn.disconnect();
                }
            }
            return false;
        }

        private boolean isHostReachable(final String host, final int port, final int timeout) {
            final boolean[] connected = new boolean[]{false};
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Socket socket = null;
                    try {
                        socket = new Socket();
                        SocketAddress socketAddress = new InetSocketAddress(host, port);
                        socket.connect(socketAddress, timeout + 50);
                        if (socket.isConnected()) {
                            connected[0] = true;
                            socket.close();
                        }
                    } catch (Exception ignored) {
                    } finally {
                        if (null != socket) {
                            try {
                                socket.close();
                            } catch (IOException ignored) {
                            }
                        }
                    }
                }
            };
            Thread thread = new Thread(runnable);
            thread.setPriority(1);
            thread.setDaemon(true);
            thread.start();
            try {
                thread.join(timeout);
            } catch (Exception ignored) {
            }
            return connected[0];
        }
    }
}
