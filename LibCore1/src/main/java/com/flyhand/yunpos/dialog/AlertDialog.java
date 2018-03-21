package com.flyhand.yunpos.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.utils.RUtils;
import com.flyhand.core.utils.ScaleTextFontSizeUtil;
import com.flyhand.core.utils.ViewUtilsBase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 14-1-25
 * Time: 下午3:53
 */
public class AlertDialog extends AbAlertDialog implements View.OnClickListener {
    private AlertParams AP;
    private ExActivity activity;
    private Holder holder;

    private AlertDialog(ExActivity activity, AlertParams AP, int theme) {
        super(activity, theme);
        this.activity = activity;
        this.AP = AP;
        View root = View.inflate(activity, RUtils.getRLayoutID("core_dialog_ex_alert"), null);
        holder = initHolder(root);
        if (null != AP.mPositiveButtonText) {
            holder.button1container.setVisibility(View.VISIBLE);
            holder.button1.setOnClickListener(this);
            holder.button1.setText(AP.mPositiveButtonText);
            ViewUtilsBase.setVisibility(holder.button_sp_h, View.VISIBLE);
        }
        if (null != AP.mNeutralButtonText) {
            holder.button2container.setVisibility(View.VISIBLE);
            holder.button2.setOnClickListener(this);
            holder.button2.setText(AP.mNeutralButtonText);
            ViewUtilsBase.setVisibility(holder.button_sp_h, View.VISIBLE);
            ViewUtilsBase.setVisibility(holder.button1sp, View.VISIBLE);
        }
        if (null != AP.mNegativeButtonText) {
            holder.button3container.setVisibility(View.VISIBLE);
            holder.button3.setOnClickListener(this);
            holder.button3.setText(AP.mNegativeButtonText);
            ViewUtilsBase.setVisibility(holder.button_sp_h, View.VISIBLE);
            ViewUtilsBase.setVisibility(holder.button2sp, View.VISIBLE);
        }
        if (null != AP.mButtonTextSize && AP.mButtonTextSize > 0) {
            holder.button1.setTextSize(AP.mButtonTextSize);
            holder.button2.setTextSize(AP.mButtonTextSize);
            holder.button3.setTextSize(AP.mButtonTextSize);
        }
        if (null != AP.mMessage) {
            TextView mMessageView = new TextView(AP.mContext);
            mMessageView.setTextSize(20f);
            int _12dp = ViewUtilsBase.getDipPx(AP.mContext.getResources(), 12);
            mMessageView.setPadding(_12dp, _12dp, _12dp, _12dp);
            mMessageView.setText(AP.mMessage);
            holder.view_c.addView(mMessageView, new LinearLayout.LayoutParams(-1, -2));
        }
        if (null != AP.mView) {
            ViewUtilsBase.RemoveParent(AP.mView);
            LinearLayout.LayoutParams layoutParams = AP.mViewLayoutParams;
            if (null == layoutParams) {
                layoutParams = new LinearLayout.LayoutParams(-1, -2);
            }
            holder.view_c.addView(AP.mView, layoutParams);
        }
        setView(root);
    }


    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        AP.mPositiveButtonText = text;
        AP.mPositiveButtonListener = listener;
        holder.button1.setText(AP.mPositiveButtonText);
    }

    public void setNeutralButton(CharSequence text, OnClickListener listener) {
        AP.mNeutralButtonText = text;
        AP.mNeutralButtonListener = listener;
        holder.button2.setText(AP.mNeutralButtonText);
    }

    public void setNegativeButton(CharSequence text, OnClickListener listener) {
        AP.mNegativeButtonText = text;
        AP.mNegativeButtonListener = listener;
        holder.button2.setText(AP.mNegativeButtonText);
    }

    private Holder initHolder(View root) {
        Holder holder = new Holder();
        holder.button1 = (Button) root.findViewById(RUtils.getRID("button1"));
        holder.button1container = (ViewGroup) root.findViewById(RUtils.getRID("button1container"));
        holder.button1sp = root.findViewById(RUtils.getRID("button1sp"));
        holder.button2 = (Button) root.findViewById(RUtils.getRID("button2"));
        holder.button2container = (ViewGroup) root.findViewById(RUtils.getRID("button2container"));
        holder.button2sp = root.findViewById(RUtils.getRID("button2sp"));
        holder.button3 = (Button) root.findViewById(RUtils.getRID("button3"));
        holder.button3container = (ViewGroup) root.findViewById(RUtils.getRID("button3container"));
        holder.button_sp_h = root.findViewById(RUtils.getRID("button_sp_h"));
        holder.view_c = (LinearLayout) root.findViewById(RUtils.getRID("view_c"));
        return holder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScaleTextFontSizeUtil.set(this, activity.getFontSizeScale());
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if (vid == RUtils.getRID("button1")) {
            onButton1Clicked();
        } else if (vid == RUtils.getRID("button2")) {
            onButton2Clicked();
        } else if (vid == RUtils.getRID("button3")) {
            onButton3Clicked();
        }
    }

    private void onButton3Clicked() {
        if (null != AP.mNegativeButtonListener) {
            AP.mNegativeButtonListener.onClick(this, 0);
        }
        if (isShowing()) {
            cancel();
        }
    }

    private void onButton2Clicked() {
        if (null != AP.mNeutralButtonListener) {
            AP.mNeutralButtonListener.onClick(this, 0);
        }
        if (isShowing()) {
            cancel();
        }
    }

    private void onButton1Clicked() {
        if (null != AP.mPositiveButtonListener) {
            AP.mPositiveButtonListener.onClick(this, 0);
        }
        if (isShowing()) {
            cancel();
        }
    }

    @Override
    public boolean needKeyboard() {
        return AP.mNeedKeyboard;
    }

    public Object getTag() {
        return AP.mTag;
    }

    public static class AlertParams {
        private CharSequence mPositiveButtonText;
        private DialogInterface.OnClickListener mPositiveButtonListener;
        private CharSequence mNegativeButtonText;
        private DialogInterface.OnClickListener mNegativeButtonListener;
        private CharSequence mNeutralButtonText;
        private DialogInterface.OnClickListener mNeutralButtonListener;
        private View mView;
        private LinearLayout.LayoutParams mViewLayoutParams;
        private int mTheme;
        private ExActivity mContext;
        private Boolean mCancelable = true;
        private CharSequence mMessage;
        private Object mTag;
        private DialogInterface.OnCancelListener mOnCancelListener;
        private DialogInterface.OnKeyListener mOnKeyListener;
        private Float mButtonTextSize;
        private boolean mNeedKeyboard = false;

    }

    public static Builder createBuilder(ExActivity activity, int theme) {
        if (isExistThemeConstructor()) {
            return new Builder(activity, theme);
        } else {
            return new Builder(activity, true);
        }
    }

    public static Builder createBuilder(ExActivity activity) {
        if (isExistThemeConstructor()) {
            return new Builder(activity);
        } else {
            return new Builder(activity, true);
        }
    }

    private static boolean isExistThemeConstructor() {
        try {
            android.app.AlertDialog.Builder.class.getConstructor(Context.class, int.class);
            android.app.AlertDialog.class.getDeclaredMethod("resolveDialogTheme", Context.class, int.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static class Builder extends android.app.AlertDialog.Builder {
        private AlertParams AP;

        private Builder(ExActivity activity) {
            this(activity, resolveDialogTheme(activity));
        }


        private Builder(ExActivity activity, Boolean _2_x) {
            super(activity);
            AP = new AlertParams();
            AP.mContext = activity;
        }

        private static int resolveDialogThemeDefault(Context context) {
            if (context.getPackageName().contains("cpff")) {
                return RUtils.getRStyleID("Theme_CPFF_Dialog");
            } else {
                return RUtils.getRStyleID("Theme_CPFF_Light_Dialog");
            }
        }

        public static int resolveDialogTheme(Context context) {
            if (Integer.valueOf(Build.VERSION.SDK) > 10) {
                try {
                    Method method = android.app.AlertDialog.class.getDeclaredMethod("resolveDialogTheme",
                            Context.class, int.class);
                    method.setAccessible(true);
                    return (Integer) method.invoke(null, context, 0);
                } catch (Exception e) {
                    return resolveDialogThemeDefault(context);
                }
            } else {
                return resolveDialogThemeDefault(context);
            }
        }

        public Builder(ExActivity activity, int theme) {
            super(activity, theme);
            AP = new AlertParams();
            AP.mContext = activity;
            AP.mTheme = theme;
        }


        @Override
        public Builder setPositiveButton(int textId, final DialogInterface.OnClickListener listener) {
            AP.mPositiveButtonText = AP.mContext.getText(textId);
            AP.mPositiveButtonListener = listener;
            return this;
        }

        @Override
        public Builder setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            AP.mPositiveButtonText = text;
            AP.mPositiveButtonListener = listener;
            return this;
        }

        @Override
        public Builder setNegativeButton(int textId, final DialogInterface.OnClickListener listener) {
            AP.mNegativeButtonText = AP.mContext.getText(textId);
            AP.mNegativeButtonListener = listener;
            return this;
        }

        @Override
        public Builder setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            AP.mNegativeButtonText = text;
            AP.mNegativeButtonListener = listener;
            return this;
        }

        @Override
        public Builder setNeutralButton(int textId, final DialogInterface.OnClickListener listener) {
            AP.mNeutralButtonText = AP.mContext.getText(textId);
            AP.mNeutralButtonListener = listener;
            return this;
        }

        @Override
        public Builder setNeutralButton(CharSequence text, final DialogInterface.OnClickListener listener) {
            AP.mNeutralButtonText = text;
            AP.mNeutralButtonListener = listener;
            return this;
        }

        @Override
        public Builder setView(View view) {
            AP.mView = view;
            AP.mViewLayoutParams = null;
            return this;
        }

        public Builder setView(View view, LinearLayout.LayoutParams layoutParams) {
            AP.mView = view;
            AP.mViewLayoutParams = layoutParams;
            return this;
        }

        @Override
        public Builder setCancelable(boolean cancelable) {
            AP.mCancelable = cancelable;
            return (Builder) super.setCancelable(cancelable);
        }

        @Override
        public Builder setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
            AP.mOnCancelListener = onCancelListener;
            return (Builder) super.setOnCancelListener(onCancelListener);
        }

        @Override
        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            AP.mOnKeyListener = onKeyListener;
            return (Builder) super.setOnKeyListener(onKeyListener);
        }

        @Override
        public AlertDialog create() {
            final AlertDialog dialog = new AlertDialog(AP.mContext, AP, AP.mTheme);
            try {
                Field PField = android.app.AlertDialog.Builder.class.getDeclaredField("P");
                PField.setAccessible(true);
                Object PObj = PField.get(this);
                Class clazz = Class.forName("com.android.internal.app.AlertController");
                Method method = PObj.getClass().getMethod("apply", clazz);
                method.setAccessible(true);
                Field mAlertField = android.app.AlertDialog.class.getDeclaredField("mAlert");
                mAlertField.setAccessible(true);
                Object alertControllerObj = mAlertField.get(dialog);
                method.invoke(PObj, alertControllerObj);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            dialog.setCancelable(AP.mCancelable);
            if (AP.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(AP.mOnCancelListener);
            if (AP.mOnKeyListener != null) {
                dialog.setOnKeyListener(AP.mOnKeyListener);
            }
            return dialog;
        }


        @Override
        public Builder setTitle(int titleId) {
            return (Builder) super.setTitle(titleId);
        }

        public Builder setTag(Object tag) {
            AP.mTag = tag;
            return this;
        }

        @Override
        public Builder setTitle(CharSequence title) {
            return (Builder) super.setTitle(title);
        }

        @Override
        public Builder setCustomTitle(View customTitleView) {
            return (Builder) super.setCustomTitle(customTitleView);
        }

        @Override
        public Builder setMessage(int messageId) {
            AP.mMessage = AP.mContext.getText(messageId);
            return this;
        }

        public Builder setNeedKeyboard(boolean needKeyboard) {
            AP.mNeedKeyboard = needKeyboard;
            return this;
        }

        @Override
        public Builder setMessage(CharSequence message) {
            AP.mMessage = message;
            return this;
        }

        @Override
        public Builder setIcon(int iconId) {
            return (Builder) super.setIcon(iconId);
        }

        @Override
        public Builder setIcon(Drawable icon) {
            return (Builder) super.setIcon(icon);
        }

        @Override
        public Builder setIconAttribute(int attrId) {
            return (Builder) super.setIconAttribute(attrId);
        }

        @Override
        public Builder setItems(int itemsId, DialogInterface.OnClickListener listener) {
            return (Builder) super.setItems(itemsId, listener);
        }

        @Override
        public Builder setItems(CharSequence[] items, DialogInterface.OnClickListener listener) {
            return (Builder) super.setItems(items, listener);
        }

        @Override
        public Builder setAdapter(ListAdapter adapter, DialogInterface.OnClickListener listener) {
            return (Builder) super.setAdapter(adapter, listener);
        }

        @Override
        public Builder setCursor(Cursor cursor, DialogInterface.OnClickListener listener, String labelColumn) {
            return (Builder) super.setCursor(cursor, listener, labelColumn);
        }

        @Override
        public Builder setMultiChoiceItems(int itemsId, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            return (Builder) super.setMultiChoiceItems(itemsId, checkedItems, listener);
        }

        @Override
        public Builder setMultiChoiceItems(CharSequence[] items, boolean[] checkedItems, DialogInterface.OnMultiChoiceClickListener listener) {
            return (Builder) super.setMultiChoiceItems(items, checkedItems, listener);
        }

        @Override
        public Builder setMultiChoiceItems(Cursor cursor, String isCheckedColumn, String labelColumn, DialogInterface.OnMultiChoiceClickListener listener) {
            return (Builder) super.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
        }

        @Override
        public Builder setSingleChoiceItems(int itemsId, int checkedItem, DialogInterface.OnClickListener listener) {
            return (Builder) super.setSingleChoiceItems(itemsId, checkedItem, listener);
        }

        @Override
        public Builder setSingleChoiceItems(Cursor cursor, int checkedItem, String labelColumn, DialogInterface.OnClickListener listener) {
            return (Builder) super.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
        }

        @Override
        public Builder setSingleChoiceItems(CharSequence[] items, int checkedItem, DialogInterface.OnClickListener listener) {
            return (Builder) super.setSingleChoiceItems(items, checkedItem, listener);
        }

        @Override
        public Builder setSingleChoiceItems(ListAdapter adapter, int checkedItem, DialogInterface.OnClickListener listener) {
            return (Builder) super.setSingleChoiceItems(adapter, checkedItem, listener);
        }

        @Override
        public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
            return (Builder) super.setOnItemSelectedListener(listener);
        }

        @Override
        public Builder setInverseBackgroundForced(boolean useInverseBackground) {
            return (Builder) super.setInverseBackgroundForced(useInverseBackground);
        }

        public Builder setButtonTextSize(float buttonTextSize) {
            AP.mButtonTextSize = buttonTextSize;
            return this;
        }

        @Override
        public AlertDialog show() {
            try {
                return (AlertDialog) super.show();
            } catch (Exception ignored) {
                ignored.printStackTrace();
                return create();
            }
        }

    }

    private static class Holder {
        public Button button1;
        public ViewGroup button1container;
        public View button1sp;
        public Button button2;
        public ViewGroup button2container;
        public View button2sp;
        public Button button3;
        public ViewGroup button3container;
        public View button_sp_h;
        public LinearLayout view_c;
    }

}
