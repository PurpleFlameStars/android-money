package com.dzfd.gids.baselibs.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zhangdecheng on 2016/11/14.
 */
public class EncryptUtils {
    /**
     * 原文：1234567890 加密后：DAPuqXUOOYyueVvpS+NSfA==
     */
    private static final String TAG = "EncryptUtils";

    private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
    public static final String KEY = "zshTtp^1";

    private static final String UTF8_CHARSET_NAME = "UTF-8";

    // 不能修改
    private static final String DES = "DES";

    // 不能修改
    private static final String CBC_MODEL = "DES/CBC/PKCS5Padding";

    public static byte[] decodeBase64String(String base64Str) {
        byte[] buf = null;
        try {
            buf = base64Str.getBytes(UTF8_CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(TAG, "getBytes Error in decodeBase64String, base64Str: " + base64Str);
        }
        if (buf == null) {
            return new byte[]{};
        } else {
            return Base64.decode(buf, Base64.NO_WRAP);
        }
    }

    /**
     * DES加密
     *
     * @param plain 待加密字符串
     * @param key DES密钥
     * @return 返回DES加密后byte[]
     */
    public static byte[] fromPlainToDesByteArray(String plain, String key) {
        byte desBytes[] = null;
        if (plain != null && plain.length() > 0) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes(UTF8_CHARSET_NAME));

                // 创建一个密匙工厂，然后用它把DESKeySpec转换成
                // 一个SecretKey对象
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
                SecretKey secretKey = keyFactory.generateSecret(dks);

                // using DES in CBC mode
                Cipher cipher = Cipher.getInstance(CBC_MODEL);

                // 初始化Cipher对象
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(UTF8_CHARSET_NAME));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

