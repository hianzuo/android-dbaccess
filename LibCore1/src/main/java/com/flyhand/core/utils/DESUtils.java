package com.flyhand.core.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by IntelliJ IDEA.
 * User: Ryan
 * Date: 12-1-10
 * Time: A.M. 10:57
 */
public class DESUtils {
    private static byte[] iv = {1, 2, 3, 4, 5, 6, 7, 8};

    public static String encryptDES(String encryptString, String encryptKey) throws Exception {
        if (null == encryptString) {
            return null;
        }
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes("UTF-8"), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(encryptString.getBytes("UTF-8"));
        return Base64.encode(encryptedData);
    }

    public static String decryptDES(String decryptString, String decryptKey) throws Exception {
        if (null == decryptString) {
            return null;
        }
        byte[] byteMi = Base64.decode(decryptString);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
        byte[] decryptedData = cipher.doFinal(byteMi);
        return new String(decryptedData, "UTF-8");
    }
}
