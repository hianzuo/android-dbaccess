package com.flyhand.core.activity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flyhand.content.Intent;
import com.flyhand.core.ExplicitIntentUtil;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.config.Config;
import com.flyhand.core.dto.UserToKnow;
import com.flyhand.core.dto.VersionUpdate;
import com.flyhand.core.remote.NetResult;
import com.flyhand.core.remote.RemoteAccess;
import com.flyhand.core.utils.AlertUtil;
import com.flyhand.core.utils.AppkeyUtils;
import com.flyhand.core.utils.ExceptionUtils;
import com.flyhand.core.utils.HandlerUtil;
import com.flyhand.core.utils.NetworkUtils;
import com.flyhand.core.utils.RUtils;
import com.flyhand.core.utils.ScaleTextFontSizeUtil;
import com.flyhand.core.utils.StringUtil;
import com.flyhand.core.utils.TagRunnable;
import com.flyhand.core.utils.URIUtils;
import com.flyhand.core.widget.ToastFlyhand;
import com.flyhand.yunpos.dialog.AbDialog;
import com.flyhand.yunpos.dialog.AbProgressDialog;
import com.flyhand.yunpos.dialog.AlertDialog;
import com.flyhand.yunpos.handler.SoftInputHandler;
import com.flyhand.yunpos.utils.ActivityAnimationSwitcherUtils;
import com.flyhand.yunpos.utils.UtilCallback;
import com.hianzuo.logger.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * User: Ryan
 * Date: 11-10-8
 * Time: Afternoon 4:22
 */
