package com.dzfd.gids.baselibs.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.dzfd.gids.baselibs.utils.ContextUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class NetworkMonitor {

    private final static String TAG = "NetworkMonitor";

    private boolean mIsNetWorkConnect = false;
    private int mNetType = -2;
    private static NetworkInfo curNetworkInfo = getCurNetInfo(true);

    private NetworkMonitor() {
    }

    private static class InstanceHolder {
        static final NetworkMonitor instance = new NetworkMonitor();
    }

    public static NetworkMonitor getGlobalNetworkMonitor() {
        return InstanceHolder.instance;
    }

    private final Map<NetworkMonitorObserver, String> mObservers = new ConcurrentHashMap<NetworkMonitorObserver, String>();

    public interface NetworkMonitorObserver {
        void onNetworkStatusChanged(boolean isConnected);
    }

    public synchronized void addNetworkMonitorObserver(NetworkMonitorObserver observer) {
        if (!mObservers.containsKey(observer)){
            mObservers.put(observer, TAG);
        }
    }

    public synchronized void addStickyNetworkMonitorObserver(NetworkMonitorObserver observer) {
        if (!mObservers.containsKey(observer)){
            mObservers.put(observer, TAG);
        }

        observer.onNetworkStatusChanged(isNetworkConnected(true));
    }

    public synchronized void removeNetworkMonitorObserver(NetworkMonitorObserver observer) {
        mObservers.remove(observer);
    }
    public synchronized void removeAllNetworkMonitorObserver(){
        if(mObservers!=null){
            mObservers.clear();
        }
    }

    public synchronized void notifyObserverNetworkChanged(NetworkInfo newNetworkInfo) {

        curNetworkInfo = newNetworkInfo;
        boolean isNetWorkConnect = false;
        int netType = -1;
        if (newNetworkInfo != null) {
            isNetWorkConnect = newNetworkInfo.isConnected();
            netType = newNetworkInfo.getType();
        }

        if (mIsNetWorkConnect != isNetWorkConnect || netType != mNetType) {

            for (NetworkMonitorObserver observer : mObservers.keySet()) {
                observer.onNetworkStatusChanged(isNetWorkConnect);

            }

        }
        mIsNetWorkConnect = isNetWorkConnect;
        mNetType = netType;
    }

    public static NetworkInfo getNetworkInfo(boolean realTime) {
        if (curNetworkInfo == null || realTime) {
            try {
                curNetworkInfo = ((ConnectivityManager) ContextUtils.getAppContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            } catch (Throwable throwable) {

            }

        }

        return curNetworkInfo;
    }
    public static NetworkInfo getCurNetInfo(boolean realTime) {
        return NetworkMonitor.getNetworkInfo(realTime);
    }
    public static boolean isNetworkConnected(boolean realTime) {
        NetworkInfo netWorkInfo = getCurNetInfo(realTime);
        return netWorkInfo != null && netWorkInfo.isConnected();
    }
    public static NetworkInfo getCurNetInfoByType(int type) {
        return ((ConnectivityManager) ContextUtils.getAppContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(type);
    }
    public static boolean isDataNetwork() {
        NetworkInfo networkInfo = getCurNetInfo(false);
        boolean bRet = networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected();
        if (!bRet) { // 湖南一个移动3G网络用户，他的 netType 是 50 。
            NetworkInfo mobileNetworkInfo = getCurNetInfoByType(ConnectivityManager.TYPE_MOBILE);
            if (mobileNetworkInfo != null && getCurNetInfo(true) == mobileNetworkInfo) {
                if (mobileNetworkInfo.isConnected()) {
                    bRet = true;
                }
            }
        }
        return bRet;
    }
    public static String getNetProvider(){
        String ProvidersName = "0";
        TelephonyManager telephonyManager = (TelephonyManager) ContextUtils.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
        @SuppressLint("MissingPermission") String IMSI = telephonyManager.getSubscriberId();
        if( IMSI == null){
            return ProvidersName;
        }

        if(IMSI.startsWith("46000") || IMSI.startsWith("46002")){
            ProvidersName = "2";
        }else if(IMSI.startsWith("46001")){
            ProvidersName ="3";
        }else if (IMSI.startsWith("46003")) {
            ProvidersName = "1";
        }else{
            ProvidersName="4";
        }

        return ProvidersName;
    }
}
