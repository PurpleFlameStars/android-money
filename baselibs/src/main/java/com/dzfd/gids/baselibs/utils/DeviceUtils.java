package com.dzfd.gids.baselibs.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

import com.dzfd.gids.baselibs.apk.sign.ApkSignerV2UtilTool;
import com.dzfd.gids.baselibs.apk.sign.SignApkTool;
import com.dzfd.gids.baselibs.utils.hideapi.HideApiHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DeviceUtils {

    private static final String TAG = "DeviceUtils";
    private static final String DEFAULT_IMEI = "360_DEFAULT_IMEI";
    private static final String APP_STORE_IMEI0 = "hot_vedio_imei0_new";
    private static final String APP_STORE_IMEI = "hot_vedio_imei";
    private static final String PRELOADED_CONF = "/system/etc/lightsky_conf";
    //    private static final String PRELOADED_CONF = "/sdcard/lightsky_conf";
    public static int screen_width = 0;
    public static int screen_height = 0;
    private static String sImei_md5;
    private static final String DEFAULT_CHANNEL = "10001";
    private static String apkChannel;
    private static String vid;
    private static int hasNavigationBar = -1;
//    private static String archName;

    private static List<String> abiList;

    // 手机品牌判断
    private static final int ANDROID_SDK_VERSION = Build.VERSION.SDK_INT;
    private static final String PRODUCT = Build.PRODUCT.toLowerCase();
    private static final String MODEL = Build.MODEL.toLowerCase();
    private static final String BRAND = Build.BRAND.toLowerCase();
    private static final String MANUFACTURER = Build.MANUFACTURER.toLowerCase();
    private static final String HOST = Build.HOST.toLowerCase();
    private static final String DISPLAY = Build.DISPLAY.toLowerCase();
    private static final String FINGERPRINT = Build.FINGERPRINT.toLowerCase();

    private static int statusBarHeight;

    private static int getStatusBarHeightByReflect() {
        int heightId = 0;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            heightId = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heightId;
    }

    public static boolean isMeizu() {
        if (!TextUtils.isEmpty(MANUFACTURER) && MANUFACTURER.equals("meizu")) {
            return true;
        }
        return false;
    }

    public static boolean isXiaoMi() {
        if (TextUtils.isEmpty(BRAND)) {
            return false;
        }
        return BRAND.equalsIgnoreCase("xiaomi");
    }


    public static boolean isMeizuM3Note() {
        return PRODUCT.contains("m3note");
    }


    public static boolean isMeizuPro7Plus() {
        if (TextUtils.isEmpty(MANUFACTURER)) {
            return false;
        }
        if (TextUtils.isEmpty(MODEL)) {
            return false;
        }
        return "meizu".equals(MANUFACTURER.toLowerCase()) && "pro 7 plus".equals(MODEL.toLowerCase());
    }

    public static boolean isVivoX20() {
        if (TextUtils.isEmpty(MANUFACTURER)) {
            return false;
        }
        if (TextUtils.isEmpty(MODEL)) {
            return false;
        }
        return "vivo".equals(MANUFACTURER.toLowerCase()) && "vivo x20a".equals(MODEL.toLowerCase());
    }

    public static boolean isSonyZ5P() {
        if (TextUtils.isEmpty(MANUFACTURER)) {
            return false;
        }
        if (TextUtils.isEmpty(MODEL)) {
            return false;
        }
        return "sony".equals(MANUFACTURER.toLowerCase()) && "e6883".equals(MODEL.toLowerCase());
    }

    public static boolean isDebuggable() {
        try {
            PackageManager pm = ContextUtils.getApplicationContext().getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(ContextUtils.getApplicationContext().getPackageName(), 0);
            return (0 != (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static float getScreenAspectRatio(Context ctx) {
        if (screen_width == 0) {
            Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screen_width = screenSize.getWidth();
        }

        if (screen_height == 0) {
            Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screen_height = screenSize.getHeight();
        }

        if (screen_height != 0 && screen_width != 0) {
            return (float) screen_width / (float) screen_height;
        }

        return 0.5625f;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidth(Context ctx) {
        if (screen_width == 0) {
            Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screen_width = screenSize.getWidth();
        }

        return screen_width;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenWidthNoCache(Context ctx) {
        Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return screenSize.getWidth();
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeight(Context ctx) {
        if (screen_height == 0) {
            Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screen_height = screenSize.getHeight();
        }
        return screen_height;
    }

    @SuppressWarnings("deprecation")
    public static int getScreenHeightNoCache(Context ctx) {
        Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        return screenSize.getHeight();
    }

    public static boolean checkDeviceHasNavigationBar(Context activity) {
        if (hasNavigationBar != -1) {
            return hasNavigationBar == 1;
        }
        try {
            //通过判断设备是否有返回键、菜单键(不是虚拟键,是手机屏幕外的按键)来确定是否有navigation bar
            boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

            //vivoX20 判断是否有back键判断错误，故此处做适配
            if (isVivoX20()) {
                hasBackKey = false;
            }

            if (isSonyZ5P()) {
                hasBackKey = false;
            }

            if (!hasMenuKey && !hasBackKey) {
                hasNavigationBar = 1;
                return true;
            }
        } catch (Exception e) {

        }
        hasNavigationBar = 0;
        return false;
    }

    /**
     * 获取设备ID号(国际移动设备身份码，储存在移动设备中，全球唯一的一组号码)
     *
     * @return
     */
    public static String getIMEI(Context context) {
        try {
            String sImei = (String) SPUtils.get( APP_STORE_IMEI0, "");
            if (!TextUtils.isEmpty(sImei)) {
                return new String(Base64.decode(sImei, Base64.DEFAULT));
            }
            sImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            if (!TextUtils.isEmpty(sImei)) {
                String sImeiBase64 = Base64.encodeToString(sImei.getBytes(), Base64.DEFAULT);
                SPUtils.put(APP_STORE_IMEI0, sImeiBase64);
                return sImei;
            }
        } catch (Exception e) {
        }
        return DEFAULT_IMEI;
    }

    public static String getIMEIMd5(Context c) {
        if (!TextUtils.isEmpty(sImei_md5)) {
            return sImei_md5;
        }

        try {
            sImei_md5 = MD5.md5LowerCase(getIMEI(c));
            return sImei_md5;
        } catch (Exception e) {
            return "";
        }
    }

    public static String getIMEI2(Context context) {
        String sImei2 = (String) SPUtils.get( APP_STORE_IMEI, "");

        if (!TextUtils.isEmpty(sImei2)) {
            return sImei2;
        }

        String imei = getIMEI(context);
        String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        String serialNo = getDeviceSerial();
        sImei2 = MD5.md5LowerCase("" + imei + androidId + serialNo);
        LogUtils.d("getIMEI2", "imei = " + imei + ", androidId = " + androidId + ", serialNo = " + serialNo + ", sImei2 = " + sImei2);
        SPUtils.put(APP_STORE_IMEI, sImei2);
        return sImei2;
    }

    private static String sDesKey;

    public static String getDESKey(Context context) {
        if (TextUtils.isEmpty(sDesKey)) {
            String androidId = android.provider.Settings.System.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            String serialNo = getDeviceSerial();
            sDesKey = MD5.md5LowerCase("lightsky" + androidId + serialNo);
        }
        return sDesKey;
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


    public static String getModel() {
        String model = null;
        try {
            model = URLEncoder.encode(Build.MODEL, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return model;
    }

    private static final String channelIdKey = "channel_id";
    private static final String channelVIdKey = "channel_vid";

    private static Properties getPreloadedConfProperties() {

        File file = new File(PRELOADED_CONF);

        if (!file.exists()) {
            return null;
        }

        InputStream in = null;
        ByteArrayOutputStream out = null;
        Properties properties = new Properties();
        try {
            out = new ByteArrayOutputStream();
            EncryptUtils.decryptFile(file, out);
            in = new ByteArrayInputStream(out.toByteArray());
            properties.load(in);
        } catch (Throwable ignored) {
//            LogUtils.d(TAG, e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {
            }
        }
        return properties;
    }

    //获取预装渠道号
    private static String readPreloadedChannel() {

        String channel = null;

        try {
            if (Build.MODEL != null) {
//                if (Build.MODEL != null && Build.MODEL.startsWith("GM-")) {
                Properties prop = getPreloadedConfProperties();
                if (prop != null) {
                    channel = prop.getProperty("cid", "");
                }
            }
        } catch (Throwable thr) {
            LogUtils.e(TAG, "", thr);
        }
        return channel;

    }

    public static String getVid(Context context) {
        if (!TextUtils.isEmpty(vid)) {
            return vid;
        }

        vid = (String) SPUtils.get(channelVIdKey, "");

        if (TextUtils.isEmpty(vid)) {
            ChannelInfo channelInfo = getV2Channel(context);
            if (channelInfo != null && !TextUtils.isEmpty(channelInfo.vid)) {
                vid = channelInfo.vid;
                SPUtils.put(channelVIdKey, vid);
                LogUtils.e(TAG, "getVid(),vid=" + channelInfo.vid);
            }
        }

        return vid;
    }

    public static class ChannelInfo {
        public String channel;
        public String vid;
    }


    public static ChannelInfo getV2Channel(Context context) {

        try {
            String apkPath = context.getPackageCodePath();

            ByteBuffer afterApkbuffer = new ApkSignerV2UtilTool().getApkByteBuffer(apkPath);

            String channelInfoStr = new SignApkTool().readChanael(afterApkbuffer);
            if (channelInfoStr != null) {
                String[] info = channelInfoStr.split(":");
                ChannelInfo channelInfo = new ChannelInfo();
                channelInfo.channel = info[0];
                if (info.length > 1) {
                    channelInfo.vid = info[1];
                }
                return channelInfo;
            }
        } catch (Throwable thr) {
            LogUtils.e(TAG, "", thr);
        }
        return null;
    }

    public static int getStatusBarHeight(Context context) {
        if (statusBarHeight != 0)
            return statusBarHeight;

        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static boolean isPortrait(Activity activity) {
        if (activity == null) {
            return true;
        }
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager manager = activity.getWindowManager();
        if (manager == null) {
            return true;
        }
        manager.getDefaultDisplay().getMetrics(dm);

        int mWidth = dm.widthPixels;
        int mHeight = dm.heightPixels;

        return mHeight > mWidth;
    }

    /**
     * 判断是否是大于特定魅族flyme系统版本
     */
    public static boolean isMoreThanFlymeVersion(String version) {
        /* 获取魅族系统操作版本标识*/
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        String meizuFlymeOSFlag = getSystemProperty("ro.build.display.id", "");
        if (TextUtils.isEmpty(meizuFlymeOSFlag)) {
            return false;
        } else if (meizuFlymeOSFlag.contains("flyme") || meizuFlymeOSFlag.toLowerCase().contains("flyme")) {
            return meizuFlymeOSFlag.toLowerCase().compareTo(version.toLowerCase()) >= 0;
        } else {
            return false;
        }
    }


    /**
     * 获取系统属性
     *
     * @param key          ro.build.display.id
     * @param defaultValue 默认值
     * @return 系统操作版本标识
     */
    private static String getSystemProperty(String key, String defaultValue) {
        try {
            Class<?> clz = Class.forName("android.os.SystemProperties");
            Method get = clz.getMethod("get", String.class, String.class);
            return (String) get.invoke(clz, key, defaultValue);
        } catch (ClassNotFoundException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }

    private static boolean isMiitChannel = false;
    private static boolean hasInitMiitChannel = false;


    public static boolean isSupportArch(String abi) {
        return getArchList().contains(abi);
    }

    public static String getMemory() {
        try {
            Method _readProclines = null;
            Class procClass;
            procClass = Class.forName("android.os.Process");
            Class parameterTypes[] = new Class[]{String.class, String[].class, long[].class};
            _readProclines = procClass.getMethod("readProcLines", parameterTypes);
            Object arglist[] = new Object[3];
            final String[] mMemInfoFields = new String[]{"MemTotal:",
                    "MemFree:", "Buffers:", "Cached:"};
            long[] mMemInfoSizes = new long[mMemInfoFields.length];
            mMemInfoSizes[0] = 30;
            mMemInfoSizes[1] = -30;
            arglist[0] = new String("/proc/meminfo");
            arglist[1] = mMemInfoFields;
            arglist[2] = mMemInfoSizes;
            if (_readProclines != null) {
                _readProclines.invoke(null, arglist);
                for (int i = 0; i < mMemInfoSizes.length; i++) {
                    return (mMemInfoSizes[i] / 1024) + "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static List<String> getArchList() {
        if (abiList == null) {
            abiList = new ArrayList<>();

            String abi1 = HideApiHelper.SystemProperties.get("ro.product.cpu.abi", "unknown");
            String abi2 = HideApiHelper.SystemProperties.get("ro.product.cpu.abi2", "unknown");
            String abilist = HideApiHelper.SystemProperties.get("ro.product.cpu.abilist", "unknown");

            if (!TextUtils.isEmpty(abi1)) {
                abiList.add(abi1);
            }

            if (!TextUtils.isEmpty(abi2)) {
                abiList.add(abi2);
            }

            if (!TextUtils.isEmpty(abilist)) {
                String[] abis = abilist.split(",");
                if (abis != null && abis.length > 0)
                    Collections.addAll(abiList, abis);
            }
        }
        return abiList;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager != null && manager.getDefaultDisplay() != null) {
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                manager.getDefaultDisplay().getRealMetrics(metrics);
            } else {
                manager.getDefaultDisplay().getMetrics(metrics);
            }
        }
        return metrics;
    }
 }