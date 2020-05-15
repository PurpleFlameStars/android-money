package com.dzfd.gids.baselibs.utils;

import android.util.Base64;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DESUtils {
    private static final String TAG = DESUtils.class.getSimpleName();

    private static final String SHA1PRNG = "SHA1PRNG";//// SHA1PRNG 强随机种子算法, 要区别4.2以上版本的调用方法

    private static byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0};

    public static String encode(String secretKey, String plainText) throws Exception {
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "DES");
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
        byte[] encryptedData = cipher.doFinal(plainText.getBytes());
        return Base64.encodeToString(encryptedData, Base64.NO_WRAP | Base64.URL_SAFE);
    }

    public static String decode(String secretKey, String encryptText) {
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "DES");
            Cipher cipher = Cipher.getInstance("DES");
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            byte[] decryptData = cipher.doFinal(Base64.decode(encryptText, Base64.NO_WRAP | Base64.URL_SAFE));
            return new String(decryptData);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 生成八位随机字符串做DES Key.
     */
    public static String genDesKey() {
        return genRandomString().substring(0, 8);
    }

    private static String genRandomString() {
        try {
            SecureRandom sr = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes = new byte[16];
            sr.nextBytes(bytes);
            return MD5.md5(bytes).toLowerCase();  // by xzh???
        } catch (Exception e) {
            // 如果当前 Android 系统不支持 SHA1PRNG 算法， 用当前时间作为随机串
            return Long.toString(System.currentTimeMillis());
        }
    }
}
