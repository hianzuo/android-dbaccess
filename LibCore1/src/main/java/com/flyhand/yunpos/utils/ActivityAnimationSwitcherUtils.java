package com.flyhand.yunpos.utils;

import android.app.Activity;

import com.flyhand.core.utils.RUtils;

/**
 * User: Ryan
 * Date: 13-8-6
 * Time: 下午4:45
 */
public class ActivityAnimationSwitcherUtils {
    public static void start(Activity activity) {
        activity.overridePendingTransition(RUtils.getRAnimID("slide_in_from_right"),
                RUtils.getRAnimID("slide_out_from_left"));
    }

    public static void finish(Activity activity) {
        activity.finish();
        activity.overridePendingTransition(RUtils.getRAnimID("slide_in_from_left"),
                RUtils.getRAnimID("slide_out_from_right"));
    }
}
