package com.flyhand.yunpos.handler;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 13-9-17
 * Time: 下午2:30
 */
public class SoftInputHandler {
    public static void requestFocusAndShow(final EditText edit, int delayed) {
        new Handler() {
            @Override
            public void handleMessage(Message msg) {
                SoftInputHandler.requestFocusAndShow(edit);
            }
        }.sendEmptyMessageDelayed(0, delayed);
    }

    public static void requestFocusAndShow(EditText edit) {
        edit.setFocusable(true);
        edit.requestFocus();
        edit.setFocusableInTouchMode(true);
        InputMethodManager inputManager =
                (InputMethodManager) edit.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(edit, 0);
    }

    public static void hide(EditText edit) {
        InputMethodManager inputManager =
                (InputMethodManager) edit.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(edit.getWindowToken(), 0);
    }

    public static boolean hide(Activity activity) {
        InputMethodManager inputManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            return false;
        }
        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean hide(Dialog dialog) {
        Context context = dialog.getContext();
        InputMethodManager inputManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = dialog.getCurrentFocus();
        if (view == null) {
            return false;
        }
        return inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean hideIme(Activity activity) {
        if (activity == null || activity.getWindow() == null || activity.getWindow().getDecorView() == null
                || activity.getWindow().getDecorView().getWindowToken() == null) {
            return true;
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
       return imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }
}
