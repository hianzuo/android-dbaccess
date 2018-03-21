package com.hianzuo.dbaccess.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

/**
 * User: Ryan
 * Date: 11-9-25
 * Time: A.M. 12:37
 */
public class StringUtil {

    public static String to(String str, String def) {
        if (null == str) {
            return def;
        } else {
            return str.trim();
        }
    }

    public static String to(String str) {
        return to(str, "");
    }

    public static String substring(String src, String start, String end) {
        int startIndex = src.indexOf(start);
        if (startIndex != -1) {
            int startIndex1 = startIndex + start.length();
            int endIndex = src.indexOf(end, startIndex1);
            if (endIndex > startIndex) {
                return src.substring(startIndex1, endIndex);
            }
        }
        return "";
    }

    public static void save(File file, String detail) throws IOException {
        if (null == detail || null == file) {
            return;
        }
        FileOutputStream fos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            fos.write(detail.getBytes());
        } finally {
            if (null != fos) {
                fos.close();
            }
        }
    }

    public static boolean isNotEmpty(String s) {
        return null != s && s.trim().length() > 0;
    }

    public static boolean isEmpty(String s) {
        return !isNotEmpty(s);
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 判断字符串是否为空或者null
     *
     * @param str
     */
    public static boolean isEmpty(String str, boolean trim) {
        if (!isNull(str)) {
            if (trim) {
                str = str.trim();
            }
            return str.length() == 0;
        } else {
            return true;
        }
    }

    public static boolean isNotEmail(String email) {
        return !isEmail(email);
    }

    public static boolean isEmail(String email) {
        return null != email && email.trim().matches("(?i)^[a-z0-9_\\-\\$]+@[a-z0-9_\\-\\$]+\\.[a-z]{2,5}$");
    }

    public static boolean isNotMobile(String mobile) {
        return !isMobile(mobile);
    }

    public static boolean isMobile(String mobile) {
        return null != mobile && mobile.trim().matches("^[1][0-9]{10}$");
    }

    public static String substringAfter(String str, String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return str;
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(pos + separator.length());
    }

    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }


    public static String join(Collection<String> strings, String token) {
        return join(strings.toArray(new String[strings.size()]), token);
    }

    public static String join(Object[] strings, String token) {
        if(null == strings) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Object string : strings) {
            sb.append(string.toString()).append(token);
        }
        if (strings.length > 0) {
            int sb_len = sb.length(), token_len = token.length();
            sb.delete(sb_len - token_len, sb_len);
        }
        return sb.toString();
    }

    public static String reverse(String s) {
        int size = s.length();
        if (s.length() == 0) {
            return s;
        }
        int half = size / 2;
        char[] buf = s.toCharArray();
        for (int leftIdx = 0, rightIdx = size - 1; leftIdx < half; leftIdx++, rightIdx--) {
            char swap = buf[leftIdx];
            buf[leftIdx] = buf[rightIdx];
            buf[rightIdx] = swap;
        }
        return new String(buf);
    }


    public static int countMatches(String str, String sub) {
        if (isEmpty(str) || isEmpty(sub)) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

}
