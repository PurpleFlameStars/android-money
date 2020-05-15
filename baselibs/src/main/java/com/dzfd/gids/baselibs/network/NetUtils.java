package com.dzfd.gids.baselibs.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.dzfd.gids.baselibs.apk.ApkUtils;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.DeviceUtils;
import com.dzfd.gids.baselibs.utils.MD5;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by zheng on 2018/12/12.
 */

public class NetUtils {
    private String uid;
    private String sid;
    private String appver;
    private String ip;
    private String model;
    private String imei;
    private String mMac;
    private String mf;
    private String os = "Android";
    private String channel;
    private String density;
    private String sdkVersion;

    private NetUtils() {

    }

    private static volatile NetUtils g_insans;

    public static NetUtils getIns() {
        if (g_insans == null) {
            synchronized (NetUtils.class) {
                if (g_insans == null) {
                    g_insans = new NetUtils();
                }
            }
        }
        return g_insans;
    }

    public String getUid() {
        if (!TextUtils.isEmpty(uid)) {
            return uid;
        }
        Context cxt = ContextUtils.getAppContext();
        uid = getUid(cxt);
        return uid;
    }


    public String getAppVertion() {
        if (!TextUtils.isEmpty(this.appver)) {
            return this.appver;
        }
        Context cxt = ContextUtils.getAppContext();
        this.appver = getPackageVersionName(cxt);
        return this.appver;
    }

    public String getIp() {
        if (!TextUtils.isEmpty(this.ip)) {
            return this.ip;
        }
        this.ip = getLocalIpAddress();
        return this.ip;
    }

    public String getmMac() {
        if (!TextUtils.isEmpty(this.mMac)) {
            return this.mMac;
        }
        this.mMac = getMacAddress();
        return this.mMac;
    }

    public String getModel() {
        if (!TextUtils.isEmpty(model)) {
            return model;
        }
        model = Build.MODEL;
        model=model.replace(" ","-");
        return model;
    }

    public String getImei() {
        if (!TextUtils.isEmpty(imei)) {
            return imei;
        }
        Context cxt = ContextUtils.getAppContext();
        imei = getIMEI(cxt);
        return imei;
    }

    public String getMf() {
        if (!TextUtils.isEmpty(mf)) {
            return mf;
        }
        mf = Uri.encode(Build.MANUFACTURER);
        return mf;
    }

    public String getOs() {
        return os;
    }

    public String getChannel() {
        if (!TextUtils.isEmpty(channel)) {
            return channel;
        }
        channel = ApkUtils.getChannelName();
        return channel;
    }

    //==================================================================
    public static String getUid(Context context) {
        if (context == null) {
            return "1982736450";
        }
        String imei = getIMEI(context);
        String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String serialNo = getDeviceSerial();
        String stret = MD5.md5LowerCase("" + imei + androidId + serialNo);
        return stret;
    }

