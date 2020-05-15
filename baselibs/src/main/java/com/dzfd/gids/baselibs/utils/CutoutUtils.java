package com.dzfd.gids.baselibs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowInsets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CutoutUtils {
    @TargetApi(28)
    public static int getCutoutHight(Context cxt){
        if(Build.VERSION.SDK_INT<28){
            if(hasNotchAtHuawei(cxt)){
                int[] ret=getNotchSizeAtHuawei(cxt);
                return ret[1];
            }else if(hasNotchInScreenAtOPPO(cxt)){
                int[] ret=getNotchSizeAtOppo(cxt);
                return ret[1];
            }
            return 0;
        }
        if( (cxt == null) || !(cxt instanceof Activity)){
            return 0;
        }
        Activity activity = (Activity)cxt;
        View decorView = activity.getWindow().getDecorView();
        if(decorView == null){
            return 0;
        }
        WindowInsets insets=decorView.getRootWindowInsets();
        return getCutoutHight(insets);

    }
    @TargetApi(28)
    public static int getCutoutHight(WindowInsets insets){
        if(insets == null){
            return 0;
        }
        int nretvalue=0;
        try {
            Class<?> clazz = Class.forName("android.view.WindowInsets");
            Method DisplayCutoutmethod= clazz.getDeclaredMethod("getDisplayCutout");
            Object DisplayCutout=DisplayCutoutmethod.invoke(insets);
            Class<?> target= Class.forName("android.view.DisplayCutout");
            Method methodsafetop=target.getDeclaredMethod("getSafeInsetTop");
            if(methodsafetop!=null){
                Object savetop=methodsafetop.invoke(DisplayCutout);
                if(savetop instanceof Integer){
                    nretvalue=(Integer)savetop;
                }
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (NoSuchMethodException e){
            e.printStackTrace();
        }catch (IllegalAccessException e){
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return nretvalue;
    }

    private static boolean hasNotchAtHuawei(Context context) {
        boolean ret = false;
        try {
            ClassLoader classLoader = context.getClassLoader();
            Class HwNotchSizeUtil = classLoader.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
            ret = (boolean) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }
    private static int[] getNotchSizeAtHuawei(Context context) {
        int[] ret = new int[]{0, 0};
        try {
            ClassLoader cl = context.getClassLoader();
            Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
            Method get = HwNotchSizeUtil.getMethod("getNotchSize");
            ret = (int[]) get.invoke(HwNotchSizeUtil);
        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
        } finally {
            return ret;
        }
    }
    public static boolean hasNotchInScreenAtOPPO(Context context) {
        return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
    }
    public static int[] getNotchSizeAtOppo(Context context){
        int[] ret={324,85};
        return ret;
    }


}
