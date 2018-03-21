package com.flyhand.yunpos.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.CheckedTextView;

/**
 * Created by Ryan on 14/11/6.
 */
public class FontAwesomeCheckedView extends CheckedTextView {

    public FontAwesomeCheckedView(Context context) {
        this(context, null, 0);
    }

    public FontAwesomeCheckedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FontAwesomeCheckedView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(FontAwesomeView.getTypeface(context));
    }
}