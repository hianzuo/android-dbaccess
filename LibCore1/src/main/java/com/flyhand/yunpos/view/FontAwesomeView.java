package com.flyhand.yunpos.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ryan on 14/11/6.
 */
public class FontAwesomeView extends TextView {
    private static Typeface mFontAwesome;

    public FontAwesomeView(Context context) {
        this(context, null, 0);
    }

    public FontAwesomeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontAwesomeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(getTypeface(context));
    }

    public static Typeface getTypeface(Context context) {
        try {
            if (null == mFontAwesome) {
                mFontAwesome = Typeface.createFromAsset(context.getAssets(), "fontawesome.ttf");
            }
            return mFontAwesome;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
