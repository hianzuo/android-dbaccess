package com.flyhand.core.utils;

import com.hianzuo.logger.Log;
import com.hianzuo.logger.LogServiceHelper;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by Ryan
 * On 15/1/14.
 */
public class ExceptionUtils {
    public static String print(Throwable ex) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        printWriter.flush();
        String errMsg = writer.toString();
        printWriter.close();
        return errMsg;
    }

    public static String getMessage(Throwable throwable) {
        if (null == throwable) {
            return "null";
        } else {
            String message = throwable.getMessage();
            if (StringUtil.isEmpty(message)) {
                return throwable.getClass().getSimpleName();
            } else {
                return message;
            }
        }
    }

    public static void logException(String tag, Throwable ex) {
        String str = ExceptionUtils.print(ex);
        logException(tag, str);
    }

    public static void logException(String tag, String str) {
        if (StringUtil.isNotEmpty(str)) {
            String[] ss = str.split("\n");
            for (String s : ss) {
                Log.e(tag, s.replace("\t", "        "));
            }
            LogServiceHelper.flush();
        }
    }

    public static String getCauseMessage(Throwable throwable) {
        Throwable cause = throwable.getCause();
        if (null != cause) {
            return getCauseMessage(cause);
        }
        return getMessage(throwable);
    }
}
