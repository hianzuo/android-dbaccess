package com.flyhand.core;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Ryan
 * On 15/10/20.
 */
public class SimpleDateFormat extends java.text.SimpleDateFormat {
    public static final TimeZone CST = TimeZone.getTimeZone("Asia/Shanghai");
    private static final long serialVersionUID = -3893847016440888396L;

    public SimpleDateFormat() {
        super();
        setTimeZone(SimpleDateFormat.CST);
    }

    public SimpleDateFormat(String pattern) {
        super(pattern);
        setTimeZone(SimpleDateFormat.CST);
    }

    public SimpleDateFormat(String template, DateFormatSymbols value) {
        super(template, value);
        setTimeZone(SimpleDateFormat.CST);
    }

    public SimpleDateFormat(String template, Locale locale) {
        super(template, locale);
        setTimeZone(SimpleDateFormat.CST);
    }
}