    public static String getIMEI(Context context) {
        try {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String sImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                if (!TextUtils.isEmpty(sImei)) {
                    return sImei;
                }
            }

        } catch (Exception e) {
        }
        return "bun_imei";
    }
    private static String getDeviceSerial() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception ignored) {
        }
        return serial;
    }
    private static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        if(ip == null){
                            ip="";
                        }
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
        }
        return "";
    }
    private static String getMacAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (!intf.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = intf.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "000000000000";

    }
    public static String getPackageVersionName(Context context) {
        if(context == null){
            return "0.1.100";
        }
        String packname=context.getPackageName();
        PackageInfo pi = getPackageInfo(context, packname);
        return pi == null ? null : pi.versionName;
    }
    private static PackageInfo getPackageInfo(Context context, String packname){
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(packname, 0);
        } catch (Exception e) {
        }
        return pi;
    }
    //=================================================================================
    public static String removeUrlKey(String url, String removeKey) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(removeKey)) {
            return null;
        }

        if (!url.contains(removeKey)) {
            return url;
        }

        Uri uri;
        try {
            uri = Uri.parse(url);
            Set<String> keys = uri.getQueryParameterNames();
            if (keys != null && keys.size() > 0) {
                Map<String, String> newParameter = new HashMap<>();
                for (String key : keys) {
                    if (TextUtils.isEmpty(key) || removeKey.equalsIgnoreCase(key)) {
                        continue;
                    }
                    newParameter.put(key, uri.getQueryParameter(key));
                }

                // 拼接新的query
                StringBuilder newQuery = new StringBuilder();
                for (Map.Entry<String, String> one : newParameter.entrySet()) {
                    String key = one.getKey();
                    String value = one.getValue();
                    if (!TextUtils.isEmpty(newQuery)) {
                        newQuery.append("&");
                    }
                    newQuery.append(key).append("=").append(value);
                }
                URI newUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), newQuery.toString(), uri.getFragment());
                String newUrl = newUri.toString();
                return newUrl;
            }
        } catch (Exception e) {
        }
        return url;
    }
    public static String addUrlParamter(String rawUrl, String key, String value) {
        if (rawUrl == null || key == null || value == null)
            return rawUrl;

        rawUrl = removeUrlKey(rawUrl, key);

        String key1 = String.format("?%s=", key);
        String key2 = String.format("&%s=", key);
        if (rawUrl.contains(key1) || rawUrl.contains(key2))
            return rawUrl;

        StringBuilder builder = new StringBuilder(rawUrl);
        if (!rawUrl.contains("?"))
            builder.append("?");

        String newRawUrl = builder.toString();
        if (!newRawUrl.endsWith("?") && !newRawUrl.endsWith("&"))
            builder.append("&");

        builder.append(key).append("=").append(value);
        return builder.toString();
    }

    public static NetworkInfo getCurNetInfo(boolean realTime) {
        return NetworkMonitor.getNetworkInfo(realTime);
    }

    public static NetworkInfo getCurNetInfoByType(int type) {
        return ((ConnectivityManager) ContextUtils.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(type);
    }

    public static boolean isDataNetwork() {
        NetworkInfo networkInfo = NetUtils.getCurNetInfo(false);
        boolean bRet = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected();
        if (!bRet) { // 湖南一个移动3G网络用户，他的 netType 是 50 。
            NetworkInfo mobileNetworkInfo = NetUtils.getCurNetInfoByType(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetworkInfo != null && NetUtils.getCurNetInfo(true) == mobileNetworkInfo) {
                if (mobileNetworkInfo.isConnected()) {
                    bRet = true;
                }
            }
        }
        return bRet;
    }

    public static boolean isWiFI(boolean bRealtime) {
        NetworkInfo networkInfo = NetUtils.getCurNetInfo(bRealtime);
        boolean bRet = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
        if (!bRet) {
            bRet = isEtherNet();
        }
        return bRet;
    }

    private static boolean isEtherNet() {
        NetworkInfo networkInfo = NetUtils.getCurNetInfo(false);
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET && networkInfo.isConnected();
    }

    public static boolean isNetworkConnected() {
        NetworkInfo netWorkInfo = NetUtils.getCurNetInfo(false);
        return netWorkInfo != null && netWorkInfo.isConnected();
    }

    public static boolean isNetworkConnected(boolean realTime) {
        NetworkInfo netWorkInfo = NetUtils.getCurNetInfo(realTime);
        return netWorkInfo != null && netWorkInfo.isConnected();
    }

    public String getDensity() {
        if (!TextUtils.isEmpty(this.density)) {
            return this.density;
        }
        return DeviceUtils.getDisplayMetrics(ContextUtils.getApplicationContext()).widthPixels
                + "x" + DeviceUtils.getDisplayMetrics(ContextUtils.getApplicationContext()).heightPixels;
    }

    public String getSDKVersion() {
        if (!TextUtils.isEmpty(this.sdkVersion)) {
            return this.sdkVersion;
        }
        return android.os.Build.VERSION.SDK_INT + "";
    }
}
