package com.flyhand.core.utils;

import android.app.Activity;
import android.app.Dialog;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Ryan
 * On 2016/8/24.
 */
public class ScaleTextFontSizeUtil {
    public static void set(Activity activity, float scale) {
        if (null == activity) {
            return;
        }
        set(activity.getWindow().getDecorView(), scale);
    }

    public static void set(Dialog dialog, float scale) {
        if (null == dialog) {
            return;
        }
        Window window = dialog.getWindow();
        if (null != window) {
            set(window.getDecorView(), scale);
        }
    }

    public static void set(View view, float scale) {
        if (scale == 1f) {
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            int childCount = viewGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childView = viewGroup.getChildAt(i);
                ScaleTextFontSizeUtil.set(childView, scale);
            }
        } else {
            if (view instanceof TextView) {
                TextView textView = (TextView) view;
                float textSize = textView.getTextSize();
                float size = textSize * scale;
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
            }
        }
    }
}
