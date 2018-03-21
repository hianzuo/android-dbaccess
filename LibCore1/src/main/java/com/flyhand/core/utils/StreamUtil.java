package com.flyhand.core.utils;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 11-11-14
 * Time: Afternoon 4:22
 */
public class StreamUtil {
    public static void CloseInputStream(InputStream is) {
        if (null != is) {
            try {
                is.close();
            } catch (IOException e) {
                //
            }
        }
    }

    public static void CloseOutputStream(OutputStream os) {
        if (null != os) {
            try {
                os.close();
            } catch (IOException e) {
                //
            }
        }
    }

    public static void FlushOutputStream(FileOutputStream fos) {
        if (null != fos) {
            try {
                fos.flush();
            } catch (IOException e) {
                //
            }
        }
    }

    public static String readString(InputStream is, String charset) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is, charset));
            String data = "";
            String readLine;
            while ((readLine = in.readLine()) != null) {
                data = data + readLine;
            }
            in.close();
            is.close();
            return data;
        } catch (IOException e) {
            return null;
        }
    }
}
