package com.flyhand.yunpos.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ryan on 14/11/6.
 */
public class CustomFontTextView extends TextView {
    private static Typeface mCustomFont;

    public CustomFontTextView(Context context) {
        this(context, null, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFontTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(getTypeface(context));
    }

     public static Typeface getTypeface(Context context) {
        try {
            if (null == mCustomFont) {
                mCustomFont = Typeface.createFromAsset(context.getAssets(), "font_custom.ttf");
            }
            return mCustomFont;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
