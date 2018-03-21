package com.flyhand.core.widget;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.flyhand.core.R;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.RUtils;
import com.flyhand.core.utils.ViewUtilsBase;

/**
 * Created by Ryan
 * On 15/8/12.
 */
public class ToastFlyhand extends Toast {
    public ToastFlyhand(Context context) {
        super(context);
    }

    public static void toast(String msg) {
        toast(msg, ToastFlyhand.LENGTH_SHORT);
    }

    public static void toastGreen(String msg) {
        toast(msg, ToastFlyhand.LENGTH_SHORT, R.drawable.yunpos_toast_flyhand_bg_green);
    }

    public static void toast(String msg, int duration) {
        final Toast toast = createToast(AbstractCoreApplication.get(), msg, duration);
        toast.show();
    }

    public static void toast(String msg, int duration, int bgRes) {
        final Toast toast = createToast(AbstractCoreApplication.get(), msg, duration, bgRes);
        toast.show();
    }

    private static Toast createToast(Context context, String msg, int duration, int bgRes) {
        if (null == msg || "null".equals(msg)) {
            Integer.parseInt("a");
        }
        View view = View.inflate(context, R.layout.yunpos_toast_flyhand, null);
        View toastView = view.findViewById(RUtils.getRID("ll_toast"));
        if (bgRes != 0) {
            toastView.setBackgroundResource(bgRes);
        }
        TextView message = (TextView) toastView.findViewById(RUtils.getRID("tv_message"));
        message.setText(msg);
        TextView fav_logo = (TextView) toastView.findViewById(RUtils.getRID("fav_logo"));
        if (bgRes == R.drawable.yunpos_toast_flyhand_bg_green) {
            fav_logo.setText(R.string.fa_check_circle);
        } else {
            fav_logo.setText(R.string.fa_info_circle);
        }
        final Toast toast = new Toast(context);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(duration);
        toast.setView(view);
        return toast;
    }

    private static int getToastMarginTop(Context context) {
        Resources resources = context.getResources();
        int heightPixels = resources.getDisplayMetrics().heightPixels;
        int viewHeight = ViewUtilsBase.getDipPx(resources, 120);
        int adjustHeight = ViewUtilsBase.getDipPx(resources, 80);
        return heightPixels / 2 - viewHeight / 2 - adjustHeight;
    }

    private static Toast createToast(Context context, String msg, int duration) {
        return createToast(context, msg, duration, R.drawable.yunpos_toast_flyhand_bg);
    }

    public static void toastLong(String msg) {
        toast(msg, ToastFlyhand.LENGTH_LONG);
    }

    public static void toastLongGreen(String msg) {
        toast(msg, ToastFlyhand.LENGTH_LONG, R.drawable.yunpos_toast_flyhand_bg_green);
    }

    public static Toast makeText(Context context, String msg, int duration) {
        return createToast(context, msg, duration);
    }

    public static Toast makeTextGreen(Context context, String msg, int duration) {
        return createToast(context, msg, duration, R.drawable.yunpos_toast_flyhand_bg_green);
    }
}
