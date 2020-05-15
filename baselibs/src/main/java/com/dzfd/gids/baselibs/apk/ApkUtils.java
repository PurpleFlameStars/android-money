package com.dzfd.gids.baselibs.apk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.dzfd.gids.baselibs.utils.ContextUtils;

import java.io.File;

/**
 * @author zhanglong on 2016/11/18.
 */
public class ApkUtils {

    public static final String RELEASE_SIG = "42f9f344415c71ad1221a8c2e643da70";

    public static String getPackageVersionName(Context context, String packname) {
        PackageInfo pi = getPackageInfo(context, packname);
        return pi == null ? null : pi.versionName;
    }

    public static int getPackageVersionCode(Context context, String packname) {
        PackageInfo pi = getPackageInfo(context, packname);
        return pi == null ? 0 : pi.versionCode;
    }

    public static void installApk(Context context, String path) {
        if (context == null || TextUtils.isEmpty(path))
            return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(new File(path)),
                "application/vnd.android.package-archive");
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            //activityNotFoundException
        }
    }

    private static PackageInfo getPackageInfo(Context context, String packname) {
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getApplicationContext().getPackageManager();
            pi = pm.getPackageInfo(packname, 0);
        } catch (Exception e) {
        }
        return pi;
    }

    public static String getPackageNameByFilePath(String path) {
        if (TextUtils.isEmpty(path))
            return null;

        PackageInfo info = ContextUtils.getApplicationContext().getPackageManager().getPackageArchiveInfo(path, 0); // 用初始化时的路径取版本号
        if (info == null)
            return null;

        return info.packageName;
    }

    public static int getPackageVersCodeByFilePath(String path) {
        if (TextUtils.isEmpty(path))
            return 0;

        PackageInfo info = ContextUtils.getApplicationContext().getPackageManager().getPackageArchiveInfo(path, 0); // 用初始化时的路径取版本号
        if (info == null)
            return 0;

        return info.versionCode;
    }

    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        if (packageName.equals(context.getPackageName())) {
            return true;
        }
        boolean result = false;

        try {
            result = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_DISABLED_COMPONENTS) != null;
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {
//            java.lang.RuntimeException: Package manager has died
//            at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:77)
//            at com.qihoo.utils.ApkUtils.isApkInstalled(AppStore:191)
//            at com.qihoo.appstore.base.AppStoreApplication$13.run(AppStore:576)
//            at android.os.Handler.handleCallback(Handler.java:725)
//            at android.os.Handler.dispatchMessage(Handler.java:92)
//            at android.os.Looper.loop(Looper.java:137)
//            at android.os.HandlerThread.run(HandlerThread.java:60)
//            Caused by: android.os.TransactionTooLargeException
//            at android.os.BinderProxy.transact(Native Method)
//            at android.content.pm.IPackageManager$Stub$Proxy.getPackageInfo(IPackageManager.java:1362)
//            at java.lang.reflect.Method.invokeNative(Native Method)
//            at java.lang.reflect.Method.invoke(Method.java:511)
//            at com.morgoo.droidplugin.hook.HookedMethodHandler.doHookInner(AppStore:51)
//            at com.morgoo.droidplugin.hook.proxy.ProxyHook.invoke(AppStore:60)
//            at $Proxy8.getPackageInfo(Native Method)
//            at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:72)
        }

        return result;
    }

    public static int getVersionCode(Context context) {
        if (context == null)
            return 0;

        return ApkUtils.getPackageVersionCode(context, context.getPackageName());
    }

    public static String getVersionName(Context context) {
        if (context == null)
            return "";

        return ApkUtils.getPackageVersionName(context, context.getPackageName());
    }

    public static String[] getApkSignMd5Array(String apkPath) {
        return ApkSignHelper.getApkSignMd5Array(apkPath);
    }

    public static String getApkSignMd5(String apkFile) {
        return ApkSignHelper.getApkSignMd5(apkFile);
    }

    public static PackageInfo getInstalledApp(Context context, String packageName) {
        return getInstalledApp(context, packageName, 0);
    }

    public static PackageInfo getInstalledApp(Context context, String packageName, int flags) {
        PackageInfo info = null;

        PackageManager pm = context.getPackageManager();
        try {
            if (pm != null) {
                info = pm.getPackageInfo(packageName, flags);
            }
        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
        }

        return info;
    }

    /**
     * 判断某个应用是否为系统应用
     */
    public static boolean isSystemApp(ApplicationInfo info) {
        if (info != null && (info.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
            return !(info.publicSourceDir.startsWith("data/dataapp") || info.publicSourceDir.startsWith("/data/dataapp"));
        }
        return false;
    }

    public static String getChannelName() {

        String channelName = null;
        try {
            PackageManager packageManager = ContextUtils.getApplicationContext().getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.
                        getApplicationInfo(ContextUtils.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = String.valueOf(applicationInfo.metaData.get("GOLEMON_CHANNEL"));
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channelName;
    }
}
