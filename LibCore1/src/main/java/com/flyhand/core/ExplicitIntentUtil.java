package com.flyhand.core;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Ryan
 * On 2016/4/24.
 */
public class ExplicitIntentUtil {
    public static Intent get(Context context,Intent oldIntent) {
        android.content.Intent newIntent = new android.content.Intent(oldIntent);
        newIntent.setAction(oldIntent.getAction());
        String aPackage = oldIntent.getPackage();
        if (null != aPackage && aPackage.trim().length() > 0) {
            newIntent.setPackage(aPackage);
        } else {
            newIntent.setPackage(context.getPackageName());
        }
        return newIntent;
    }
}
