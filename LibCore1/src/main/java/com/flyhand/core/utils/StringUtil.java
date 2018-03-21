package com.flyhand.core.utils;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * User: Ryan
 * Date: 11-9-25
 * Time: A.M. 12:37
 */
public class StringUtil {

    public static String to(String str, String def) {
        if (StringUtil.isEmpty(str)) {
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

    public static boolean isMobileSimple(String mobile) {
        return null != mobile && mobile.charAt(0) == '1' && mobile.matches("[0-9]+") && mobile.length() == 11;
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


    public static String join(Collection objs, String token) {
        return join(objs.toArray(new Object[objs.size()]), token);
    }

    public static String join(Object[] strings, String token) {
        StringBuilder sb = new StringBuilder();
        for (Object string : strings) {
            sb.append(string).append(token);
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


    private static final String EMPTY_PWD = "d41d8cd98f00b204e9800998ecf8427e";

    public static boolean isEmptyPassword(String pwd) {
        return isEmpty(pwd) || pwd.equals(EMPTY_PWD);
    }

    private static final Pattern PATTERN = Pattern.compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");

    /**
     * 判断是否是IP地址
     *
     * @param str
     * @return
     */
    public static boolean isIPAddress(String str) {
        return PATTERN.matcher(str).matches();
    }

    /**
     * 生成bit位的随机数
     *
     * @param bit 位数
     * @return 随机数
     */
    public static String random(int bit) {
        int min = (int) (Math.pow(10, bit - 1));
        int max = (int) (Math.pow(10, bit) - 1);
        return String.valueOf(random(min, max));
    }


    /**
     * 生成min到max之间的随机数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机数
     */
    public static int random(int min, int max) {
        return (int) (Math.random() * (max - min)) + min;
    }


    public static boolean isPhoneNumber(String telephone) {
        return null != telephone && telephone.trim().matches("^(([0\\+]\\d{2,3}-?)?(0\\d{2,3})-?)?(\\d{7,8})(-(\\d{3,}))?$");
    }

    public static boolean intAnd(Integer _this, Integer value) {
        return !(_this == null || value == null) && (value & _this) == _this;
    }

    public static boolean isBlank(Object o) {
        if (null == o) {
            return true;
        }
        if (!(o instanceof String)) {
            return false;
        }
        String str = (String) o;
        int strLen;
        if ((strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
