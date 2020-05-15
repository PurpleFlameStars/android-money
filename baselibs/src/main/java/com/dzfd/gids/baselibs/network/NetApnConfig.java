package com.dzfd.gids.baselibs.network;


import com.dzfd.gids.baselibs.utils.LogUtils;

public class NetApnConfig {

    private final static String TAG = "NetUtils";

    public int netType;
    public int subNetType;
    public static boolean mUseWap; // 是否正在使用Wap

    public static String mProxy; // 代理服务器
    public static int mPort; // 端口号

    public static String mApn; // 接入点名称

    public NetApnConfig() {
    }

    public NetApnConfig(int netType, int subNetType) {
        this.netType = netType;
        this.subNetType = subNetType;
        LogUtils.d(TAG, "netType: " + netType+ " subNetType: " + subNetType + " mApn: "+ mApn);
    }

    public NetApnConfig(int netType, String netProxyHost, int netProxyPort, int subNetType) {
        this.netType = netType;
        this.mProxy = netProxyHost;
        this.mPort = netProxyPort;
        this.subNetType = subNetType;
        LogUtils.d(TAG, "xx netType: " + netType+ " subNetType: " + subNetType + " mApn: "+ mApn);
    }
}
