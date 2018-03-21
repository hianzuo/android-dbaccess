package com.flyhand.yunpos.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.BaseContextUtil;
import com.flyhand.core.utils.RUtils;
import com.flyhand.yunpos.handler.SoftInputHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-12-25
 * Time: 下午5:33
 */
public abstract class AbAlertDialog extends android.app.AlertDialog {
    protected boolean mNeedKeyboard = false;
    protected AbAlertDialog(Context context) {
        super(context);
    }

    protected AbAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected AbAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
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
            AbstractCoreApplication.get().getUIHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    superCancel();
                }
            }, 300);
        } else {
            superCancel();
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogWindowFocusChanged(this,hasFocus);
        }
    }

    public boolean needKeyboard() {
        return mNeedKeyboard;
    }

    protected Context getBaseContext() {
        return BaseContextUtil.get(getContext());
    }

    public AbAlertDialog getDialog() {
        return this;
    }

    public void superCancel() {
        try {
            super.cancel();
        } catch (Exception ignored) {
        }
    }

    protected void superShow() {
        try {
            super.show();
        } catch (Exception ignored) {
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

    public static interface OnClose {
        void onClose(Dialog dialog);
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
    public void dismiss() {
        super.dismiss();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogDismiss(this);
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
    public void show() {
        try {
            super.show();
            Context context = getBaseContext();
            if (context instanceof ExActivity) {
                ((ExActivity) context).onDialogShow(this);
            }
        } catch (Exception ignored) {
        }
    }
}
