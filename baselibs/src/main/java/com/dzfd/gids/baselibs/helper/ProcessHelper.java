package com.dzfd.gids.baselibs.helper;

import android.content.Context;

import com.dzfd.gids.baselibs.utils.ProcessUtils;

/**
 * Created by zhangdecheng on 2018/3/8.
 */
public class ProcessHelper {
    private static String MAIN_PROCESS;
    private static String CORE_PROCESS;
    private static String STAT_PROCESS;

    private static final int PROCESS_FLAG_OTHER = 0;
    private static final int PROCESS_FLAG_MAIN = 1;
    private static final int PROCESS_FLAG_CORE = 2;
    private static final int PROCESS_FLAG_STAT = 3;

    private static int processFlag = PROCESS_FLAG_OTHER;

    public static void initProcessInfo(Context context) {
        MAIN_PROCESS = context.getPackageName();
        CORE_PROCESS = MAIN_PROCESS + ":core";
        STAT_PROCESS = MAIN_PROCESS + ":stat";
        String processName = ProcessUtils.getCurrentProcessName();
        if (MAIN_PROCESS.equals(processName)) {
            processFlag = PROCESS_FLAG_MAIN;
        } else if (CORE_PROCESS.equals(processName)) {
            processFlag = PROCESS_FLAG_CORE;
        } else if (STAT_PROCESS.equals(processName)) {
            processFlag = PROCESS_FLAG_STAT;
        }
    }

    public static boolean isUiProccess() {
        return processFlag == PROCESS_FLAG_MAIN;
    }

    public static boolean isCoreProcess() {
        return processFlag == PROCESS_FLAG_CORE;
    }

    public static boolean isStatProcess() {
        return processFlag == PROCESS_FLAG_STAT;
    }

    public static String getProcessName() {
        if (isUiProccess()) {
            return "main";
        } else if (isCoreProcess()) {
            return "core";
        } else if (isStatProcess()) {
            return "stat";
        } else {
            return "unknown";
        }
    }
}