                // 执行加密操作
                desBytes = cipher.doFinal(plain.getBytes(UTF8_CHARSET_NAME));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return desBytes;
    }

    public static String fromDesByteArrayToString(byte[] desByteArray, String key) {
        String plain = null;
        if (desByteArray != null) {
            try {
                DESKeySpec dks = new DESKeySpec(key.getBytes(UTF8_CHARSET_NAME));

                // 创建一个密匙工厂，然后用它把DESKeySpec转换成
                // 一个SecretKey对象
                SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
                SecretKey secretKey = keyFactory.generateSecret(dks);

                // using DES in CBC mode
                Cipher cipher = Cipher.getInstance(CBC_MODEL);

                // 初始化Cipher对象
                IvParameterSpec iv = new IvParameterSpec(key.getBytes(UTF8_CHARSET_NAME));
                cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

                // 执行加密操作
                byte[] plainBytes = cipher.doFinal(desByteArray);

                plain = new String(plainBytes, UTF8_CHARSET_NAME);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return plain;
    }


    public static byte[] fromBase64StrToByteArray(String base64Str) {
        byte[] byteArray = null;
        if (base64Str != null && base64Str.length() > 0) {
            try {
                byteArray = Base64.decode(base64Str, Base64.NO_WRAP);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return byteArray;
    }

    public static byte[] encryptDES(byte[] encryptData) throws Exception {
        return encryptDES(encryptData, KEY);
    }

    public static byte[] encryptDES(byte[] encryptData, String key) throws Exception {
        byte[] ret = null;
        IvParameterSpec zeroIv;
        SecretKeySpec sks;
        Cipher cipher;
        byte[] encryptedData;
        if (encryptData != null && encryptData.length > 0) {
            byte[] byteKey = key.getBytes();
            zeroIv = new IvParameterSpec(byteKey);
            sks = new SecretKeySpec(byteKey, "DES");
            cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.ENCRYPT_MODE, sks, zeroIv);
            encryptedData = cipher.doFinal(encryptData);
            // ret = Base64.encodeToString(encryptedData, Base64.DEFAULT);
            // CipherOutputStream cos = new
            // CipherOutputStream(decfos,decipher);
            ret = encryptedData;
            // if (ret.endsWith("\n"))
            // ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    public static byte[] decryptDES(byte[] encryptData) throws Exception {
        return decryptDES(encryptData, KEY);
    }

    public static byte[] decryptDES(byte[] encryptData, String key) throws Exception {
        byte[] ret = null;
        byte[] byteMi, decryptData;
        IvParameterSpec zeroIv;
        SecretKeySpec sks;
        Cipher cipher;
        if (encryptData != null && encryptData.length > 0) {
            byte[] byteKey = key.getBytes();
            // byteMi = Base64.decode(encryptData, Base64.DEFAULT);
            byteMi = encryptData;
            zeroIv = new IvParameterSpec(byteKey);
            sks = new SecretKeySpec(byteKey, "DES");
            cipher = Cipher.getInstance(ALGORITHM_DES);
            cipher.init(Cipher.DECRYPT_MODE, sks, zeroIv);
            decryptData = cipher.doFinal(byteMi);
            // ret = new String(decryptData);
            ret = decryptData;
        }
        return ret;
    }

    public static String decryptDES(InputStream response, String key) throws Exception {
        IvParameterSpec zeroIv;
        SecretKeySpec sks;
        Cipher cipher;
        ByteArrayOutputStream result = null;
        CipherInputStream cis = null;
        try {
            result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            if (key != null) {
                byte[] byteKey = key.getBytes();
                zeroIv = new IvParameterSpec(byteKey);
                sks = new SecretKeySpec(byteKey, "DES");
                cipher = Cipher.getInstance(ALGORITHM_DES);
                cis = new CipherInputStream(response, cipher);
                cipher.init(Cipher.DECRYPT_MODE, sks, zeroIv);
                while ((length = cis.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                result.flush();
            }
        } finally {
            FileUtils.closeQuietly(result);
            FileUtils.closeQuietly(cis);
        }

        return result.toString();
    }

    private static final int BUFFER_LENGTH = 1024;

    /**
     * des 加密文件，文件的前8个字节为加密的长度，取1024个字节进行加des加密，如果文件小于1024个字节，则全部进行加密
     *
     * @param desKey
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void encryptFile(String desKey, File sourceFile, File targetFile) throws Exception {
        FileInputStream in = new FileInputStream(sourceFile);
        DataInputStream dis = new DataInputStream(in);

        FileOutputStream out = new FileOutputStream(targetFile);
        DataOutputStream dos = new DataOutputStream(out);
        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            if (sourceFile.length() < BUFFER_LENGTH) {
                int n = dis.read(buffer, 0, buffer.length);
                byte[] temp = new byte[n];
                System.arraycopy(buffer, 0, temp, 0, n);
                byte[] desData = encryptDES(temp, desKey);
                dos.writeLong(desData.length);// 8 byte
                dos.write(desData);
            } else {
                dis.read(buffer, 0, buffer.length);
                byte[] desData = encryptDES(buffer, desKey);
                dos.writeLong(desData.length);// 8 byte
                dos.write(desData);
                int n;
                while ((n = dis.read(buffer, 0, buffer.length)) != -1) {
                    dos.write(buffer, 0, n);
                }
            }
        } finally {
            closeStream(dis);
            closeStream(in);
            closeStream(dos);
            closeStream(out);
        }
    }

    public static void decryptFile(File sourceFile, OutputStream out) throws IOException {
        decryptFile(KEY, sourceFile, out);
    }

    public static void decryptFile(String desKey, File sourceFile, OutputStream out) throws IOException {
        if (sourceFile == null || !sourceFile.exists() || !sourceFile.isFile()) {
            throw new IOException();
        }

        FileInputStream in = new FileInputStream(sourceFile);
        DataInputStream dis = new DataInputStream(in);

        DataOutputStream dos = new DataOutputStream(out);

        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            long desLength = dis.readLong();
            if (sourceFile.length() < desLength + 8) {
                int n = dis.read(buffer, 0, buffer.length);
                byte[] temp = new byte[n];
                System.arraycopy(buffer, 0, temp, 0, n);
                byte[] desData = decryptDES(temp, desKey);
                dos.write(desData);
            } else {
                if (desLength > 0) { //PC老版本有bug，会出现未加密的情况
                    byte[] desBuffer = new byte[(int) desLength];
                    dis.read(desBuffer, 0, desBuffer.length);
                    byte[] desData = decryptDES(desBuffer, desKey);
                    dos.write(desData);
                }

                int n;
                while ((n = dis.read(buffer, 0, buffer.length)) != -1) {
                    dos.write(buffer, 0, n);
                }
            }
        } catch (Throwable e) {
            throw new IOException();
        } finally {
            closeStream(dis);
            closeStream(in);
        }
    }

    /**
     * des 解密文件，文件的前8个字节为加密的长度，取1024个字节进行加des加密，如果文件小于1024个字节，则全部进行加密
     *
     * @param desKey
     * @param sourceFile
     * @param targetFile
     * @throws IOException
     */
    public static void decryptFile(String desKey, File sourceFile, File targetFile) throws IOException {

        FileOutputStream out = new FileOutputStream(targetFile);

        try {
            decryptFile(desKey, sourceFile, out);
        } catch (Throwable e) {
            throw new IOException();
        } finally {
            closeStream(out);
        }
    }

    public static void decordOrEncodeFile(String desKey, File sourceFile, File targetFile, boolean decode) throws Exception {
        if (sourceFile.exists() && sourceFile.isFile()) {
            InputStream is = null;
            OutputStream os = null;
            try {
                byte[] bytesKey = desKey.getBytes();
                IvParameterSpec zeroIv = new IvParameterSpec(bytesKey);
                SecretKeySpec sks = new SecretKeySpec(bytesKey, "DES");
                Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
                if (decode) {
                    cipher.init(Cipher.DECRYPT_MODE, sks, zeroIv);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, sks, zeroIv);
                }
                if (decode) {
                    os = new CipherOutputStream(new FileOutputStream(targetFile), cipher);
                    is = new FileInputStream(sourceFile);
                } else {
                    is = new CipherInputStream(new FileInputStream(sourceFile), cipher);
                    os = new FileOutputStream(targetFile);
                }
                byte[] buffer = new byte[BUFFER_LENGTH];
                int n;
                while ((n = is.read(buffer, 0, buffer.length)) > 0) {
                    os.write(buffer, 0, n);
                }
            } finally {
                closeStream(os);
                closeStream(is);
            }
        }
    }

    /**
     * des 加密文件，文件的前8个字节为加密的长度，取1024个字节进行加des加密，如果文件小于1024个字节，则全部进行加密
     *
     * @param desKey
     * @param sourceFile
     * @throws IOException
     */
    public static byte[] encryptBytes(String desKey, byte[] sourceFile) throws Exception {
        byte[] targetFile = null;
        ByteArrayInputStream in = new ByteArrayInputStream(sourceFile);
        DataInputStream dis = new DataInputStream(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);
        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            if (sourceFile.length < BUFFER_LENGTH) {
                int n = dis.read(buffer, 0, buffer.length);
                byte[] temp = new byte[n];
                System.arraycopy(buffer, 0, temp, 0, n);
                byte[] desData = encryptDES(temp, desKey);
                dos.writeLong(desData.length);// 8 byte
                dos.write(desData);
            } else {
                dis.read(buffer, 0, buffer.length);
                byte[] desData = encryptDES(buffer, desKey);
                dos.writeLong(desData.length);// 8 byte
                dos.write(desData);
                int n;
                while ((n = dis.read(buffer, 0, buffer.length)) != -1) {
                    dos.write(buffer, 0, n);
                }
            }
            dos.flush();
            targetFile = out.toByteArray();
        } finally {
            closeStream(dis);
            closeStream(in);
            closeStream(dos);
            closeStream(out);
        }
        return targetFile;
    }

    /**
     * des 解密文件，文件的前8个字节为加密的长度，取1024个字节进行加des加密，如果文件小于1024个字节，则全部进行加密
     *
     * @param desKey
     * @param sourceFile
     * @throws IOException
     */
    public static byte[] decryptBytes(String desKey, byte[] sourceFile) throws Exception {
        byte[] targetFile = null;
        // if(targetFile == null || !targetFile.exists() ||
        // !targetFile.isFile()) {
        // throw new IOException();
        // }

        ByteArrayInputStream in = new ByteArrayInputStream(sourceFile);
        DataInputStream dis = new DataInputStream(in);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        byte[] buffer = new byte[BUFFER_LENGTH];
        try {
            long desLength = dis.readLong();
            if (sourceFile.length < desLength + 8) {
                int n = dis.read(buffer, 0, buffer.length);
                byte[] temp = new byte[n];
                System.arraycopy(buffer, 0, temp, 0, n);
                byte[] desData = decryptDES(temp, desKey);
                dos.write(desData);
            } else {
                byte[] desBuffer = new byte[(int) desLength];
                dis.read(desBuffer, 0, desBuffer.length);
                byte[] desData = decryptDES(desBuffer, desKey);
                dos.write(desData);

                int n;
                while ((n = dis.read(buffer, 0, buffer.length)) != -1) {
                    dos.write(buffer, 0, n);
                }

            }
            dos.flush();
            targetFile = out.toByteArray();
        } finally {
            closeStream(dis);
            closeStream(in);
            closeStream(dos);
            closeStream(out);
        }
        return targetFile;
    }

    public static void closeStream(Closeable io) {
        if (io == null) {
            return;
        }
        try {
            io.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String qucDesEncryptStr(String source, String key) {
        String encryptedStr = "";
        try {
            DESKeySpec dks = new DESKeySpec(key.getBytes());

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成
            // 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in CBC mode
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

            // 初始化Cipher对象
            IvParameterSpec iv = new IvParameterSpec(key.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            // 执行加密操作
            byte encryptedData[] = cipher.doFinal(source.getBytes());

            // 通过Base64将二进制数据变成文本
            encryptedStr = new String(Base64.encode(encryptedData, Base64.NO_WRAP));

        } catch (Exception e) {
        }

        return encryptedStr;
    }

    public static byte[] DES_encrypt(String plain, String key) {
        try {

            // DES算法要求有一个可信任的随机数源
            SecureRandom sr = new SecureRandom();

            // 从原始密钥数据创建DESKeySpec对象, DES 要求秘钥是 64bit的
            DESKeySpec dks = new DESKeySpec(key.getBytes());

            // 创建一个密匙工厂，然后用它把DESKeySpec转换成 一个SecretKey对象
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);

            // using DES in ECB mode
            Cipher cipher = Cipher.getInstance("DES");

            // 用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, sr);

            // 执行加密操作
            byte[] encryptedData = cipher.doFinal(plain.getBytes());

            return encryptedData;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
