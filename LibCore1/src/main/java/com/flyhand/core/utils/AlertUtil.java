package com.flyhand.core.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyhand.core.activity.ExActivity;
import com.flyhand.core.app.AbstractCoreApplication;
import com.flyhand.core.dialog.DialogUtils;
import com.flyhand.core.widget.ToastFlyhand;
import com.flyhand.yunpos.dialog.AlertDialog;
import com.flyhand.yunpos.view.CustomFontTextView;
import com.flyhand.yunpos.view.FontAwesomeView;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 13-9-23
 * Time: 上午10:24
 */
public class AlertUtil {
    public static void showAlertDialog(ExActivity activity, String title, String content, boolean cancelable, boolean showCancelBtn,
                                       DialogInterface.OnClickListener sureListener, DialogInterface.OnClickListener cancelListener) {
        showAlertDialog(activity, title, content, cancelable, showCancelBtn, sureListener, "确定", cancelListener);
    }

    public static AlertDialog confirm(ExActivity activity, String content, DialogInterface.OnClickListener onOkListener) {
        return showAlertDialog(activity, getAlertWarnTitle(activity), content, true, true, onOkListener, "确定", null);
    }

    public static AlertDialog confirm(ExActivity activity, String title, String content, DialogInterface.OnClickListener onOkListener) {
        return showAlertDialog(activity, title, content, true, true, onOkListener, "确定", null);
    }

    public static AlertDialog alert(ExActivity activity, String content) {
        return alert(activity, getAlertWarnTitle(activity), content);
    }

