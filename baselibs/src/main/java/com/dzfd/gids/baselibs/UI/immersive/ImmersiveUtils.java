package com.dzfd.gids.baselibs.UI.immersive;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.dzfd.gids.baselibs.utils.DeviceUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ImmersiveUtils {

    private static String mAdaptVersion = "Flyme 6.1.2.5A";
    private static String BRANDNAME = "Meizu".toLowerCase();
    private static String MODEL = "PRO 7 Plus".toLowerCase();

    public static boolean setImmerseLayout(Activity activity) {
        if (!isSupported()){
            return false;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            setImmerseLayoutHighApiLevel(activity);
        }
        return true;
    }

    public static int getStatusBarHeight(Context context) {
        return DeviceUtils.getStatusBarHeight(context);
    }

    public static void showView(View v) {
        if (v == null) {
            return;
        }

        if (!isSupported()) {
            v.setVisibility(View.GONE);
            return;
        }

        v.setVisibility(View.VISIBLE);
    }

    public static void dismissView(View v) {
        if (v == null) {
            return;
        }

        v.setVisibility(View.GONE);
    }

    public static boolean isSupported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    /**
     * SYSTEM_UI_FLAG_LIGHT_STATUS_BAR 在6.0可以自适应状态栏字体深色
     * 6.0以下小米和魅族可支持状态栏字体深色
     *
     * @param activity
     */
    private static void setImmerseLayoutHighApiLevel(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;
        //适配版本  http://open-wiki.flyme.cn/index.php?title=Flyme%E7%B3%BB%E7%BB%9FAPI#.E4.B8.80.E3.80.81.E6.B2.89.E6.B5.B8.E5.BC.8F.E7.8A.B6.E6.80.81.E6.A0.8F
        //经过测试发现 只有蔡磊的Meizu Pro7 Plus才需要做这种会适配
        if (DeviceUtils.isMoreThanFlymeVersion(mAdaptVersion) && DeviceUtils.isMeizuPro7Plus()) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            try {
                if (DeviceUtils.isXiaoMi()) {
                    MIUISetStatusBarLightMode(activity, false);
                }

                Window window = activity.getWindow();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                } else {
                    if (DeviceUtils.isMeizu()) {
                        FlymeSetStatusBarLightMode(activity, false);
                    }
                    window.getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                }
                if (!DeviceUtils.isMeizu()) //魅族手机加此行会出白条
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);   //去除半透明状态栏
                window.setStatusBarColor(Color.TRANSPARENT);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 需要MIUIV6以上 android6.0以下
     *
     * @param activity
     * @param dark     是否把状态栏字体及图标颜色设置为深色
     */
    public static boolean MIUISetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            Class clazz = window.getClass();
            try {
                int darkModeFlag = 0;
                Class layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
                Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
                darkModeFlag = field.getInt(layoutParams);
                Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
                if (dark) {
                    extraFlagField.invoke(window, darkModeFlag, darkModeFlag);//状态栏透明且黑色字体
                } else {
                    extraFlagField.invoke(window, 0, darkModeFlag);//清除黑色字体
                }
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

    /**
     * 魅族 是否把状态栏字体及图标颜色设置为深色
     */
    public static boolean FlymeSetStatusBarLightMode(Activity activity, boolean dark) {
        boolean result = false;
        Window window = activity.getWindow();
        if (window != null) {
            try {
                WindowManager.LayoutParams lp = window.getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                window.setAttributes(lp);
                result = true;
            } catch (Exception e) {

            }
        }
        return result;
    }

}
