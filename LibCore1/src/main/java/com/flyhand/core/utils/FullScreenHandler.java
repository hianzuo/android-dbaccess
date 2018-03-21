package com.flyhand.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Ryan
 * On 2016/6/15.
 */
public class FullScreenHandler {

    public static void onPostCreate(final Activity activity) {
        try {
            final View decorView = activity.getWindow().getDecorView();
            hideNavigationBar(decorView);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // This work only for android 4.4+
                decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            hideNavigationBar(decorView);
                        }
                    }
                });
            }
        } catch (Exception ignored) {
        }
    }


    public static void onWindowFocused(Activity activity) {
        hideNavigationBar(activity.getWindow().getDecorView());
    }

    public static void onWindowFocused(Dialog dialog) {
        hideNavigationBar(dialog.getWindow().getDecorView());
    }

    public static void onDialogCreate(final Dialog dialog) {
        try {
            //Set the dialog to not focusable
            final Window window = dialog.getWindow();
            final View decorView = window.getDecorView();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            FullScreenHandler.hideNavigationBar(decorView);
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface d) {
                    try {
                        //Clear the not focusable flag from the window
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                        //Update the WindowManager with the new attributes
                        WindowManager wm = (WindowManager) dialog.getContext().getSystemService(Context.WINDOW_SERVICE);
                        wm.updateViewLayout(decorView, window.getAttributes());
                    } catch (Exception ignored) {
                    }
                }
            });

            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        FullScreenHandler.hideNavigationBar(decorView);
                    }
                }
            });
        } catch (Exception ignored) {
        }
    }

    public static void hideNavigationBar(View view) {
        try {
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                view.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                //for new api versions.
                view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        } catch (Exception ignored) {
        }
    }
}