    public static AlertDialog alertError(ExActivity activity, String content) {
        if (null == activity || activity.isFinishing()) {
            return null;
        }
        content = null == content ? "" : content;
        content = content.replace("<br/>\n", "\n")
                .replace("<BR/>\n", "\n")
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replace("<br/>", "\n")
                .replace("<BR/>", "\n");
        String[] lines = content.split("\n");
        String packageName = activity.getPackageName();
        String prefix = packageName.substring(packageName.lastIndexOf("."));
        if (!prefix.contains(".")) {
            prefix = packageName;
        }
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (line.contains(prefix)) {
                sb.append("<font color=blue>").append(line).append("</font><br/>\n");
            } else {
                sb.append(line).append("<br/>\n");
            }
        }
        return alert(activity, getAlertWarnTitle(activity), Html.fromHtml(sb.toString()));
    }

    public static String getAppName(Context context) {
        if (null == context) {
            return "系统";
        }
        if ("com.flyhand.yunpos".equals(context.getPackageName())) {
            return RUtils.getRString("yunpos_app_name");
        } else if ("com.flyhand.cpff".equals(context.getPackageName())) {
            return RUtils.getRString("cpff_app_name");
        } else if ("com.flyhand.queue".equals(context.getPackageName())) {
            return RUtils.getRString("queue_app_name");
        } else if ("com.flyhand.queuetv".equals(context.getPackageName())) {
            return RUtils.getRString("queue_app_name_tv");
        } else {
            return "系统";
        }
    }

    public static String getAlertWarnTitle(Context context) {
        return getAppName(context) + " 提示";
    }

    public static AlertDialog alert(ExActivity activity, String title, CharSequence content) {
        return showAlertDialog(activity, title, content,
                true, false, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }, activity.getString(android.R.string.ok), null);
    }

    public static void alert(ExActivity activity, String title,
                             String content, String sureText,
                             DialogInterface.OnClickListener onSureClickListener) {
        showAlertDialog(activity, title, content,
                true, true, onSureClickListener, sureText, null);
    }

    public static void alert(ExActivity activity, String title, String content, DialogInterface.OnClickListener onClickListener) {
        showAlertDialog(activity, title, content, false, false, onClickListener, "确定", null);
    }

    public static void alert(ExActivity activity, String content, DialogInterface.OnClickListener onClickListener) {
        showAlertDialog(activity, getAlertWarnTitle(activity), content, false, false, onClickListener, "确定", null);
    }

    public static void alert(ExActivity activity, CharSequence content, DialogInterface.OnClickListener onClickListener, String sureBtnText, boolean showCancelBtn) {
        showAlertDialog(activity, getAlertWarnTitle(activity), content, true, showCancelBtn, onClickListener, sureBtnText, null);
    }

    public static void alert(ExActivity activity, CharSequence content, DialogInterface.OnClickListener onClickListener, boolean showCancelBtn) {
        showAlertDialog(activity, getAlertWarnTitle(activity), content, true, showCancelBtn, onClickListener, "确定", null);
    }

    public static void alert(ExActivity activity, CharSequence content, DialogInterface.OnClickListener onClickListener, boolean showCancelBtn, boolean cancelable) {
        showAlertDialog(activity, getAlertWarnTitle(activity), content, cancelable, showCancelBtn, onClickListener, "确定", null);
    }

    public static void alert(ExActivity activity, CharSequence content, DialogInterface.OnClickListener onClickListener, DialogInterface.OnClickListener onCancelListener) {
        showAlertDialog(activity, getAlertWarnTitle(activity), content, true, true, onClickListener, "确定", onCancelListener);
    }

    public static AlertDialog showAlertDialog(ExActivity activity, String title, CharSequence content, boolean cancelable, boolean showCancelBtn,
                                              DialogInterface.OnClickListener sureListener, String sureBtnText,
                                              DialogInterface.OnClickListener cancelListener, String cancelBtnText) {
        AlertDialog.Builder builder = AlertDialog.createBuilder(activity);
        if (StringUtil.isNotEmpty(title)) {
            builder.setTitle(title);
        }
        if (null == content) {
            content = "";
        }
        CharSequence showMessage;
        if (content instanceof String) {
            String _content = (String) content;
            _content = _content.replace("\r\n", "<br/>");
            _content = _content.replace("\n", "<br/>");
            _content = _content.replace("\r", "<br/>");
            showMessage = Html.fromHtml(_content);
        } else {
            showMessage = content;
        }
        builder.setMessage(showMessage);
        if (null == sureListener) {
            sureListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }
        if (null == cancelListener) {
            cancelListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            };
        }
        builder.setPositiveButton(sureBtnText, sureListener);
        if (showCancelBtn) {
            builder.setNeutralButton(cancelBtnText, cancelListener);
        }

        builder.setCancelable(cancelable);
        try {
            return builder.show();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static AlertDialog showAlertDialog(ExActivity activity, String title, CharSequence content, boolean cancelable, boolean showCancelBtn,
                                              DialogInterface.OnClickListener sureListener, String sureBtnText,
                                              DialogInterface.OnClickListener cancelListener) {
        return showAlertDialog(activity, title, content, cancelable, showCancelBtn,
                sureListener, sureBtnText,
                cancelListener, RUtils.getRString("mjkf_cancel"));
    }

    public static AlertDialog alertProgress(ExActivity activity, String msg) {
        AlertDialog dialog = AlertDialog.createBuilder(activity)
                .setMessage(msg).create();
        dialog.show();
        return dialog;
    }

    public static void toastLong(Activity mActivity, String msg) {
        ToastFlyhand.makeText(mActivity.getApplicationContext(), msg, ToastFlyhand.LENGTH_LONG).show();
    }

    public static void toastLongGreen(Activity mActivity, String msg) {
        ToastFlyhand.makeTextGreen(mActivity.getApplicationContext(), msg, ToastFlyhand.LENGTH_LONG).show();
    }

    public static void toast(Activity mActivity, String msg) {
        ToastFlyhand.makeText(mActivity.getApplicationContext(), msg, ToastFlyhand.LENGTH_SHORT).show();
    }

    public static void toastGreen(Activity mActivity, String msg) {
        ToastFlyhand.makeTextGreen(mActivity.getApplicationContext(), msg, ToastFlyhand.LENGTH_SHORT).show();
    }

    public static void toast(final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            ToastFlyhand.makeText(AbstractCoreApplication.get(), msg, ToastFlyhand.LENGTH_SHORT).show();
        } else {
            AbstractCoreApplication.get().getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    ToastFlyhand.makeText(AbstractCoreApplication.get(), msg, ToastFlyhand.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void toastGreen(final String msg) {
        if ("main".equals(Thread.currentThread().getName())) {
            ToastFlyhand.makeTextGreen(AbstractCoreApplication.get(), msg, ToastFlyhand.LENGTH_SHORT).show();
        } else {
            AbstractCoreApplication.get().getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    ToastFlyhand.makeTextGreen(AbstractCoreApplication.get(), msg, ToastFlyhand.LENGTH_SHORT).show();
                }
            });
        }
    }


    public static void toastLong(String msg) {
        ToastFlyhand.makeText(AbstractCoreApplication.get(), msg, ToastFlyhand.LENGTH_LONG).show();
    }


    public static void selectItem(ExActivity activity,
                                  String title,
                                  CharSequence[] items,
                                  DialogInterface.OnClickListener onClickListener
    ) {
        selectItem(activity, title, items, onClickListener, "取消", null);
    }

    public static void selectItem(ExActivity activity,
                                  String title,
                                  List<? extends CharSequence> items,
                                  DialogInterface.OnClickListener onClickListener
    ) {
        selectItem(activity, title, items, onClickListener, "取消", null);
    }

    public static void selectItem(ExActivity activity,
                                  String title,
                                  List<? extends CharSequence> items,
                                  DialogInterface.OnClickListener onClickListener,
                                  String btnText,
                                  DialogInterface.OnClickListener onBtnClickListener
    ) {
        CharSequence[] array = items.toArray(new CharSequence[items.size()]);
        selectItem(activity, title, array, onClickListener, btnText, onBtnClickListener);
    }

    public static void selectItem(ExActivity activity,
                                  String title,
                                  CharSequence[] items,
                                  DialogInterface.OnClickListener onClickListener,
                                  String btnText,
                                  DialogInterface.OnClickListener onBtnClickListener
    ) {
        AlertDialog.createBuilder(activity)
                .setTitle(title)
                .setItems(items, onClickListener)
                .setPositiveButton(btnText, onBtnClickListener)
                .show();
    }

    public static void input(ExActivity activity, String title, String hint, int inputType,
                             final OnInputListener onInputListener, boolean showCancelBtn) {
        input(activity, title, "", hint, 1, inputType, onInputListener, showCancelBtn);
    }

    public static void input(ExActivity activity, String title, String content, String hint, int lineCount, int inputType,
                             final OnInputListener onInputListener, boolean showCancelBtn) {
        final EditText et = new EditText(activity);
        if (null != content) {
            et.setText(content);
        }
        if (null != hint) {
            et.setHint(hint);
        }
        et.setInputType(inputType);
        if (lineCount <= 1) {
            et.setSingleLine(true);
        } else {
            et.setGravity(Gravity.TOP);
            et.setSingleLine(false);
            et.setLines(lineCount);
//            float _5dp = ViewUtils.getDipPx(activity.getResources(),5);
//            et.setLineSpacing(_5dp,1f);
        }
        int paddingTopBottom;
        try {
            paddingTopBottom = et.getTotalPaddingTop() + et.getTotalPaddingBottom();
        } catch (Exception e) {
            paddingTopBottom = ViewUtilsBase.getDipPx(activity.getResources(), 15);
        }
        int padding = paddingTopBottom + ViewUtilsBase.getDipPx(activity.getResources(), 5);
        AlertDialog.Builder builder = AlertDialog.createBuilder(activity);
        builder.setNeedKeyboard(true);
        builder.setTitle(title)
                .setView(et, new LinearLayout.LayoutParams(-1, (et.getLineHeight() * lineCount) + padding))
                .setPositiveButton(RUtils.getRString("confirm"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = et.getText().toString().replace(" ", "");
                        if (null != onInputListener) {
                            if (onInputListener.onInput(dialog, str)) {
                                dialog.cancel();
                            }
                        } else {
                            dialog.cancel();
                        }
                    }
                })
                .create();
        if (showCancelBtn) {
            builder.setNeutralButton(RUtils.getRString("cancel"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (null != dialog) {
                        try {
                            dialog.cancel();
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
            builder.setCancelable(false);
        } else {
            builder.setCancelable(true);
        }
        builder.show();
    }

    public static void choiceItems(ExActivity activity,
                                   String title,
                                   String[] items,
                                   final boolean[] checked,
                                   final OnChoiceItemListener listener) {
        AlertDialog.createBuilder(activity)
                .setTitle(title)
                .setMultiChoiceItems(items, checked, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        checked[which] = isChecked;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onInput(dialog, checked);
                    }
                })
                .setNeutralButton("取消", null)
                .show();

    }

    public static GirdItem fcGirdItem(String key, String name, String icon) {
        return new GirdItem(key, name, icon).fc();
    }

    public static GirdItem faGirdItem(String key, String name, String icon) {
        return new GirdItem(key, name, icon);
    }

    public interface OnInputListener {
        boolean onInput(DialogInterface dialog, String text);
    }

    public interface OnChoiceItemListener {
        boolean onInput(DialogInterface dialog, boolean[] checked);
    }

    public static void selectItemGird(ExActivity activity,
                                      String title,
                                      List<GirdItem> items,
                                      int numColumns,
                                      boolean cancelBtn,
                                      DialogInterface.OnClickListener onClickListener
    ) {
        GirdItem[] itemArrays = new GirdItem[items.size()];
        for (int i = 0; i < itemArrays.length; i++) {
            itemArrays[i] = items.get(i);
        }
        selectItemGird(activity, title, itemArrays, numColumns, cancelBtn, onClickListener);
    }

    public static void selectItemGird(final ExActivity activity,
                                      String title,
                                      final GirdItem[] items,
                                      int numColumns,
                                      boolean cancelBtn,
                                      final DialogInterface.OnClickListener onClickListener
    ) {
        final AlertDialog dialog = AlertDialog.createBuilder(activity).create();
        View core_dialog_gird_select = View.inflate(activity, RUtils.getRLayoutID("core_dialog_gird_select"), null);
        GridView gv = (GridView) core_dialog_gird_select.findViewById(RUtils.getRID("grid_view"));
        gv.setNumColumns(numColumns);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (null != onClickListener) {
                    onClickListener.onClick(dialog, i);
                }
            }
        });
        gv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return items.length;
            }

            @Override
            public GirdItem getItem(int i) {
                return items[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (null == view) {
                    view = View.inflate(activity, RUtils.getRLayoutID("core_dialog_gird_select_item"), null);
                }
                TextView fav_icon = (TextView) view.findViewById(RUtils.getRID("fav_icon"));
                TextView tv_name = (TextView) view.findViewById(RUtils.getRID("tv_name"));
                GirdItem item = getItem(i);
                if (item.hasIcon()) {
                    fav_icon.setVisibility(View.VISIBLE);
                    fav_icon.setTypeface(item.getTypeface());
                    fav_icon.setText(item.getIcon());
                    fav_icon.setTextColor(item.getIconColor());
                } else {
                    fav_icon.setVisibility(View.GONE);
                }
                if (item.hasText()) {
                    tv_name.setVisibility(View.VISIBLE);
                    tv_name.setText(item.getText());
                } else {
                    tv_name.setVisibility(View.GONE);
                }
                return view;
            }
        });
        dialog.setTitle(title);
        dialog.setView(core_dialog_gird_select);
        if (cancelBtn) {
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DialogUtils.CancelDialog(dialog);
                }
            });
            dialog.setCancelable(false);
        }
        dialog.show();
    }

    public static class GirdItem implements CharSequence {
        private String key;
        private String text;
        private String icon;
        private int iconColor = Color.parseColor("#505050");
        private Typeface typeface;

        public GirdItem(String key, String text, String icon) {
            this.key = key;
            this.text = text;
            this.icon = icon;
            this.typeface = FontAwesomeView.getTypeface(AbstractCoreApplication.get());
        }

        public String getKey() {
            return key;
        }

        public String getText() {
            return text;
        }

        public String getIcon() {
            return icon;
        }

        public int getIconColor() {
            return iconColor;
        }

        @Override
        public int length() {
            return text.length();
        }

        @Override
        public char charAt(int i) {
            return text.charAt(i);
        }

        public Typeface getTypeface() {
            return typeface;
        }

        @Override
        public CharSequence subSequence(int i, int i1) {
            return text.subSequence(i, i1);
        }

        public GirdItem fc() {
            this.typeface = CustomFontTextView.getTypeface(AbstractCoreApplication.get());
            return this;
        }

        public GirdItem iconColor(int color) {
            this.iconColor = color;
            return this;
        }

        public boolean hasIcon() {
            return StringUtil.isNotEmpty(icon);
        }

        public boolean hasText() {
            return StringUtil.isNotEmpty(text);
        }
    }
}
