package com.flyhand.yunpos.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created with IntelliJ IDEA.
 * User: Ryan
 * Date: 13-12-23
 * Time: 下午2:15
 */
public class AlwaysMarqueeTextView extends TextView {

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
