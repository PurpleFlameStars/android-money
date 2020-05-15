package com.dzfd.gids.baselibs.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class StringUtils {
    public static String sanitizeMimeType(String mimeType) {
        try {
            mimeType = mimeType.trim().toLowerCase(Locale.ENGLISH);

            final int semicolonIndex = mimeType.indexOf(';');
            if (semicolonIndex != -1) {
                mimeType = mimeType.substring(0, semicolonIndex);
            }
            return mimeType;
        } catch (NullPointerException npe) {
            return null;
        }
    }


    public static boolean isUrlEncodedByUtf8(String s) {
        if (TextUtils.isEmpty(s)) {
            return false;
        } else {
            String tmp = null;
            try {
                tmp = URLDecoder.decode(s, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return false;
            }
            return !tmp.equals(s);
        }
    }

    public static String checkNullPointer(String val) {
        if (val == null) {
            return "";
        } else {
            return val;
        }
    }

    public static boolean IsEmpty(String val) {
        if (val == null) {
            return true;
        } else {
            if (val.length() == 0)
                return true;
        }
        return false;
    }

    public static String filterSpace(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        String content;
        content = s.replaceAll("\r", "");
        content = content.replaceAll("\t", "");
        content = content.replaceAll("\b", "");
        content = content.replaceAll("\n", "");
        content.replaceAll(" ", "");
        return content;
    }


    public static int getIntegerValue(String vercodeString, int defaultValue) {
        int result = defaultValue;
        try {
            result = Integer.parseInt(vercodeString);
        } catch (NumberFormatException e) {
        }
        return result;
    }


    public static byte[] readInputStream(InputStream inputStream) throws Exception {
        LogUtils.safeCheck(inputStream != null);
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream outSteam = null;
        byte[] buffer = new byte[1024];
        try {
            int len;
            outSteam = new ByteArrayOutputStream();
            while ((len = inputStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            inputStream.close();
            return outSteam.toByteArray();
        } catch (OutOfMemoryError e) {
            // 提示系统，进行内存回收
            System.gc();
            e.printStackTrace();
        } finally {
            if (outSteam != null) {
                outSteam.close();
            }
            inputStream.close();
        }
        return null;
    }

    public static String[] split(String value, char seperator) {
        if (value.length() == 0) {
            return new String[0];
        }

        value += seperator;

        List<String> ret = new ArrayList<String>();

        StringBuilder temp = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '\\') {
                // we may get the escape character here
                if (i + 1 < value.length()) {
                    i++;
                }
            }

            if (value.charAt(i) == seperator) {
                ret.add(Uri.decode(temp.toString()));
                temp.delete(0, temp.length());
            } else {
                temp.append(value.charAt(i));
            }
        }

        return ret.toArray(new String[ret.size()]);
    }


    public static String getUtf8String(String val) {
        if (val == null) {
            return null;
        }
        try {
            byte[] data = val.getBytes("UTF-8");
            /*if(data.length > 100){
                data = ZipUtils.gZipCompress(data);
			}*/
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            return null;
        }
    }

    public static boolean startWithIgnoreCase(String val, String prefix) {
        if (!TextUtils.isEmpty(val) && !TextUtils.isEmpty(prefix)) {
            return val.toLowerCase().startsWith(prefix.toLowerCase());
        }
        return false;
    }

    // *._ \'\"  ssid = StringUtils.trim(ssid, "*._ \'\"");
    public static String trim(String val, String trim) {
        val = trimLeft(val, trim);
        return trimRight(val, trim);
    }

    //    val = StringUtils.trimLeft("abd","*._");
//    val = StringUtils.trimLeft("a","*._");
//    val = StringUtils.trimLeft("*._abd","*._");
//    val = StringUtils.trimLeft("*._a*._","*._");
//    val = StringUtils.trimLeft("*._","*._");
    public static String trimLeft(String val, String trim) {
        if (TextUtils.isEmpty(val) || TextUtils.isEmpty(trim)) {
            LogUtils.safeCheck(false);
            return val;
        }

        for (int nPos = 0; nPos < val.length(); ++nPos) {
            if (trim.indexOf(val.charAt(nPos)) == -1) {
                return val.substring(nPos, val.length());
            }
        }

        return "";
    }

    //    val = StringUtils.trimRight("abd ", "*._ ");
//    val = StringUtils.trimRight("a*._ ", "*._");
//    val = StringUtils.trimRight("*. _","*._");
//    val = StringUtils.trimRight("*_ abd *._","*._ ");
    public static String trimRight(String val, String trim) {
        if (TextUtils.isEmpty(val) || TextUtils.isEmpty(trim)) {
            LogUtils.safeCheck(false);
            return val;
        }

        for (int nPos = val.length() - 1; nPos >= 0; --nPos) {
            if (trim.indexOf(val.charAt(nPos)) == -1) {
                return val.substring(0, nPos + 1);
            }
        }

        return "";
    }

    public static boolean endWithIgnoreCase(String val, String prefix) {
        if (!TextUtils.isEmpty(val) && !TextUtils.isEmpty(prefix)) {
            return val.toLowerCase().endsWith(prefix.toLowerCase());
        }
        return false;
    }


    // GENERAL_PUNCTUATION 判断中文的“号
    // CJK_SYMBOLS_AND_PUNCTUATION 判断中文的。号
    // HALFWIDTH_AND_FULLWIDTH_FORMS 判断中文的，号
    public static final boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    public static final boolean hasChineseCharacter(String val) {// 该代码可能耗时，不要在release版本调用。
        if (!TextUtils.isEmpty(val)) {
            char[] ch = val.toCharArray();
            for (int i = 0; i < ch.length; i++) {
                char c = ch[i];
                if (isChinese(c)) {
                    return true;
                }
            }
        }
        return false;
    }


    public static boolean isUrlValid(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.indexOf(" ") == -1) {
                if (!StringUtils.hasChineseCharacter(url)) {
                    return true;
                }
            }
        }

        if (LogUtils.isDebug()) {
            throw new Error("UrlParamCheckInterceptor.jva url not valid " + url);
        }

        return false;
    }


    //整理小米或者魅族的rom版本号为数字串
    public static String trimRomVersionName(String rom) {
        try {
            if (TextUtils.isEmpty(rom))
                return null;
            //1.去掉所有字母
            String result = rom.replaceAll("[a-zA-Z]", "");
            //2.去掉所有空格
            result = result.replaceAll("\\s*", "");
            //3.去掉首字母为.的情况
            if (result.charAt(0) == '.') {
                result = result.substring(1);
            }
            //4.去掉末字母为.的情况
            if (result.charAt(result.length() - 1) == '.') {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取指定长度的字节数组，超出指定大小用指定省略字符串代替；
     * 如：getSubByteArrayEnds(0123456789, 6, "...") -> 012...789
     *
     * @param bytes
     * @param maxSize
     * @param ellipsis
     * @return
     */
    public static String getSubByteArrayEnds(byte[] bytes, int maxSize, String ellipsis) {
        int subSize = maxSize / 2;
        StringBuilder sb = new StringBuilder();
        if (bytes != null) {
            if (bytes.length <= maxSize || maxSize <= 0 || maxSize >= bytes.length) {
                sb.append(new String(bytes, 0, bytes.length));
            } else {
                sb.append(new String(bytes, 0, subSize));
                if (subSize > 0 && bytes.length > maxSize) {
                    sb.append(ellipsis);
                }
                sb.append(new String(bytes, bytes.length - subSize, subSize));
            }
        }
        return sb.toString();
    }

    //无效的url 包括空或者以file开头的url
    public static boolean isFileOrInvalidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return true;
        }
        url = trimLeft(url, " ");
        if (TextUtils.isEmpty(url) || url.toLowerCase().startsWith("file")) {
            return true;
        }
        return false;
    }

    /**
     * 获取一定位数的随机字符串
     * @param length 位数
     * @return 随机字符串
     */
    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String getNotNullString(String str) {
        return str == null ? "" : str;
    }
}
