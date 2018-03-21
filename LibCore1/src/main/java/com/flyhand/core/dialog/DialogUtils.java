package com.flyhand.core.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.yunpos.dialog.AlertDialog;

import java.lang.reflect.Field;

/**
 * Ryan
 * User: Administrator
 * Date: 12-1-4
 * Time: Afternoon 10:08
 */
public class DialogUtils {

    public static void CancelDialog(DialogInterface dialog) {
        if (null == dialog) {
            return;
        }
        try {
            MakeCanCancelDialog(dialog);
            dialog.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MakeCanCancelDialog(DialogInterface dialog) {
        try {
            Field field = Dialog.class.getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void MakeNotCancelDialog(DialogInterface dialog) {
        try {
            Field field = Dialog.class.getDeclaredField("mShowing");
            field.setAccessible(true);
            field.set(dialog, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ShowRichMutilineEditDialog(ExActivity context, final OnDialogBtnClickListener clickListener, String title, String defValue, String... buttons) {
        if (null == title) {
            title = "";
        }
        String btn1 = null, btn2 = null, btn3 = null;
        if (buttons.length > 0) {
            btn1 = buttons[0];
        }
        if (buttons.length > 1) {
            btn2 = buttons[1];
        }
        if (buttons.length > 2) {
            btn3 = buttons[2];
        }
        final EditText et = new EditText(context);
        et.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
        et.setSingleLine(false);
        et.setMinLines(6);
        et.setText(defValue);
        et.setMinLines(8);
        et.setGravity(Gravity.TOP | Gravity.LEFT);
        AlertDialog.Builder builder = AlertDialog.createBuilder(context)
                .setTitle(title)
                .setView(et);
        if (null != btn1) {
            builder.setPositiveButton(btn1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (null != clickListener) {
                        clickListener.onBtn1Click(dialog, et, et.getText().toString());
                    }
                }
            });
        }
        if (null != btn2) {
            builder.setNeutralButton(btn2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (null != clickListener) {
                        clickListener.onBtn1Click(dialog, et, et.getText().toString());
                    }
                }
            });
        }
        if (null != btn3) {
            builder.setNegativeButton(btn3, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    if (null != clickListener) {
                        clickListener.onBtn1Click(dialog, et, et.getText().toString());
                    }
                }
            });
        }
        builder.show();
    }


    public static abstract class OnDialogBtnClickListener {
        public void onBtn1Click(DialogInterface dialog, View view, String note) {
        }

        public void onBtn2Click(DialogInterface dialog, View view, String note) {
        }

        public void onBtn3Click(DialogInterface dialog, View view, String note) {
        }
    }
}
