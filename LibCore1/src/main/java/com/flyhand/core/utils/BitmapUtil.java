package com.flyhand.core.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.widget.ImageView;

import com.flyhand.cache.IoUtils;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Ryan
 * On 2016/5/6.
 */
public class BitmapUtil {

    public static void release(Bitmap bitmap) {
        try {
            if (null != bitmap && bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception ignored) {
        }
    }

    public static void release(ImageView iv) {
        Drawable drawable = iv.getDrawable();
        iv.setImageResource(0);
        if (null != drawable && drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            BitmapUtil.release(bitmap);
        }
    }

    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            return bitmap.copy(bitmap.getConfig(), bitmap.isMutable());
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static boolean saveToFile(Drawable drawable, File target) {
        Bitmap bitmap = drawable2Bitmap(drawable);
        return saveToFile(bitmap, target);
    }

    public static boolean saveToFile(Bitmap bitmap, File target) {
        if (null == bitmap) {
            return false;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
            IoUtils.closeQuietly(fos);
        }
    }
}
