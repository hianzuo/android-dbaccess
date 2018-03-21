package com.flyhand.core.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;

import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.yunpos.utils.UtilCallback;

import java.util.HashMap;

/**
 * Created by Ryan
 * On 2016/8/26.
 */
public class ViewUtilsBase {
    public static int GetResourcesColor(Resources res, int cId) {
        return res.getColor(cId);
    }

    private static float _1dp = 0;
    private static float _1sp = 0;

    public static int getDipPx(Resources res, float dp) {
        if (_1dp <= 0) {
            _1dp = ConvertDimension(res, "1dip");
        }
        if (dp != 0) {
            return (int) (_1dp * dp);
        }
        return 0;
    }

    public static int getSpPx(Resources res, float sp) {
        if (_1sp <= 0) {
            _1sp = ConvertDimension(res, "1sp");
        }
        if (sp != 0) {
            return (int) (_1sp * sp);
        }
        return 0;
    }

    public static float ConvertDimension(Resources res, String st) {
        st = st.toLowerCase();
        DisplayMetrics metrics = res.getDisplayMetrics();
        float value = ConvertDimensionPixel(st, metrics, "px", TypedValue.COMPLEX_UNIT_PX);
        if (value > -1) {
            return value;
        }
        value = ConvertDimensionPixel(st, metrics, "dip", TypedValue.COMPLEX_UNIT_DIP);
        if (value > -1) {
            return value;
        }
        value = ConvertDimensionPixel(st, metrics, "sp", TypedValue.COMPLEX_UNIT_SP);
        if (value > -1) {
            return value;
        }
        value = ConvertDimensionPixel(st, metrics, "pt", TypedValue.COMPLEX_UNIT_PT);
        if (value > -1) {
            return value;
        }
        value = ConvertDimensionPixel(st, metrics, "in", TypedValue.COMPLEX_UNIT_IN);
        if (value > -1) {
            return value;
        }
        value = ConvertDimensionPixel(st, metrics, "mm", TypedValue.COMPLEX_UNIT_MM);
        if (value > -1) {
            return value;
        }
        return 0;
    }

    public static float ConvertDimensionPixel(String st, DisplayMetrics metrics, String unitStr, int unitInt) {
        if (st.endsWith(unitStr)) {
            float value = Float.valueOf(st.substring(0, st.length() - unitStr.length()));
            return TypedValue.applyDimension(unitInt, value, metrics);
        }
        return -1;
    }


    public static Drawable getDrawable(int resId) {
        return AbstractCoreApplication.get().getResources().getDrawable(resId);
    }

    public static Drawable createSelector(int normalResId, int selectedResId, int pressedResId) {
        final Drawable normal = 0 == normalResId ? null : getDrawable(normalResId);
        final Drawable selected = 0 == selectedResId ? null : getDrawable(selectedResId);
        final Drawable pressed = 0 == pressedResId ? null : getDrawable(pressedResId);
        return new Selector(AbstractCoreApplication.get()).setState(normal, selected, pressed);
    }

    public static void setVisibility(View view, int visible) {
        if (null == view) {
            return;
        }
        if (visible != view.getVisibility()) {
            view.setVisibility(visible);
        }
    }

    public static boolean isVisible(View view) {
        return null != view && View.VISIBLE == view.getVisibility();
    }

    public static void RemoveParent(View view) {
        ViewParent vp = view.getParent();
        if (null != vp) {
            ((ViewGroup) vp).removeView(view);
        }
    }

    public static void setPadding(View view, int dp) {
        int dipPx = getDipPx(view.getContext().getResources(), dp);
        view.setPadding(dipPx, dipPx, dipPx, dipPx);
    }

    public static void setPadding(View view, int left, int top, int right, int bottom) {
        int leftDipPx = getDipPx(view.getContext().getResources(), left);
        int topDipPx = getDipPx(view.getContext().getResources(), left);
        int rightDipPx = getDipPx(view.getContext().getResources(), left);
        int bottomDipPx = getDipPx(view.getContext().getResources(), left);
        view.setPadding(leftDipPx, topDipPx, rightDipPx, bottomDipPx);
    }

    public static void setPaddingLeft(View view, int dp) {
        int dipPx = getDipPx(view.getContext().getResources(), dp);
        view.setPadding(dipPx, 0, 0, 0);
    }

    public static void setPaddingBottom(View view, int dp) {
        int dipPx = getDipPx(view.getContext().getResources(), dp);
        view.setPadding(0, 0, 0, dipPx);
    }

    protected static class Selector extends View {
        public Selector(Context context) {
            super(context);
        }

        // 以下这个方法也可以把你的图片数组传过来，以StateListDrawable来设置图片状态，来表现button的各中状态。未选
        // 中，按下，选中效果。
        public StateListDrawable setState(Drawable normal, Drawable selected, Drawable pressed) {
            StateListDrawable drawable = new StateListDrawable();
            if (null != pressed) {
                drawable.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
            }
            if (null != selected) {
                drawable.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
                drawable.addState(View.FOCUSED_STATE_SET, selected);
            }
            if (null != normal) {
                drawable.addState(View.ENABLED_STATE_SET, normal);
                drawable.addState(View.EMPTY_STATE_SET, normal);
            }
            return drawable;
        }
    }

    public static void getWidthHeight(final View view, final UtilCallback<HashMap<String, Integer>> callback) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (view.getMeasuredWidth() > 0) {
                    view.getViewTreeObserver().removeOnPreDrawListener(this);

                    HashMap<String, Integer> map = new HashMap<>();
                    map.put("width", view.getMeasuredWidth());
                    map.put("height", view.getMeasuredHeight());
                    callback.callback(map);
                }
                return true;
            }

        });
    }
}
