package com.dzfd.gids.baselibs.utils;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Darren on 2019/2/16.
 */

public class ContextUtils {

    private static Context mContext;

    public static void init(Context context) {
        synchronized (ContextUtils.class) {
            mContext = context;
        }
    }
    public static Context getAppContext() {
        synchronized (ContextUtils.class) {
            if (mContext == null) {
                mContext = ReflectAppContext();
            }
            return mContext;
        }
    }

    public static Context getApplicationContext(){
        Context cxt=getAppContext();
        if(cxt!=null){
            return cxt.getApplicationContext();
        }
        return null;
    }

    public static Context ReflectAppContext() {
        Context context = null;
        try {
            Class<?> clazz = Class.forName("android.app.ActivityThread");
            Method method = clazz.getDeclaredMethod("currentApplication", new Class<?>[]{});
            context = (Context) method.invoke(null, new Object[]{});
        } catch (ClassNotFoundException | SecurityException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return context;
    }

}
