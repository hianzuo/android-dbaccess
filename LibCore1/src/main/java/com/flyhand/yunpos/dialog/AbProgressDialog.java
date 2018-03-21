package com.flyhand.yunpos.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.utils.BaseContextUtil;
import com.flyhand.core.utils.ScaleTextFontSizeUtil;
import com.flyhand.core.utils.StringUtil;
import com.flyhand.core.utils.TagsRunnable;

/**
 * Created by Ryan
 * On 2016/6/21.
 */
public class AbProgressDialog extends ProgressDialog {
    private Context mBaseContext;

    public AbProgressDialog(Context context) {
        super(context);
        this.mBaseContext = context;
    }

    public AbProgressDialog(Context context, int theme) {
        super(context, theme);
        this.mBaseContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogCreate(this);
            ScaleTextFontSizeUtil.set(this, ((ExActivity) context).getFontSizeScale());
        }
    }

    @Override
    public void cancel() {
        try {
            super.cancel();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void hide() {
        super.hide();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogHide(this);
        }
    }

    protected Context getBaseContext() {
        return BaseContextUtil.get(mBaseContext);
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
        super.show();
        Context context = getBaseContext();
        if (context instanceof ExActivity) {
            ((ExActivity) context).onDialogShow(this);
        }
    }


    public static AbProgressDialog show(Context context, CharSequence title,
                                        CharSequence message) {
        return show(context, title, message, false);
    }

    public static AbProgressDialog show(Context context, CharSequence title,
                                        CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static AbProgressDialog show(Context context, CharSequence title,
                                        CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static AbProgressDialog show(Context context, CharSequence title,
                                        CharSequence message, boolean indeterminate,
                                        boolean cancelable, OnCancelListener cancelListener) {
        AbProgressDialog dialog = new AbProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    public static CanShowDef show(ExActivity activity, String message, int delayed) {
        final AbProgressDialog.CanShowDef canShow = new AbProgressDialog.CanShowDef();
        show(activity, message, canShow, delayed);
        return canShow;
    }

    public static void show(ExActivity activity, String message, CanShow canShow, int delayed) {
        if (StringUtil.isEmpty(message)) {
            return;
        }
        TagsRunnable tagRunnable = new TagsRunnable() {
            @Override
            public void run() {
                ExActivity activity = getTag();
                String message = getTag1();
                CanShow canShow = getTag2();
                boolean canShowDialog = null == canShow || canShow.can();
                if (canShowDialog && null != activity && activity.isActivity()) {
                    activity.showProgressDialog(message);
                }
            }
        };
        tagRunnable.setTag(activity);
        tagRunnable.setTag1(message);
        tagRunnable.setTag2(canShow);
        AbstractCoreApplication.get().getUIHandler().postDelayed(tagRunnable, delayed);
    }

    public static void close(ExActivity activity) {
        try {
            if (null != activity) {
                activity.closeProgressDialog();
            }
        } catch (Exception ignored) {
        }
    }

    public static void cancel(Dialog dialog) {
        if (null != dialog) {
            try {
                dialog.cancel();
            } catch (Exception ignored) {
            }
        }
    }

    public interface CanShow {
        boolean can();
    }

    public static class CanShowDef implements CanShow {
        private boolean can = true;

        @Override
        public boolean can() {
            return can;
        }

        public void setCan(boolean can) {
            this.can = can;
        }
    }
}
