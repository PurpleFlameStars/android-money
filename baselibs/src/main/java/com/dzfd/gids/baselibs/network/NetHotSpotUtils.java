package com.dzfd.gids.baselibs.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.TextUtils;


import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;
import com.dzfd.gids.baselibs.utils.StringUtils;

import java.lang.reflect.Method;

public class NetHotSpotUtils {

    private static String TAG = "NetHotSpotUtils";


    public static boolean isNetMetered() {
        return isNetMeteredByAndroid() || isHotspot(ContextUtils.getApplicationContext());
    }

    public static boolean isNetMeteredByAndroid() {
        boolean bRet = false;
        ConnectivityManager connectivityManager = ((ConnectivityManager) ContextUtils.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE));
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            LogUtils.d(TAG, "isNetMeteredByAndroid false ");
            try {
                bRet = connectivityManager.isActiveNetworkMetered();
            } catch (NullPointerException e) {
//                Coolpad 8185 4.1.2(16)
//                java.lang.NullPointerException
//                at android.os.Parcel.readException(Parcel.java:1431)
//                at android.os.Parcel.readException(Parcel.java:1379)
//                at android.net.IConnectivityManager$Stub$Proxy.isActiveNetworkMetered(IConnectivityManager.java:875)
//                at android.net.ConnectivityManager.isActiveNetworkMetered(ConnectivityManager.java:1012)
//                at com.qihoo.utils.net.NetHotSpotUtils.isNetMeteredByAndroid(AppStore:30)
//                at com.qihoo.utils.net.NetHotSpotUtils.isNetMetered(AppStore:22)
//                at com.qihoo.utils.net.NetUtils.isFreeWifi(AppStore:99)
//                at com.qihoo.express.mini.multicast.MulticastMsgManager$2.run(AppStore:109)
//                at java.lang.Thread.run(Thread.java:856)
//                at com.qihoo.utils.thread.PriorityThreadFactory$1.run(AppStore:34)
                if (LogUtils.isDebug()) {
                    e.printStackTrace();
                }
            }
        }
        LogUtils.d(TAG, "isNetMeteredByAndroid result " + bRet);
        return bRet;
    }

    /**
     * @param ctx
     * @return true说明可能是热点，false说明不是热点或者无法判断
     * @author jinmeng
     * @description 判断手机是否连接的是热点，此判断不能保证百分之百准确
     * 判断方法：1.开启热点和连接热点的手机都是4.1及以上,通过隐藏api可判断
     * 2.ssid是以“iphone”结尾
     */

    public static boolean isHotspot(Context ctx) {
        if (ctx != null) {
            WifiManager manager = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = null;
            try {

                info = manager.getConnectionInfo();
            } catch (NullPointerException | IllegalArgumentException e) {
                /*java.lang.NullPointerException: name == null
                 at java.lang.Enum.valueOf(Enum.java:187)
                 at android.net.wifi.SupplicantState.valueOf(SupplicantState.java:33)
                 at android.net.wifi.SupplicantState$1.createFromParcel(SupplicantState.java:255)
                 at android.net.wifi.SupplicantState$1.createFromParcel(SupplicantState.java:253)
                 at android.net.wifi.WifiInfo$1.createFromParcel(WifiInfo.java:361)
                 at android.net.wifi.WifiInfo$1.createFromParcel(WifiInfo.java:344)
                 at android.net.wifi.IWifiManager$Stub$Proxy.getConnectionInfo(IWifiManager.java:805)
                 at android.net.wifi.WifiManager.getConnectionInfo(WifiManager.java:889)*/

                /*java.lang.IllegalArgumentException: ::::: is not a constant in android.net.wifi.SupplicantState
                 at java.lang.Enum.valueOf(Enum.java:192)
                 at android.net.wifi.SupplicantState.valueOf(SupplicantState.java:33)
                 at android.net.wifi.SupplicantState$1.createFromParcel(SupplicantState.java:255)
                 at android.net.wifi.SupplicantState$1.createFromParcel(SupplicantState.java:253)
                 at android.net.wifi.WifiInfo$1.createFromParcel(WifiInfo.java:328)
                 at android.net.wifi.WifiInfo$1.createFromParcel(WifiInfo.java:313)
                 at android.net.wifi.IWifiManager$Stub$Proxy.getConnectionInfo(IWifiManager.java:779)
                 at android.net.wifi.WifiManager.getConnectionInfo(WifiManager.java:825)*/
                if (LogUtils.isDebug()) {
                    e.printStackTrace();
                }
            }
            if (info != null) {
                LogUtils.d(TAG, "isHotspot result A ");
                return getMeteredHint(info) || sssidLegal(info);
            }
        }
        LogUtils.d(TAG, "isHotspot result B " + false);
        return false;
    }

    /**
     * @param info
     * @return true说明是热点，false说明不是热点或者无法判断
     * @author jinmeng
     * @description 通过调用4.1以上隐藏api判断手机是否连接的是热点
     */
    private static boolean getMeteredHint(WifiInfo info) {

        LogUtils.d(TAG, "getMeteredHint begin ");

        int sdkInt = android.os.Build.VERSION.SDK_INT;
        try {
            if (sdkInt >= 16) {
                Method get = WifiInfo.class.getMethod("getMeteredHint");
                get.setAccessible(true);
                boolean bResult = (Boolean) get.invoke(info);
                LogUtils.d(TAG, "getMeteredHint result " + bResult);
                return bResult;
            }
        } catch (Exception e) {
            LogUtils.d(TAG, "getMeteredHint exception result " + false);
        }
        LogUtils.d(TAG, "getMeteredHint exception end " + false);
        return false;
    }

    /**
     * @param info
     * @return true说明可能连接的是热点，false说明不是热点或者无法判断
     * @author jinmeng
     * @description 根据sssid名粗略判断是否连接热点
     */
    private static boolean sssidLegal(WifiInfo info) {
        String ssid = info.getSSID();
        LogUtils.d(TAG, "TestNetwifi sssidLegal begin " + ssid);
        if (!TextUtils.isEmpty(ssid)) {
            ssid = StringUtils.trim(ssid, "*._ \'\"");
            boolean bResult = StringUtils.endWithIgnoreCase(ssid, "iphone");
            LogUtils.d(TAG, "TestNetwifi sssidLegal " + ssid + " " + bResult);
            return bResult;
        }
        LogUtils.d(TAG, "TestNetwifi sssidLegal end " + false);
        return false;
    }
}
