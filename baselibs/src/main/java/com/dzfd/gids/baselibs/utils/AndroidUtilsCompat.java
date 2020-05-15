package com.dzfd.gids.baselibs.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Chenyichang on 2016/10/12.
 */

public class AndroidUtilsCompat {

    private static final String TAG = "AndroidUtilsCompat";

    private static class ForegroundApp {
        public static String lastForegroundAppName = null;
        public static int lastForegroundAppPid = 0;
        public static int lastForegroundAppScore = -2;
    }

    public static Drawable getDrawable(Resources resources, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return resources.getDrawable(id);
        } else {
            return resources.getDrawable(id, null);
        }
    }

    public static void setBackgroundDrawable(View view, Resources resources, int id) {
        Drawable d = getDrawable(resources, id);
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }


    public static void setBackgroundDrawable(View view, Drawable d) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackgroundDrawable(d);
        } else {
            view.setBackground(d);
        }
    }

    public static void removeGlobalOnLayoutListener(ViewTreeObserver mViewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mViewTreeObserver.removeGlobalOnLayoutListener(listener);
        } else {
            mViewTreeObserver.removeOnGlobalLayoutListener(listener);
        }
    }

    public static int getColorRes(Resources resources, int id) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return resources.getColor(id);
        } else {
            return resources.getColor(id, null);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void setAlpha(View v, float alpha) {
        if (v == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            v.setAlpha(alpha);
        } else {
            AlphaAnimation mainInAnimation = new AlphaAnimation(alpha, alpha);
            mainInAnimation.setDuration(0);
            mainInAnimation.setFillAfter(true);
            v.startAnimation(mainInAnimation);
        }
    }

    /**
     * 获取内部类所在的外部类的引用对象
     *
     * @param obj
     * @return
     */
    public static Object getOuterClass(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            Field fieldThis0 = obj.getClass().getDeclaredField("this$0"); // this$0特指该内部类所在的外部类的引用，不需要手动定义，编译时自动加上；
            fieldThis0.setAccessible(true);
            return fieldThis0.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public interface AsynctaskRejectedExecutionHandler {
        Object getExtraInfo(Object task);
    }

    /**
     * 全局设置AsyncTask中的线程池为并发线程池，并且当队列满了之后另起一个单独的线程执行任务；
     * 如果设置不同的targetSdkVersion则导致AsyncTask的执行顺序是不一样的，如果设置为小于13，则AsyncTask是并行执行的；如果设置为大于等于13，则会顺序执行；
     */
    public static void setAsyncTaskDefaultExecutor(final AsynctaskRejectedExecutionHandler handler) {
        Class<?> cls = AsyncTask.class;
//        解决：低版本设备AsyncTask在子线程首次实例化（new AsyncTask()）崩溃的问题；
//        java.lang.RuntimeException: Can't create handler inside thread that has not called Looper.prepare()
//        at android.os.Handler.<init>(Handler.java:121)
//        at android.os.AsyncTask$InternalHandler.<init>(AsyncTask.java:421)
//        at android.os.AsyncTask$InternalHandler.<init>(AsyncTask.java:421)
//        at android.os.AsyncTask.<clinit>(AsyncTask.java:152)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) { // Android4.0
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    return null;
                }
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) { // 4.0~4.0.4
            try {
                java.lang.reflect.Method init = cls.getMethod("init");
                init.invoke(cls);
            } catch (Throwable e) {

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) { // 3.2
            try {
                Object objectExecutor = cls.getField("THREAD_POOL_EXECUTOR").get(null);
                if (objectExecutor instanceof ThreadPoolExecutor) {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) objectExecutor;
                    threadPoolExecutor.allowCoreThreadTimeOut(true);
                    RejectedExecutionHandler rejectedExecutionHandler = new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            throw new RejectedExecutionException(
                                    "setAsyncTaskDefaultExecutor.rejectedExecution"
                                            + ".Tasks: " + getAsyncTaskNamesOnRejectedExecution(r, executor, handler)
                                            + " rejected from " + executor.toString());
                        }
                    };
                    threadPoolExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
                }
                java.lang.reflect.Method setDefaultExecutor = cls.getMethod("setDefaultExecutor", Executor.class);
                setDefaultExecutor.invoke(cls, objectExecutor);
            } catch (Throwable e) {

            }
        }
    }

    /**
     * 在发生RejectedExecution时获取AsyncTask中任务类的名称，便于检查哪些任务被拒绝、哪些任务在队列里、哪些任务存在死循环问题；
     *
     * @param r
     * @param executor
     * @return
     */
    public static String getAsyncTaskNamesOnRejectedExecution(Runnable r, ThreadPoolExecutor executor, AsynctaskRejectedExecutionHandler handler) {
        StringBuilder result = new StringBuilder();
        result.append("r.OuterClass = " + getOuterClass(r));
        result.append(", queue.runnable.OuterClasses = [");
        BlockingQueue<Runnable> queue = executor.getQueue();
        boolean isFirst = false;
        for (Runnable task : queue) {
            if (!isFirst) {
                isFirst = true;
            } else {
                result.append(", ");
            }
            Object outerClass = getOuterClass(task);
            result.append(outerClass);
            result.append(";extra:").append(handler != null ? handler.getExtraInfo(outerClass) : "");
        }
        result.append("]");
        return result.toString();
    }

    /**
     * 使用fragmentTransaction.commit()而非fragmentTransaction.commitAllowingStateLoss时会可能发生崩溃；
     * <p/>
     * 解决崩溃：java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * http://blog.csdn.net/edisonchang/article/details/49873669
     * http://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception-can-not-perform-this-action-after-onsa
     * <p/>
     * java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
     * at android.app.FragmentManagerImpl.checkStateLoss(FragmentManager.java:1318)
     * at android.app.FragmentManagerImpl.popBackStackImmediate(FragmentManager.java:488)
     * at android.app.Activity.onBackPressed(Activity.java:2181)
     * at com.qihoo.appstore.home.SplashActivity.onBackPressed(AppStore:248)
     * at android.app.Activity.onKeyUp(Activity.java:2159)
     * at android.view.KeyEvent.dispatch(KeyEvent.java:2667)
     * at android.app.Activity.dispatchKeyEvent(Activity.java:2389)
     * at com.android.internal.policy.impl.PhoneWindow$DecorView.dispatchKeyEvent(PhoneWindow.java:1867)
     * at android.view.ViewRootImpl.deliverKeyEventPostIme(ViewRootImpl.java:3757)
     * at android.view.ViewRootImpl.handleImeFinishedEvent(ViewRootImpl.java:3707)
     * at android.view.ViewRootImpl$ViewRootHandler.handleMessage(ViewRootImpl.java:2861)
     * at android.os.Handler.dispatchMessage(Handler.java:99)
     * at android.os.Looper.loop(Looper.java:137)
     * at android.app.ActivityThread.main(ActivityThread.java:5090)
     * at java.lang.reflect.Method.invokeNative(Native Method)
     * at java.lang.reflect.Method.invoke(Method.java:511)
     * at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:793)
     * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:560)
     * at dalvik.system.NativeStart.main(Native Method)
     *
     * @param activity android.app.Activity或者android.support.v4.app.FragmentActivity
     */
    public static void fixdActionAfterOnSaveInstanceState(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Object mFragments = ReflectUtils.getFieldValue(activity, "mFragments");
            ReflectUtils.invokeMethod(mFragments, "noteStateNotSaved", null);
        }
    }

    public static String getTopActivity(Context context) {
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
            ActivityManager.RunningTaskInfo task = tasks.get(0);
            return task.baseActivity.getClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过反射销毁所有Activity；
     * <p>
     * 使用场景：Avtivity所在进程崩溃时，5.0以上系统会显示当前Activity的前一个Activity并且卡住点击界面没反应；
     */
    public static void finishAllActivity() {
        finishAllActivity(null);
    }

    /**
     * 通过反射获取系统Activity栈中的所有存在的Activity；
     * 注意：需要在UI线程调用，否则会有数据同步问题；
     *
     * @return
     */
    public static List<Activity> getAllActivity() {
        List<Activity> activities = new ArrayList<>();
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activityMap = (Map) activitiesField.get(activityThread);
            if (activityMap != null) {
                for (Object activityRecord : activityMap.values()) {
                    Class activityRecordClass = activityRecord.getClass();
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    activities.add(activity);
                }
            }
        } catch (Throwable e) {
            //CrashHandler.getInstance().tryCatch(e, "getAllActivity");
        }
        return activities;
    }

    public static void finishAllActivity(Set<Activity> excludeActivity) {
        try {
            List<Activity> activities = getAllActivity();
            for (Activity activity : activities) {
                if (excludeActivity != null && excludeActivity.contains(activity))
                    continue;

                activity.moveTaskToBack(true);
                activity.finish();

            }
        } catch (Throwable e) {
            //CrashHandler.getInstance().tryCatch(e, "finishAllActivity");
        }
    }

    public static boolean isActivitiesEmpty() {
        try {
            List<Activity> activities = getAllActivity();

            return activities == null || activities.isEmpty();
        } catch (Throwable e) {
            // CrashHandler.getInstance().tryCatch(e, "isActivitiesEmpty");
        }
        return false;
    }

    public static boolean isFinish(Context context) {
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            return ((Activity) context).isFinishing() ||
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && ((Activity) context).isDestroyed();
        }
        return false;
    }

    /**
     * 使用继承基类的方式解决“Can not perform this action after onSaveInstanceState”崩溃；
     */
    public static class FixedActionAfterOnSaveInstanceStateActivity extends Activity {
        @Override
        public void onBackPressed() {
            fixdActionAfterOnSaveInstanceState(this);
            try {
                super.onBackPressed();
            } catch (IllegalStateException e) {
//                java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
//                at android.app.FragmentManagerImpl.checkStateLoss(FragmentManager.java:1318)
//                at android.app.FragmentManagerImpl.popBackStackImmediate(FragmentManager.java:488)
//                at android.app.Activity.onBackPressed(Activity.java:2267)
//                at android.app.Activity.onKeyUp(Activity.java:2245)
//                at android.view.KeyEvent.dispatch(KeyEvent.java:2633)
//                at android.app.Activity.dispatchKeyEvent(Activity.java:2475)
//                at com.android.internal.policy.impl.PhoneWindow$DecorView.dispatchKeyEvent(PhoneWindow.java:1952)
//                at android.view.ViewRootImpl.deliverKeyEventPostIme(ViewRootImpl.java:3794)
//                at android.view.ViewRootImpl.deliverKeyEvent(ViewRootImpl.java:3716)
//                at android.view.ViewRootImpl.deliverInputEvent(ViewRootImpl.java:3248)
//                at android.view.ViewRootImpl.doProcessInputEvents(ViewRootImpl.java:4385)
//                at android.view.ViewRootImpl.enqueueInputEvent(ViewRootImpl.java:4364)
//                at android.view.ViewRootImpl$WindowInputEventReceiver.onInputEvent(ViewRootImpl.java:4456)
//                at android.view.InputEventReceiver.dispatchInputEvent(InputEventReceiver.java:179)
//                at android.os.MessageQueue.nativePollOnce(Native Method)
//                at android.os.MessageQueue.next(MessageQueue.java:125)
//                at android.os.Looper.loop(Looper.java:124)
//                at android.app.ActivityThread.main(ActivityThread.java:5071)
//                at java.lang.reflect.Method.invokeNative(Native Method)
//                at java.lang.reflect.Method.invoke(Method.java:511)
//                at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:812)
//                at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:579)
//                at dalvik.system.NativeStart.main(Native Method)
                finish();
            }
        }
    }

   /* public static String getTopPkgName(Context context) {
        String topPackageName;
        if (Build.VERSION.SDK_INT > 20) {
            topPackageName = getTopPkgNameByPid(context);
            if (TextUtils.isEmpty(topPackageName)) {
                topPackageName = getTopPkgNameByActivityManager(context);
            }
        } else {
            topPackageName = getTopPkgNameByActivityManager(context);
            if (TextUtils.isEmpty(topPackageName)) {
                topPackageName = getTopPkgNameByPid(context);
            }
        }
        return topPackageName;
    }*/

    //针对5.0以上通过读proc下文件来判断前台app
   /* public static String getTopPkgNameByPid(Context ctx) {
        String foregroundName = null;
        if (ForegroundApp.lastForegroundAppName != null) {
            String pidFileName = Integer.toString(ForegroundApp.lastForegroundAppPid);
            File lastAppScoreFile = new File("/proc/" + pidFileName + "/oom_score");
            if (lastAppScoreFile.exists()) {
                try {
                    String scoreStr = ProcFile.readFile("/proc/" + pidFileName + "/oom_score");
                    int nowscore = ConvertUtils.string2Int(scoreStr);
                    String oom_score_adj = ProcFile.readFile("/proc/" + pidFileName + "/oom_score_adj");
                    int score_adj = ConvertUtils.string2Int(oom_score_adj);
                    if (nowscore == ForegroundApp.lastForegroundAppScore && score_adj == 0) {
                        foregroundName = ForegroundApp.lastForegroundAppName;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (TextUtils.isEmpty(foregroundName)) {
            foregroundName = getForegroundAppbyProcFile(ctx);
        }
        return foregroundName;
    }*/

    private static String getTopPkgNameByActivityManager(Context context) {

        if (context == null) {
            return null;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        String strTopPName = null;
        if (Build.VERSION.SDK_INT > 20) {
            final List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
            if (processInfos != null && processInfos.size() > 5) {
                for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        if (ReflectUtils.getIntField(processInfo, "flags") == 4 && processInfo.pkgList.length == 1) {
                            strTopPName = processInfo.pkgList[0];
                            break;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> runningTaskInfos = null;
            try {
                runningTaskInfos = am.getRunningTasks(1);
            } catch (NullPointerException e) {
//                vivo vivo X3t 4.2.1(17)、Huawei HUAWEI G750-T01 4.2.2(17)、OPPO R8207 4.4.4(19)
//                java.lang.NullPointerException
//                at android.app.ActivityManager.getRunningTasks(ActivityManager.java:769)
//                at android.app.ActivityManager.getRunningTasks(ActivityManager.java:805)
//                at com.qihoo.utils.AndroidUtilsCompat.getTopPkgName(AppStore:311)
//                at com.qihoo360.mobilesafe.util.OSUtils.isLauncherTop(AppStore:620)
//                at com.qihoo.core.CoreService$CheckAppUpdateTask.a(AppStore:293)
//                at com.qihoo.core.CoreService$CheckAppUpdateTask.doInBackground(AppStore:284)
//                at android.os.AsyncTask$2.call(AsyncTask.java:288)
//                at java.util.concurrent.FutureTask.run(FutureTask.java:237)

            }
            if (runningTaskInfos != null && !runningTaskInfos.isEmpty()) {
                ComponentName componentName = runningTaskInfos.get(0).topActivity;
                if (componentName != null && componentName.getPackageName() != null) {
                    strTopPName = componentName.getPackageName();
                }
            }
        }
        return strTopPName;
    }



    public static long parseLong(String string) {
        if (TextUtils.isEmpty(string)) {
            return 0;
        }

        try {
            return Long.parseLong(string);
        } catch (NumberFormatException e) {

        }

        return 0;
    }

    public static Intent getLaunchIntentForPackageCompat(Context context, String pkgName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
        if (intent == null) {
            intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage(pkgName);
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);
            if (list != null) {
                for (ResolveInfo info : list) {
                    ActivityInfo activityInfo = info.activityInfo;
                    if (activityInfo != null && pkgName.equals(activityInfo.packageName)) {
                        intent.setComponent(new ComponentName(pkgName, activityInfo.name));
                        break;
                    }
                }
            }
        }
        return intent;
    }

    /**
     * 提高AlarmManager执行的准确性
     * 参考：https://developer.android.com/training/monitoring-device-state/doze-standby.html
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void alarmManagerSet(Context context, int type, long triggerAtMillis, PendingIntent pendingIntent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.set(type, triggerAtMillis, pendingIntent);

        } else if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            try {
                alarmManager.setExact(type, triggerAtMillis, pendingIntent);

            } catch (NoSuchMethodError e) { // AOSP on Flo型号手机sdk版本号判断有问题，添加保护
                alarmManager.set(type, triggerAtMillis, pendingIntent);

            }
        } else {
            try { // Android 6.0开始有Doze模式（低电耗模式），如果需要设置在该模式下触发的闹铃，需要使用 setAndAllowWhileIdle() 或 setExactAndAllowWhileIdle()；
                ReflectUtils.invokeMethod(
                        alarmManager,
                        "setExactAndAllowWhileIdle",
                        new Class[]{int.class, long.class, PendingIntent.class},
                        type, triggerAtMillis, pendingIntent);

            } catch (Throwable e) {
                alarmManager.setExact(type, triggerAtMillis, pendingIntent);

            }
        }
    }
}
