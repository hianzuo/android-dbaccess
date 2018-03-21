package com.flyhand.core.widget;

import android.content.Context;
import android.text.Layout;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.flyhand.core.utils.ViewUtilsBase;

/**
 * Created by Ryan
 * On 2017/5/10.
 */

public class AutoResizeTextView extends TextView {
    float mMaxTextSize = 0.0f;
    float mMinTextSize = 0.0f;


    public AutoResizeTextView(Context context) {
        this(context, null, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoResizeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAutoResize();
    }

    private void initAutoResize() {
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.END);
        mMaxTextSize = getTextSize();
        mMinTextSize = ViewUtilsBase.getSpPx(getResources(), 8);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaxTextSize);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int lineCount = getLayoutLineCount();
        if (lineCount > 0) {
            int ellipsisCount = getLayout().getEllipsisCount(lineCount - 1);
            float textSize = getTextSize();
            while (ellipsisCount > 0) {
                // textSize is already expressed in pixels
                textSize = textSize - 1;
                if (textSize < mMinTextSize) {
                    textSize = mMinTextSize;
                }
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                ellipsisCount = getLayout().getEllipsisCount(lineCount - 1);
                if (textSize <= mMinTextSize) {
                    break;
                }
            }
        }
    }

    private int getLayoutLineCount() {
        Layout layout = getLayout();
        if (null != layout) {
            return layout.getLineCount();
        }
        return 0;
    }
}