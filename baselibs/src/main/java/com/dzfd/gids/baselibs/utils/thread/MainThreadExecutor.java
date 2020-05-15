package com.dzfd.gids.baselibs.utils.thread;

import android.os.Handler;
import android.os.Looper;

import com.dzfd.gids.baselibs.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhanglong on 2016/11/15.
 */
public class MainThreadExecutor {

    private final static String TAG = "MainThreadExecutor";


    private final static Handler MainThreadHandler = new Handler(Looper.getMainLooper());
    private final static MainThreadExecutor s_inst = new MainThreadExecutor();
    //    private final List<String> intercepters = new ArrayList<>();
    private final Map<String, Object> intercepters = new HashMap<>();
    private final List<DelayedTask> wattingTasks = new ArrayList<>();

    public static MainThreadExecutor getGlobalExecutor() {
        return s_inst;
    }

    public void post(Runnable r) {
        MainThreadHandler.post(r);
    }

    public void postDelayed(Runnable r, long delayMillis) {
        MainThreadHandler.postDelayed(r, delayMillis);
    }


    public void postDelayedTask(Runnable r, String id, long delayMillis) {
        DelayedTask task = new DelayedTask(id, MainThreadHandler, r, delayMillis);
        MainThreadHandler.postDelayed(task, delayMillis);
//        pauseDelayedTask(id);
//        MainThreadHandler.postDelayed(r, delayMillis);
    }

    class DelayedTask implements Runnable {

        private long delayMillis;
        private Runnable run;
        private Handler handler;
        private String id;

        DelayedTask(String id, Handler handler, Runnable run, long delayMillis) {
            this.handler = handler;
            this.run = run;
            this.delayMillis = delayMillis;
            this.id = id;
        }

        public Runnable getRunnable() {
            return run;
        }

        public String getId() {
            return id;
        }

        @Override
        public void run() {

            //在有拦截器的情况下，加入等待列表。(此时等待时间已经到了，恢复时不应再延迟)
            if (intercepters.containsKey(id)) {
                LogUtils.d(TAG, "task add to wattingTasks...id=" + id);
                wattingTasks.add(this);
            } else {
                run.run();
            }
        }
    }


    public synchronized void pauseDelayedTask(String id) {

        LogUtils.d(TAG, "pauseDelayedTask...id=" + id);
        intercepters.put(id, id);

    }

    public synchronized void resumeDelayedTask(String id) {
        LogUtils.d(TAG, "resumeDelayedTask...id=" + id);

        if (intercepters.containsKey(id)) {
            //去掉对应的拦截器
            intercepters.remove(id);

            for (int i = wattingTasks.size() - 1; i >= 0; i--) {
                DelayedTask task = wattingTasks.get(i);

                //将匹配的任务恢复执行
                if (task != null && task.getId().equals(id)) {
                    LogUtils.d(TAG, "resumeDelayedTask...post id=" + id);

                    MainThreadHandler.post(task.getRunnable());
                    wattingTasks.remove(task);
                    //一个id只对应唯一的一个任务，直接返回
                    return;

                }
            }
        }

    }


    public void remove(Runnable r) {
        MainThreadHandler.removeCallbacks(r);
    }
}
