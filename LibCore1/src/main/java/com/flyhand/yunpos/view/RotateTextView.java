package com.flyhand.yunpos.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.flyhand.core.R;
import com.flyhand.core.utils.RUtils;


/**
 * Created by Ryan
 * On 2017/1/4.
 */

public class RotateTextView extends TextView {
    private int degree;
    private int transX;
    private int transY;

    public RotateTextView(Context context) {
        this(context, null);
    }

    public RotateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RotateTextView, 0, 0);
        degree = ta.getInteger(R.styleable.RotateTextView_degree, 0);
        transX = ta.getDimensionPixelSize(R.styleable.RotateTextView_transX, 0);
        transY = ta.getDimensionPixelSize(R.styleable.RotateTextView_transY, 0);
        ta.recycle();
    }

    public RotateTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.rotate(degree, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        canvas.translate(transX, transY);
        super.onDraw(canvas);
        canvas.restore();
    }
}