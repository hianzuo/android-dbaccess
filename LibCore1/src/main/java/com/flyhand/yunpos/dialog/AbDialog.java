package com.flyhand.yunpos.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.utils.BaseContextUtil;
import com.flyhand.core.utils.RUtils;
import com.flyhand.yunpos.handler.SoftInputHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-12-25
 * Time: 下午5:33
 */
public abstract class AbDialog extends Dialog {
    public AbDialog(Context context) {
        this(context, 0);
    }

    public AbDialog(Context context, int theme) {
        super(context, theme);
        setCancelable(false);
        cancelOnActivityDestroy();
        cancelOnActivityCallDialogCancel();
    }

    private void cancelOnActivityDestroy() {
        Context baseContext = getBaseContext();
        if (baseContext instanceof ExActivity) {
            final ExActivity activity = (ExActivity) baseContext;
            activity.addOnActionListener(new ExActivity.OnActionListener() {
                @Override
                public void onDestroy() {
                    activity.removeOnActionListener(this);
                    superCancel();
                }
            });
        }
    }

    private void cancelOnActivityCallDialogCancel() {
        Context baseContext = getBaseContext();
        if (baseContext instanceof ExActivity) {
            final ExActivity activity = (ExActivity) baseContext;
            activity.addOnActionListener(new ExActivity.OnActionListener() {
                @Override
                public void onCallDialogCancel() {
                    activity.removeOnActionListener(this);
                    superCancel();
                }
            });
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            onUserInteraction();
        }
        return super.dispatchTouchEvent(event);
    }

    protected void onUserInteraction() {
        Context context = getBaseContext();
        if (context instanceof Activity) {
            ((Activity) context).onUserInteraction();
        }
    }

    protected Context getBaseContext() {
        return BaseContextUtil.get(getContext());
    }


    @Override
    public void hide() {
        super.hide();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogHide(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogHide(this);
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogDismiss(this);
        }
    }

    @Override
    public void show() {
        super.show();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogShow(this);
        }
    }

    public void superShow() {
        super.show();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogShow(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogCreate(this);
        }
    }

    @Override
    public void cancel() {
        if (SoftInputHandler.hide(this)) {
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    superCancel();
                }
            }.sendEmptyMessageDelayed(0, 300);
        } else {
            superCancel();
        }
    }

    private void superCancel() {
        try {
            super.cancel();
        } catch (Exception e) {
            //
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogWindowFocusChanged(this, hasFocus);
        }
    }

    protected int getRID(String name) {
        return RUtils.getRID(name);
    }

    protected int getRLayoutID(String name) {
        return RUtils.getRLayoutID(name);
    }

    protected int getRDrawableID(String name) {
        return RUtils.getRDrawableID(name);
    }

    protected int getRXmlID(String name) {
        return RUtils.getRXmlID(name);
    }

    protected int getRRawID(String name) {
        return RUtils.getRRawID(name);
    }

    protected int getRArrayID(String name) {
        return RUtils.getRArrayID(name);
    }

    protected int getRColorID(String name) {
        return RUtils.getRColorID(name);
    }

    protected int getRAnimID(String name) {
        return RUtils.getRAnimID(name);
    }

    protected int getRMenuID(String name) {
        return RUtils.getRMenuID(name);
    }

    protected int getRStyleID(String name) {
        return RUtils.getRStyleID(name);
    }

    public boolean needKeyboard() {
        return false;
    }
}
