package com.flyhand.core;

import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by Ryan on 15/11/6.
 */
public class Calendar extends GregorianCalendar {
    public static int DATE = java.util.Calendar.DATE;

    public Calendar(TimeZone timezone) {
        super(timezone);
    }

    public static Calendar getInstance() {
        return new Calendar(SimpleDateFormat.CST);
    }
}
