package com.dzfd.gids.baselibs.network;

/**
 * @author fengda
 */
public class NetConsts {

    public static final int NET_TYPE_NO_CONNECTION = -1;
    public static final int NET_TYPE_WIFI = 1;
    public static final int NET_TYPE_MOBILE_3G_WAP = 2;
    public static final int NET_TYPE_MOBILE_3G_NET = 3;
    public static final int NET_TYPE_MOBILE_2G_WAP = 4;
    public static final int NET_TYPE_MOBILE_2G_NET = 5;
    public static final int NET_TYPE_DEFAULT = 6;
    public static final int NET_TYPE_MOBILE_4G = 7;

    public static boolean isDataNet(int netType) {
        if (netType == NET_TYPE_MOBILE_3G_WAP
                || netType == NET_TYPE_MOBILE_3G_WAP
                || netType == NET_TYPE_MOBILE_2G_WAP
                || netType == NET_TYPE_MOBILE_2G_NET
                || netType == NET_TYPE_MOBILE_4G) {
            return true;
        }
        return false;
    }

    public static final int NETWORK_TYPE_LTE = 13;
    public static final int MOBILE_NETWORK_TYPE_CM = 1; // 中国移动；
    public static final int MOBILE_NETWORK_TYPE_UNI = 2; // 中国联通；
    public static final int MOBILE_NETWORK_TYPE_CT = 3;  //中国电信；


    public class NetworkState {
        public static final int OK = 0; // 网络正常
        public static final int NO_CONNECTION = 1; // 网络异常
        public static final int UNUSABLE_DUE_TO_SIZE = 2;// 网络异常：超过非wifi下大小限制
    }

}