public abstract class ExActivity extends FragmentActivity
        implements ExActivityScreenTimeOutHandler.Watcher {
    public static final int WHAT_FOR_ERROR = 4444;
    public static final int WHAT_FOR_ERROR_STRING = 4445;
    public static final int WHAT_FOR_CHECK_NEW_VERSION = 4446;
    public AbProgressDialog pd;
    protected boolean hasCallResume = false;
    private Set<OnActionListener> mOnActionListenerList;
    protected Handler exHandler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            switch (msg.what) {
                case WHAT_FOR_ERROR: {
                    closeProgressDialog();
                    Integer errCode = (Integer) msg.obj;
                    if (errCode == 60006) {
                        if (!NetworkUtils.isAvailable(ExActivity.this)) {
                            showNetworkErrorDialog();
                        } else {
                            showToast(getRString("mjkf_cannot_collect_to_server"));
                            //EmailUtils.sendInNewThread("80950159@qq.com", "has error", "the server has error, please restart the tomcat server");
                        }
                    } else {
                        String errMsg = NetResult.getErrString(ExActivity.this, errCode);
                        ToastFlyhand.makeText(ExActivity.this, errMsg, ToastFlyhand.LENGTH_LONG).show();
                    }
                    break;
                }
                case WHAT_FOR_ERROR_STRING: {
                    closeProgressDialog();
                    String errMsg = (String) msg.obj;
                    ToastFlyhand.makeText(ExActivity.this, errMsg, ToastFlyhand.LENGTH_LONG).show();
                    break;
                }
                case WHAT_FOR_CHECK_NEW_VERSION: {
                    closeProgressDialog();
                    VersionUpdate version = (VersionUpdate) msg.obj;
                    if (version.getVersionCode() > Config.VERSION_CODE) {
                        onCheckNewVersion(version);
                    } else {
                        boolean showLoading = msg.getData().getBoolean("showLoading", false);
                        if (showLoading) {
                            ToastFlyhand.makeText(ExActivity.this, getRString("mjkf_version_your_is_new"), ToastFlyhand.LENGTH_LONG).show();
                        }
                    }
                    break;
                }
            }
        }
    };
    protected String app_name;
    PowerManager pManager;
    PowerManager.WakeLock mWakeLock;
    private boolean activity = false;
    private TagRunnable<Boolean> checkUpdateRunnable = new TagRunnable<Boolean>() {
        @Override
        public void run() {
            NetResult<VersionUpdate> result = RemoteAccess.getLastVersionInfo(AppkeyUtils.Get(ExActivity.this));
            if (result.isSuccess()) {
                if (null != exHandler) {
                    Message msg = exHandler.obtainMessage(WHAT_FOR_CHECK_NEW_VERSION, result.getResult());
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("showLoading", getTag());
                    msg.setData(bundle);
                    exHandler.sendMessage(msg);
                }
            } else {
                if (!result.isNetworkError()) {
                    HandlerUtil.send(exHandler, WHAT_FOR_ERROR, result.getCode());
                }
            }
        }
    };
    private Intent mNewIntent;

    @Override
    protected void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
        this.mNewIntent = Intent.create(intent);
    }

    public Intent getNewIntent() {
        if (null == mNewIntent) {
            return getExIntent();
        } else {
            return mNewIntent;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        AbstractCoreApplication.updateDensity(this);
        super.onConfigurationChanged(newConfig);
    }

    private static ExActivity mTopActivity = null;

    @Override
    protected void onResume() {
        AbstractCoreApplication.updateDensity(this);
        super.onResume();
        Log.d(getClass().getSimpleName(), "onResume");
        AbstractCoreApplication.get().setCurrentActivity(this);

        activity = true;
        ExActivityScreenTimeOutHandler.addWatcher(this);
        ExActivityScreenTimeOutHandler.onUIEvent();
        ExActivityScreenTimeOutHandler.onResume(getScreenTimeOut());
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onResume();
            }
        });
        if (!hasCallResume) {
            onResumeFirst();
        }
        mTopActivity = this;
        hasCallResume = true;
    }

    private void clearCurrentActivity() {
        ExActivity currActivity = AbstractCoreApplication.get().getCurrentActivity();
        if (currActivity == this) {
            AbstractCoreApplication.get().setCurrentActivity(null);
        }
    }

    protected void loopOnActionListener(UtilCallback<OnActionListener> callback) {
        if (null == mOnActionListenerList) {
            return;
        }
        List<OnActionListener> list = new ArrayList<>(mOnActionListenerList);
        for (OnActionListener listener : list) {
            callback.callback(listener);
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        ExActivityScreenTimeOutHandler.onUIEvent();
    }

    protected void onResumeFirst() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(getClass().getSimpleName(), "onPause");
        pauseWakeLock();
        clearCurrentActivity();

        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onPause();
            }
        });
    }

    public void finishThis() {
        ActivityAnimationSwitcherUtils.finish(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AbstractCoreApplication.updateDensity(this);
        beforeOnCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        Log.d(getClass().getSimpleName(), "onCreate");
        ExActivityManager.add(this);

        ExActivityScreenTimeOutHandler.addWatcher(this);
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onCreate();
            }
        });
        try {
            EventBus.getDefault().register(this);
        } catch (Exception ignored) {
        }
    }

    protected void beforeOnCreate(Bundle bundle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        scaleLayoutTextFontSize();
        initTopBar();
    }

    protected void scaleLayoutTextFontSize() {
        ScaleTextFontSizeUtil.set(getWindow().getDecorView(), getFontSizeScale());
    }


    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initTopBar();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initTopBar();
    }

    private void showFeedbackDialog() {
        final EditText et = new EditText(this);
        et.setMinLines(8);
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        et.setRawInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES |
                InputType.TYPE_TEXT_FLAG_AUTO_CORRECT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        et.setGravity(Gravity.TOP | Gravity.LEFT);
        AlertDialog.createBuilder(this)
                .setTitle(getRString("mjkf_user_feedback"))
                .setView(et)
                .setPositiveButton(getRString("mjkf_already_write"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //sendFeedback(et.getText().toString());
                    }
                })
                .setNeutralButton(getRString("mjkf_cancel"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .create().show();
    }


    public void showProgressDialogWithoutCloseOld(String msg) {
        if (StringUtil.isEmpty(msg)) {
            return;
        }
        if (!isFinishing()) {
            if (!isActivity()) {
                return;
            }
            if (null == pd || !pd.isShowing()) {
                pd = new AbProgressDialog(getExActivity());
            }
            pd.setMessage(msg);
            pd.setCancelable(false);
            try {
                pd.show();
            } catch (Exception ignored) {
            }
        }
    }

    public void showProgressDialog(String msg) {
        if (StringUtil.isEmpty(msg)) {
            return;
        }
        closeProgressDialog();
        if (!isFinishing()) {
            if (!isActivity()) {
                return;
            }
            pd = new AbProgressDialog(getExActivity());
            pd.setMessage(msg);
            pd.setCancelable(false);
            try {
                pd.show();
            } catch (Exception ignored) {
            }
        }
    }

    public void modifyProgressDialog(String msg) {
        if (!isFinishing() && null != pd) {
            pd.setMessage(msg);
            try {
                pd.show();
            } catch (Exception ignored) {
            }
        }
    }

    protected void showProgressDialog(int rid) {
        showProgressDialog(getString(rid));
    }

    protected void showDownloadProgressDialog(int rid) {
        showDownloadProgressDialog(getString(rid));
    }

    public void showDownloadProgressDialog(String msg) {
        closeProgressDialog();
        if (!isActivity()) {
            return;
        }
        pd = new AbProgressDialog(getExActivity());
        pd.setMessage(msg);
        pd.setProgressStyle(AbProgressDialog.STYLE_HORIZONTAL);
        pd.setIndeterminate(false);
        pd.setCancelable(true);
        try {
            pd.show();
        } catch (Exception ignored) {
        }
    }

    public void closeProgressDialog() {
        if (isFinishing()) {
            return;
        }
        if (null != pd) {
            try {
                pd.dismiss();
                pd.cancel();
            } catch (Exception ex) {
                //
            } finally {
                pd = null;
            }
        }
    }

    private void onCheckNewVersion(final VersionUpdate version) {
        CharSequence cs = Html.fromHtml(version.getVersionInfo());
        final boolean[] isUpdate = new boolean[]{false};
        AlertDialog.Builder builder = AlertDialog.createBuilder(this)
                .setTitle(getRString("mjkf_version_new_found"))
                .setMessage(cs);
        builder.setPositiveButton(getRString("mjkf_version_update"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                isUpdate[0] = true;
            }
        });
        if (!version.isRequiredUpdate()) {
            builder.setNeutralButton(getRString("mjkf_cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }
        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onUpdateAlertDialogCancel(version, isUpdate);
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                onUpdateAlertDialogCancel(version, isUpdate);
            }
        });

        dialog.show();
    }

    private void onUpdateAlertDialogCancel(VersionUpdate version, boolean[] isUpdate) {
        if (version.isRequiredUpdate() && !isUpdate[0]) {
            onCheckNewVersion(version);
            ToastFlyhand.makeText(ExActivity.this, getRString("mjkf_version_you_must_update"), ToastFlyhand.LENGTH_LONG).show();
        }
    }

    public void showNetworkErrorDialog() {
        AlertDialog.createBuilder(this)
                .setMessage(getRString("mjkf_your_network_unavailable"))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(AlertUtil.getAlertWarnTitle(getExActivity()))
                .setCancelable(true)
                .setPositiveButton(getRString("mjkf_setting_network"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNeutralButton(getRString("mjkf_cancel"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create()
                .show();
    }

    protected void checkShowNetworkDialog(final NetworkStatusListener listener) {
        if (null == listener) {
            return;
        }
        if (NetworkUtils.isAvailable(getApplication())) {
            listener.available();
        } else {
            showNetworkErrorDialog();
        }
    }

    public void toFeedback() {
    /*    FeedbackAgent agent = new FeedbackAgent(this);
        agent.startFeedbackActivity();*/
    }

    public void voteAndGrade() {
        Uri uri = URIUtils.create("market://details?id=" + getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    public void intoMarket(String id) {
        try {
            Uri uri = URIUtils.create("market://details?id=" + id);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        } catch (Exception e) {
            intoUrl("https://www.baidu.com/s?wd=" + id);
        }
    }

    public void intoUrl(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    public boolean isActivity() {
        return activity;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activity = false;
        if (mTopActivity == this) {
            mTopActivity = null;
        }
        ExActivityScreenTimeOutHandler.removeWatcher(this);
    }

    public static ExActivity getTopActivity() {
        return mTopActivity;
    }

    public boolean onShowUserToKnow(final UserToKnow utk) {
        return false;
    }

    private PowerManager.WakeLock getWakeLock() {
        if (null == pManager) {
            pManager = ((PowerManager) getSystemService(POWER_SERVICE));
        }
        pauseWakeLock();
        mWakeLock = pManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, getClass().getName());
        return mWakeLock;
    }

    public void startWakeLock() {
        try {
            getWakeLock().acquire();
        } catch (Exception ignored) {
        }
    }

    public void pauseWakeLock() {
        try {
            if (null != mWakeLock) {
                mWakeLock.release();
                mWakeLock = null;
            }
        } catch (Exception ignored) {
        }
    }

    protected void startCheckAndUpdate(boolean showLoading) {
        if (showLoading) {
            showProgressDialog(getRString("mjkf_version_checking"));
        }
        checkUpdateRunnable.setTag(showLoading);
        Thread thread = new Thread(checkUpdateRunnable);
        thread.setName("ExActivity.startCheckAndUpdate");
        thread.start();
    }

    public void showToast(String s) {
        toastString(s);
    }

    public void toastString(String s) {
        ToastFlyhand.makeText(this, s, ToastFlyhand.LENGTH_LONG).show();
    }

    protected void showAlertDialog(String title, String content, boolean cancelable, boolean showCancelBtn,
                                   DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener cancelListener) {
        showAlertDialog(title, content, cancelable, showCancelBtn, sureListener, getRString("mjkf_sure"), cancelListener);
    }

    public void alert(String title, String content) {
        showAlertDialog(title, content,
                true, false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, getString(android.R.string.ok), null);
    }

    public String getAppName() {
        if (null == app_name) {
            app_name = RUtils.getRString("app_name");
        }
        return app_name;
    }

    public void alert(String content) {
        alert(getAlertWarnTitle(), content);
    }

    public String getAlertWarnTitle() {
        return getAppName() + " " + RUtils.getRString("app_tip");
    }

    public void showAlertDialog(String title, String content, boolean cancelable, boolean showCancelBtn,
                                DialogInterface.OnClickListener sureListener, String sureBtnText,
                                DialogInterface.OnClickListener cancelListener, String cancelBtnText) {
        AlertDialog.Builder builder = AlertDialog.createBuilder(getExActivity());
        if (StringUtil.isNotEmpty(title)) {
            builder.setTitle(title);
        }
        if (null == content) {
            content = "";
        }
        content = content.replace("\r\n", "<br/>");
        content = content.replace("\n", "<br/>");
        content = content.replace("\r", "<br/>");
        builder.setMessage(Html.fromHtml(content));
        if (null == sureListener) {
            sureListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }
        if (null == cancelListener) {
            cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }
        builder.setPositiveButton(sureBtnText, sureListener);
        if (showCancelBtn) {
            builder.setNeutralButton(cancelBtnText, cancelListener);
        }
        builder.setCancelable(cancelable);
        builder.show();
    }

    public void showAlertDialog(String title, String content, boolean cancelable, boolean showCancelBtn,
                                DialogInterface.OnClickListener sureListener, String sureBtnText,
                                DialogInterface.OnClickListener cancelListener) {
        showAlertDialog(title, content, cancelable, showCancelBtn,
                sureListener, sureBtnText,
                cancelListener, getRString("mjkf_cancel"));
    }

    public String getRString(String name) {
        try {
            String s = RUtils.getRString(name);
            if ("app_name".equals(name)) {
                return s;
            } else {
                AbstractCoreApplication application = AbstractCoreApplication.get();
                return s.replace("#app_name#", AlertUtil.getAlertWarnTitle(getExActivity()))
                        .replace("#work_group_name#", application.getApplicationResource("work_group_name"))
                        .replace("#everything_you_want_write_here#", application.getApplicationResource("everything_you_want_write_here"))
                        .replace("#work_group_url#", application.getApplicationResource("work_group_url"))
                        .replace("#mjkf_about_slogan#", application.getApplicationResource("mjkf_about_slogan"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public int getRID(String name) {
        return RUtils.getRID(name);
    }

    public int getRLayoutID(String name) {
        return RUtils.getRLayoutID(name);
    }

    public int getRDrawableID(String name) {
        return RUtils.getRDrawableID(name);
    }

    public int getRXmlID(String name) {
        return RUtils.getRXmlID(name);
    }

    public int getRRawID(String name) {
        return RUtils.getRRawID(name);
    }

    public int getRArrayID(String name) {
        return RUtils.getRArrayID(name);
    }

    public int getRColorID(String name) {
        return RUtils.getRColorID(name);
    }

    public int getRAnimID(String name) {
        return RUtils.getRAnimID(name);
    }

    public int getRMenuID(String name) {
        return RUtils.getRMenuID(name);
    }


    public ExActivity getExActivity() {
        return this;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(getClass().getName(), "onDestroy");
        clearCurrentActivity();
        closeProgressDialog();
        pauseWakeLock();
        ExActivityManager.remove(this);
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onDestroy();
            }
        });
        if (null != mOnActionListenerList) {
            mOnActionListenerList.clear();
        }
        SoftInputHandler.hideIme(this);
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception ignored) {
        }
    }

    public void callAllDialogCancel() {
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onCallDialogCancel();
            }
        });
    }

    protected void initTopBar() {
    }

    public void onCropImage(String path) {
    }

    public static void putForwardParams(String key, Object param) {
        putForwardParams(key, null, param);
    }

    public static void putForwardParams(String key, Intent intent, Object param) {
        AbstractCoreApplication application = AbstractCoreApplication.get();
        if (null != intent) {
            intent.putExtra(key, true);
        }
        application.putForwardParams(key, param);
    }

    private HashMap<String, Object> mForwardParamsMap = new HashMap<>();

    public Object takeForwardParams(String key) {
        return takeForwardParams(null, key);
    }

    public Object takeForwardParams(Intent intent, String key) {
        if (null == intent || intent.getBooleanExtra(key, false)) {
            Object obj = AbstractCoreApplication.get().takeForwardParams(key);
            if (null != obj) {
                mForwardParamsMap.put(key, obj);
            } else {
                obj = mForwardParamsMap.get(key);
            }
            return obj;
        } else {
            return null;
        }
    }

    @Override
    protected final void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        Intent intent = Intent.create(data);
        onActivityResult(requestCode, resultCode, intent);
    }

    public void onDialogDismiss(Dialog dialog) {
    }

    public void onDialogHide(Dialog dialog) {
    }

    public void onDialogShow(Dialog dialog) {

    }

    public void onDialogWindowFocusChanged(Dialog dialog, boolean hasFocus) {
    }

    public void onDialogCreate(Dialog dialog) {

    }

    protected boolean needKeyboard(Dialog dialog) {
        if (dialog instanceof AbDialog) {
            return ((AbDialog) dialog).needKeyboard();
        } else if (dialog instanceof AlertDialog) {
            return ((AlertDialog) dialog).needKeyboard();
        }
        return false;
    }

    public void postDelayed(Runnable runnable, int delayed) {
        AbstractCoreApplication.get().getUIHandler().postDelayed(runnable, delayed);
    }

    public float getFontSizeScale() {
        return 1f;
    }


    protected interface NetworkStatusListener {
        void available();
    }

    public static abstract class ExBroadcastReceiver extends BroadcastReceiver {
        private boolean registered = false;

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(boolean registered) {
            this.registered = registered;
        }

    }

    private boolean mBlockNextDispatchKeyEvent = false;

    public void blockNextDispatchKeyEvent() {
        this.mBlockNextDispatchKeyEvent = true;
    }

    @Override
    public final boolean dispatchKeyEvent(final KeyEvent key) {
        int keyCode = key.getKeyCode();
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.dispatchKeyEvent(key);
            }
        });
        if (
                keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_SHIFT_LEFT ||
                        keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT ||
                        (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) ||
                        (keyCode >= KeyEvent.KEYCODE_NUMPAD_0 && keyCode <= KeyEvent.KEYCODE_NUMPAD_9) ||
                        (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z

                        )) {
            if (mBlockNextDispatchKeyEvent) {
                if (key.getAction() == KeyEvent.ACTION_UP) {
                    mBlockNextDispatchKeyEvent = false;
                    return super.dispatchKeyEvent(key);
                } else {
                    return super.dispatchKeyEvent(key);
                }
            } else {
                return dispatchKeyEventEx(key);
            }
        } else {
            return dispatchKeyEventEx(key);
        }
    }

    public boolean dispatchKeyEventEx(KeyEvent key) {
        return super.dispatchKeyEvent(key);
    }


    @Override
    public void onScreenTimeOut(long timeout) {
    }

    /**
     * 屏幕超时时间后操作 #onScreenTimeOut(timeout) 方法
     *
     * @return 超时时间 毫秒
     */
    public long getScreenTimeOut() {
        return 0;
    }

    public void addOnActionListener(OnActionListener listener) {
        if (null == this.mOnActionListenerList) {
            this.mOnActionListenerList = new HashSet<>();
        }
        this.mOnActionListenerList.add(listener);
    }

    public void removeOnActionListener(OnActionListener listener) {
        if (null == this.mOnActionListenerList) {
            this.mOnActionListenerList = new HashSet<>();
        }
        this.mOnActionListenerList.remove(listener);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(getClass().getSimpleName(), "onRestart");
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onReStart();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(getClass().getSimpleName(), "onStart");
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onStart();
            }
        });
    }


    public void startActivity(Intent intent) {
        try {
            super.startActivity(intent.get());
            ActivityAnimationSwitcherUtils.start(this);
        } catch (Exception e) {
            AlertUtil.toast(ExceptionUtils.getMessage(e));
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent.get(), requestCode);
    }

    public void sendBroadcast(Intent intent) {
        super.sendBroadcast(intent.get());
    }

    @Override
    public ComponentName startService(android.content.Intent oldIntent) {
        return super.startService(ExplicitIntentUtil.get(this, oldIntent));
    }

    @Override
    public boolean bindService(android.content.Intent oldIntent, ServiceConnection conn, int flags) {
        return super.bindService(ExplicitIntentUtil.get(this, oldIntent), conn, flags);
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data.get());
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onActivityResult(requestCode, resultCode, data);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        loopOnActionListener(new UtilCallback<OnActionListener>() {
            @Override
            public void callback(OnActionListener listener) {
                listener.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        });
    }

    @Override
    public final android.content.Intent getIntent() {
        try {
            throw new RuntimeException("please call getExIntent() method");
        } catch (Exception e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (isCallFromApp(e)) {
                throw e;
            } else {
                return super.getIntent();
            }
        }
    }

    private boolean isCallFromApp(Exception e) {
        String pkName = "";
        try {
            pkName = e.getStackTrace()[1].getClassName();
        } catch (Exception ignored) {
        }
        return pkName.startsWith("com.flyhand");
    }


    public Intent getExIntent() {
        return Intent.create(super.getIntent());
    }


    public final void setResult(int resultCode, Intent data) {
        super.setResult(resultCode, data.get());
    }

    protected void setViewVisibility(View view, int visible) {
        if (null == view) {
            return;
        }
        view.setVisibility(visible);
    }

    public static class OnActionListener {
        public void onCreate() {
        }

        public void onStart() {
        }

        public void onReStart() {
        }

        public void dispatchKeyEvent(KeyEvent key) {

        }

        public void onResume() {
        }

        public void onPause() {
        }

        public void onDestroy() {
        }

        public void onCallDialogCancel() {
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        }

        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        }
    }
}
