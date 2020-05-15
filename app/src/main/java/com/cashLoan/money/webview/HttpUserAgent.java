package com.cashLoan.money.webview;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.WebSettings;

import com.cashLoan.money.MainApplication;
import com.dzfd.gids.baselibs.apk.ApkUtils;


public class HttpUserAgent {
    public static final String USER_AGENT_KEY = "limonlive";
    private static String userAgent;

    public static String getUserAgent() {
        if (!TextUtils.isEmpty(userAgent)) {
            return userAgent;
        }

        userAgent = getDefaultUserAgent() + "; " + USER_AGENT_KEY + "_" + ApkUtils.getVersionName(MainApplication.getInstance());
        return userAgent;
    }

    private static String getDefaultUserAgent() {
        String userAgent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(MainApplication.getInstance());
            } catch (Throwable throwable) {
            }
        }

        if (TextUtils.isEmpty(userAgent)) {
            try {
                userAgent = System.getProperty("http.agent");
            } catch (Exception e) {
            }
        }

        return handleChineseLetter(userAgent);
    }

    // 处理中文字符
    public static String handleChineseLetter(String info) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, length = info.length(); i < length; i++) {
            char c = info.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
