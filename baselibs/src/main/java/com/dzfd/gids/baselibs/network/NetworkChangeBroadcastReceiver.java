package com.dzfd.gids.baselibs.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.dzfd.gids.baselibs.utils.LogUtils;
import com.dzfd.gids.baselibs.utils.ProcessUtils;


/**
 * 全局网络变化广播接收器
 * 解决：部分手机收不到系统网络变化广播的问题
 * 1、部分手机（如：HUAWEI RIO-UL00 Android5.1）子进程代码，动态注册网络广播无法收到通知的问题，改用AndroidManifest.xml中静态注册；
 * 2、部分手机（如：Lenovo P770 Android4.1.1）无论什么进程，AndroidManifest.xml静态注册网络广播无法收到通知的问题；
 * 所以：动态，静态都注册广播；
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NetworkChangeBroadcastReceiver";
    private static final String CONNECTIVITY_ACTION_TO_OTHER_PROCESS = ConnectivityManager.CONNECTIVITY_ACTION + "_TO_OTHER_PROCESS";
    private static String sInProcessName;
    private static BroadcastReceiver sReceiver;
    private static InnerNetworkChangeBroadcastReceiver sReceiverSystem;

    /**
     * 初始化：Application的attachBaseContext或者onCreate里面调用；
     *
     * @param context
     */
    public static void create(Context context, String inProcessName) {
        sInProcessName = inProcessName;
        if (isSelfProcess(context)) {
            sReceiverSystem = new InnerNetworkChangeBroadcastReceiver();
            try {
                context.registerReceiver(sReceiverSystem, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            } catch (Exception ex) {
                if (LogUtils.isDebug()) {
                    ex.printStackTrace();
                }
            }
        } else {
            sReceiver = new NetworkChangeBroadcastReceiver();
            try {
                context.registerReceiver(sReceiver, new IntentFilter(CONNECTIVITY_ACTION_TO_OTHER_PROCESS));
            } catch (Exception ex) {
                if (LogUtils.isDebug()) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 销毁：Application退出时调用；
     *
     * @param context
     */
    public static void destroy(Context context) {
        if (sReceiver != null) {
            try {
                context.unregisterReceiver(sReceiver);
            } catch (Exception e) {
            }

            sReceiver = null;
        }
        if (sReceiverSystem != null) {
            try {
                context.unregisterReceiver(sReceiverSystem);
            } catch (Exception e) {
            }

            sReceiverSystem = null;
        }
    }

    private static boolean isSelfProcess(Context context) {
        String processName = ProcessUtils.getCurrentProcessName();
        if (TextUtils.isEmpty(sInProcessName)) {
            try {
                ActivityInfo[] activities = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_RECEIVERS).receivers;
                for (ActivityInfo activityInfo : activities) {
                    if (activityInfo.name.equals(NetworkChangeBroadcastReceiver.class.getName()) && activityInfo.processName.equals(processName)) {
                        return true;
                    }
                }
            } catch (PackageManager.NameNotFoundException ignore) {
            }
            return false;
        } else {
            return sInProcessName.equals(processName);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "onReceive.processName = " + ProcessUtils.getCurrentProcessName() + ", intent = " + LogUtils.intentToString(intent));
        }
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            if (sReceiverSystem != null
                    && !sReceiverSystem.sIsRepect
                    && intent.getComponent() != null) { // 来自AndroidManifest.xml静态注册系统网络广播时，Component不为空；
//                context.unregisterReceiver(sReceiverSystem); // TODO 不知道为何会抛异常：java.lang.IllegalArgumentException: Receiver not registered: xxx
//                sReceiverSystem  = null;
                sReceiverSystem.sIsRepect = true;
            }
            intent.setComponent(null);
            intent.setAction(CONNECTIVITY_ACTION_TO_OTHER_PROCESS);
            intent.setPackage(context.getPackageName());
            try {
                context.sendBroadcast(intent);
            } catch (Exception ex) {
            }
            NetworkMonitor.getGlobalNetworkMonitor().notifyObserverNetworkChanged(NetUtils.getCurNetInfo(true));
        } else if (CONNECTIVITY_ACTION_TO_OTHER_PROCESS.equals(action)) {
            NetworkMonitor.getGlobalNetworkMonitor().notifyObserverNetworkChanged(NetUtils.getCurNetInfo(true));
        }
    }

    private static class InnerNetworkChangeBroadcastReceiver extends BroadcastReceiver {
        private boolean mIsFirstReceived; // 首次收到通知时忽略：ConnectivityManager.CONNECTIVITY_ACTION是sendStickyBroadcast出来的，代码动态注册立马会收到系统通知，这里保持和xml静态注册时一致；
        public boolean sIsRepect;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mIsFirstReceived) {
                mIsFirstReceived = true;
                return;
            }
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                if (LogUtils.isDebug()) {
                    LogUtils.d(TAG, "onReceive.sIsRepect = " + sIsRepect);
                }
                if (!sIsRepect) {
                    try {
                        context.sendBroadcast(intent.setAction(CONNECTIVITY_ACTION_TO_OTHER_PROCESS).setPackage(context.getPackageName()));
                    } catch (Exception ex) {
                    }

                    NetworkMonitor.getGlobalNetworkMonitor().notifyObserverNetworkChanged(NetUtils.getCurNetInfo(true));
                }
            }
        }
    }
}