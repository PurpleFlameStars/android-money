package com.dzfd.gids.baselibs.utils.thread;

/**
 * @author zhanglong on 2017/9/29.
 */
public class BackgroundPoolExecutors {
    private static final String TAG = "BackgroundExecutors";

    private static final int CORE_POOL_SIZE = 10;
    private static volatile BackgroundExecutors.BackgroundScheduledThreadPoolExecutor sBackgroundExecutor;

    /**
     * 获取一个全局static的后台单线程执行器
     *
     * @return
     */
    public static BackgroundExecutors.BackgroundScheduledThreadPoolExecutor getGlobalExecutor() {
        if (sBackgroundExecutor == null) {
            synchronized (BackgroundExecutors.class) {
                if (sBackgroundExecutor == null) {
                    sBackgroundExecutor = new BackgroundExecutors.BackgroundScheduledThreadPoolExecutor(CORE_POOL_SIZE);
                }
            }
        }
        return sBackgroundExecutor;
    }
}
