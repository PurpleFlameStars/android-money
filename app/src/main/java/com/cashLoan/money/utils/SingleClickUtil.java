package com.cashLoan.money.utils;

public class SingleClickUtil {
    private static long lastClickTime;

    public static boolean isFastDoubleClick(long delay) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (timeD >= 0 && timeD <= delay) {
            return true;
        } else {
            lastClickTime = time;
            return false;
        }
    }
}