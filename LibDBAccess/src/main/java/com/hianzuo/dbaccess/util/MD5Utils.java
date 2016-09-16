package com.hianzuo.dbaccess.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: Ryan
 * Date: 11-10-6
 * Time: A.M. 11:31
 */
public class MD5Utils {
    public static String MD5(String src) {
        try {
            int i;
            if (src == null) return null;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            byte b[] = md.digest();
            StringBuffer buf = new StringBuffer();
            for (byte aB : b) {
                i = aB;
                if (i < 0) i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static final String MD5 = "MD5";
    public static final String SHA1 = "SHA1";

    public static String md5(String value) {
        if (value == null) return "";
        return md5(value.getBytes());
    }

    public static String md5(String value, String charset) {
        if (value == null) return "";
        try {
            return md5(value.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(byte[] value) {
        return encode(MD5, value);
    }

    public static String sha1(String value) {
        if (value == null) return "";
        return sha1(value.getBytes());
    }

    public static String sha1(String value, String charset) {
        if (value == null) return "";
        try {
            return sha1(value.getBytes(charset));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sha1(byte[] value) {
        return encode(SHA1, value);
    }

    public static String encode(String algorithm, String value) {
        if (value == null) return "";
        return encode(algorithm, value.getBytes());
    }

    public static String encode(String algorithm, byte[] value) {
        if (value == null) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(value);
            byte[] bytes = digest.digest();

            int i;
            StringBuilder buf = new StringBuilder("");
            for (byte aByte : bytes) {
                i = aByte;
                if (i < 0) {
                    i += 256;
                }
                if (i < 16) {
                    buf.append("0");
                }
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("No " + algorithm + " algorithm!");
        }
    }

    public static void main(String[] args) {
        System.out.println(encode(SHA1, "111111"));
        System.out.println(encode(MD5, "111111"));
    }
}

