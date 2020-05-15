package com.dzfd.gids.baselibs.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.reflect.Field;


public class BunToast {
    private static Field sField_TN;
    private static Field sField_TN_Handler;
    private static Toast sToast;

    private BunToast() {
    }

    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
        }
    }

    /**
     * 尝试解决异常
     * 1 android.view.ViewRootImpl.setView(ViewRootImpl.java:922)
     * 2 android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:377)
     * 3 android.view.WindowManagerImpl.addView(WindowManagerImpl.java:105)
     * 4 android.widget.Toast$TN.handleShow(Toast.java:746)
     * 5 android.widget.Toast$TN$2.handleMessage(Toast.java:622)
     * 6 android.os.Handler.dispatchMessage(Handler.java:102)
     * 7 android.os.Looper.loop(Looper.java:154)
     * 8 android.app.ActivityThread.main(ActivityThread.java:6816)
     * 9 java.lang.reflect.Method.invoke(Native Method)
     * 10 com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1563)
     * 11 com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1451)
     * **/
    static {
        try {
            sField_TN = Toast.class.getDeclaredField("mTN");
            sField_TN.setAccessible(true);
            sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
            sField_TN_Handler.setAccessible(true);
        } catch (Exception e) {
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    public static void showShort(Context context, CharSequence message) {
        showToast(context, message, Toast.LENGTH_SHORT);
    }

    public static void showShort(Context context, int message) {
        showToast(context, context.getString(message), Toast.LENGTH_SHORT);
    }

    public static void showLong(Context context, CharSequence message) {
        showToast(context, message, Toast.LENGTH_LONG);
    }

    public static void showLong(Context context, int message) {
        if (context != null) {
            showToast(context, context.getString(message), Toast.LENGTH_LONG);
        }
    }

    public static void show(Context context, CharSequence message, int duration) {
        showToast(context, message, duration);
    }

    public static void show(Context context, int message, int duration) {
        showToast(context, context.getString(message), duration);
    }

    private static void showToast(final Context context, final CharSequence msg, final int duration) {
        if(context == null || TextUtils.isEmpty(msg)){
            return;
        }
        Utils.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (sToast == null) {
                    sToast = Toast.makeText(context.getApplicationContext(), msg, duration);
                } else {
                    sToast.setDuration(duration);
                    sToast.setText(msg);
                }
                hook(sToast);
                sToast.show();
            }
        });
    }

    private static class SafelyHandlerWarpper extends Handler {
        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            impl.handleMessage(msg);//需要委托给原Handler执行
        }
    }
}