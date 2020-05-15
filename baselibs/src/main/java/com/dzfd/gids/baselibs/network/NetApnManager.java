package com.dzfd.gids.baselibs.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.dzfd.gids.baselibs.utils.ContextUtils;


public class NetApnManager {

    private static final String TAG = "NetApnManagerImpl";

    private static final int NETWORK_TYPE_EVDO_B = 12;
    private static final int NETWORK_TYPE_LTE = 13;
    private static final int NETWORK_TYPE_EHRPD = 14;
    private static final int NETWORK_TYPE_HSPAP = 15;

    private static final String CHINA_MOBILE_46000 = "46000";
    private static final String CHINA_MOBILE_46002 = "46002";
    private static final String CHINA_MOBILE_46007 = "46007";
    private static final String CHINA_UNICOM_46001 = "46001";
    private static final String CHINA_TELECOM_46003 = "46003";

    public static NetApnConfig getNetApnConfig() {
        try {
            NetworkInfo info = NetUtils.getCurNetInfo(false);

            if (info == null || !info.isAvailable()) {
                return new NetApnConfig(NetConsts.NET_TYPE_NO_CONNECTION, -2);
            }

            int type = info.getType();
            if (type == ConnectivityManager.TYPE_WIFI) {
                return new NetApnConfig(NetConsts.NET_TYPE_WIFI, -1);
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                int subType = info.getSubtype();
                String proxyHost = android.net.Proxy.getDefaultHost();
                int proxyPort = android.net.Proxy.getDefaultPort();
                switch (subType) {
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                        return get2GApnConfig(proxyHost, proxyPort, subType);
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case NetApnManager.NETWORK_TYPE_EVDO_B:
                    case NetApnManager.NETWORK_TYPE_EHRPD:
                    case NetApnManager.NETWORK_TYPE_HSPAP:
                        return get3GApnConfig(proxyHost, proxyPort, subType);
                    case NetApnManager.NETWORK_TYPE_LTE:
                        return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_4G, subType);
                    case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                        return new NetApnConfig(NetConsts.NET_TYPE_DEFAULT, subType);
                    default:
                        return new NetApnConfig(NetConsts.NET_TYPE_DEFAULT, subType);
                }
            }
            return new NetApnConfig(NetConsts.NET_TYPE_DEFAULT, 0);
        } catch (Exception e) {
            return new NetApnConfig(NetConsts.NET_TYPE_NO_CONNECTION, -2);
        }
    }

    private static NetApnConfig get2GApnConfig(String proxyHost, int proxyPort, int subType) {
        try {
            if (proxyHost == null || proxyHost.equals("") || proxyPort == -1) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_NET, subType);
            }

            TelephonyManager mTelephonyManager = (TelephonyManager) ContextUtils.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

            String operator = mTelephonyManager.getSimOperator();
            if (operator == null || operator.equals("")) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_NET, subType);
            }
            if (operator.equals(CHINA_MOBILE_46000) || operator.equals(CHINA_MOBILE_46002) || operator.equals(CHINA_MOBILE_46007)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_NET, subType);
            } else if (operator.equals(CHINA_UNICOM_46001)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_WAP, proxyHost, proxyPort, subType);
            } else if (operator.equals(CHINA_TELECOM_46003)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_NET, subType);
            } else {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_2G_NET, subType);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private static NetApnConfig get3GApnConfig(String proxyHost, int proxyPort, int subType) {
        try {
            if (proxyHost == null || proxyHost.equals("") || proxyPort == -1) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_NET, subType);
            }
            TelephonyManager mTelephonyManager = (TelephonyManager) ContextUtils.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            String operator = mTelephonyManager.getSimOperator();
            if (operator == null || operator.equals("")) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_NET, subType);
            }
            if (operator.equals(CHINA_MOBILE_46000) || operator.equals(CHINA_MOBILE_46002) || operator.equals(CHINA_MOBILE_46007)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_NET, subType);
            } else if (operator.equals(CHINA_UNICOM_46001)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_WAP, proxyHost, proxyPort, subType);
            } else if (operator.equals(CHINA_TELECOM_46003)) {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_NET, subType);
            } else {
                return new NetApnConfig(NetConsts.NET_TYPE_MOBILE_3G_NET, subType);
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }
}
