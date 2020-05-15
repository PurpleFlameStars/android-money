package com.dzfd.gids.baselibs.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.dzfd.gids.baselibs.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;

/**
 * Created by zheng on 2018/12/13.
 */

public class Utils {

    private  static int videoScreenHeight=0;
    private static int screen_width=0;
    public static DisplayMetrics dm = null;

    public static DisplayMetrics getDisplayMetrics() {
        if (dm == null) {
            dm = ContextUtils.getAppContext().getResources().getDisplayMetrics();
        }
        return dm;
    }
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, context.getResources().getDisplayMetrics());
    }
    public static int dip2px(float dip) {
        final float scale = getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }
    public static int getColorRes(Resources resources, int id) {
        return resources.getColor(id);
    }
    public static int getVideoListHeight(Context context){
        if(videoScreenHeight >0){
            return videoScreenHeight;
        }
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
       videoScreenHeight = width * 9 / 16;
       return videoScreenHeight;
    }
    public static void runOnMainThread(Runnable action) {
        if (action != null) {
            if (Looper.getMainLooper() == Looper.myLooper()) {
                action.run();
            } else {
                new Handler(Looper.getMainLooper()).post(action);
            }
        }
    }
    public static int getScreenWidth(Context ctx) {
        if (screen_width == 0) {
            Display screenSize = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screen_width = screenSize.getWidth();
        }

        return screen_width;
    }
    public static void removeGlobalOnLayoutListener(ViewTreeObserver mViewTreeObserver, ViewTreeObserver.OnGlobalLayoutListener listener) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            mViewTreeObserver.removeGlobalOnLayoutListener(listener);
        } else {
            mViewTreeObserver.removeOnGlobalLayoutListener(listener);
        }
    }

    public static String formatDurition(int tm){
        if(tm<=0){
            return "00:00";
        }
        int sec=tm%60;
        int min=tm/60;
        return String.format("%02d:%02d",min,sec);
    }
    public static String formatTime(int second) {
        int seconds = second % 60;
        int minutes = (second / 60) % 60;
        int hours = second / 3600;
        return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
                seconds) : String.format("%02d:%02d", minutes, seconds);
    }
    public static String formatTime(Context cxt,long second){
        long millon=second*1000;
        String datestr= DateUtils.formatDateTime(cxt,
                millon, DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_ABBREV_ALL);
        datestr=datestr.toUpperCase();
        return datestr;
    }
    public static String GetTimeOffset(long ts){
        long curtime= System.currentTimeMillis();
        curtime/=1000;
        long offset=curtime-ts;
        if(offset<60){
            return "刚刚";
        }
        offset /=60;
        if(offset<60){
            return String.valueOf(offset)+"分钟前";
        }
        offset /=60;
        if(offset<24){
            return String.valueOf(offset)+"小时前";
        }
        offset /=24;
        if(offset<30){
            return String.valueOf(offset)+"天前";
        }
        offset /=30;
        if(offset<12){
            return String.valueOf(offset)+"月前";
        }
        offset /=12;
        return String.valueOf(offset)+"年前";

    }
    /** 网络是否已经连接 */
    public static boolean isConnected(Context c) {
        ConnectivityManager cm = null;
        try {
            cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        } catch (Exception ex) {
        }

        if (cm != null) {
            NetworkInfo[] infos = null;
            try {
                infos = cm.getAllNetworkInfo();
            } catch (Exception e) {

            }
            if (infos != null) {
                for (NetworkInfo ni : infos) {
                    if (null != ni && ni.isConnected()) {
                        return true;
                    }

                }
            }
        }
        return false;
    }
    /** WiFi 是否已经连接 */
    public static boolean isWifiConnected(Context c) {
        ConnectivityManager connecManager = (ConnectivityManager) c.getApplicationContext().getSystemService(
                Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        try {
            networkInfo = connecManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        } catch (Exception ex) {
            //java.lang.NullPointerException
            //   at android.os.Parcel.readException(Parcel.java:1333)
            //   at android.os.Parcel.readException(Parcel.java:1281)
            //   at android.net.IConnectivityManager$Stub$Proxy.getNetworkInfo(IConnectivityManager.java:830)
            //   at android.net.ConnectivityManager.getNetworkInfo(ConnectivityManager.java:387)
        }
        if (networkInfo != null) {
            return networkInfo.isConnected();
        } else {
            return false;
        }
    }
    public static Drawable getDrawable(Resources resources, int id) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            return resources.getDrawable(id);
        } else {
            return resources.getDrawable(id, null);
        }
    }
    public static String formatSize(long number) {
        if (number <= 1024) {
            return number + "B";
        } else if (number <= 10 * 1024) {
            return number / 1024 + "KB";
        }

        float result = number * 1.0f / (1024.0f * 1024.0f);
        String suffix = "M";

        if (result > 900) {
            suffix = "G";
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.2f", result);
        } else if (result < 100) {
            value = String.format("%.2f", result);
        } else {
            value = String.format("%.2f", result);
        }
        return value + suffix;
    }
    public static String formatCount(long number) {
        if (number <= 1000) {
            return String.valueOf(number);
        } else if(number<1000*1000){
            return number / 1000 + "K";
        }else if(number<(1000*1000*1000)){
            return number/(1000*1000)+"M";
        }else{
            return number/(1000*1000*1000)+"B";
        }
    }
    public static String formatCountfloat(long number) {
        float fnumber=number;
        if (number <= 1000) {
            return String.valueOf(number);
        } else if(number<1000*1000){
            return fnumber / 1000 + "K";
        }else if(number<(1000*1000*1000)){
            return fnumber/(1000*1000)+"M";
        }else{
            return fnumber/(1000*1000*1000)+"B";
        }
    }
    public static String formatdotNumber(int number){
        StringBuilder builder=new StringBuilder();
        int insertdot=0;
        while (number>0){
            int low=(number%10);
            builder.insert(0,String.valueOf(low));
            number=number/10;
            insertdot++;
            if((insertdot%3 == 0) && (number>0)){
                builder.insert(0,",");
            }
        }
        return builder.toString();
    }
    public static List<String> formatTimeToList(long tsInsecond){
        long now=System.currentTimeMillis()/1000;
        now =tsInsecond-now;
        List<String> retlist=new ArrayList<>();
        for(int index=0;index<4;index++){
            retlist.add("0");
        }
        if(now<=0){
            return retlist;
        }
        int second=(int) (now%60);
        retlist.set(3,String.valueOf(second));
        now /=60;
        if(now<=0){
            return retlist;
        }
        int min=(int) (now%60);
        retlist.set(2,String.valueOf(min));
        now /=60;
        if(now<=0){
            return retlist;
        }
        int hour=(int)now%24;
        retlist.set(1,String.valueOf(hour));
        retlist.set(0,String.valueOf(now/24));
        return retlist;

    }
    private static int statusBarHeight;

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
    public static boolean isSDCardMounted() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

    }
    public static long getSDCardAvalableSize() {
        if (isSDCardMounted()) {
            return FileUtils.getAvailableBytes(new File(FileUtils.getSDCardPath()));
        }
        return 0;
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
    public static boolean isApkInstalled(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        /*if (packageName.equals(context.getPackageName())) {
            return true;
        }*/
        boolean result = false;

        try {
            result = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_DISABLED_COMPONENTS) != null;
        } catch (PackageManager.NameNotFoundException | RuntimeException e) {

        }

        return result;
    }
    public static String getSystemProperty(String propertyKey) {
        String propertyValue = null;
        try {
            Object obj = ReflectUtil.invokeStaticMethod("android.os.SystemProperties", "get", new Class[]{String.class}, new Object[] {propertyKey });
            if (obj != null && obj instanceof String) {
                propertyValue = (String) obj;
            }
        } catch (Exception e) {
            //ignore
        }

        return propertyValue;
    }
    public static void setTextViewText(TextView view, String str){
        if(view!=null && !TextUtils.isEmpty(str)){
            view.setText(str);
        }
    }
    public static void setTextViewText(TextView view, int strid){
        if(view!=null && strid!=0){
            view.setText(strid);
        }
    }
    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    public static void hideSoftInput(IBinder token) {
        Context cxt=ContextUtils.getAppContext();

        if (token != null) {
            InputMethodManager im = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (im != null) {
                im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 隐藏软键盘
     * @param view
     */
    public static void hideSoftKeyboard(View view) {
        Context cxt=ContextUtils.getAppContext();
        if (view == null || cxt == null)
            return;

        ((InputMethodManager) cxt.getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }
    /**
     * 显示软键盘
     */
    public static void showSoftKeyboard(){
        Context cxt=ContextUtils.getAppContext();
        if(cxt == null){
            return;
        }
        ((InputMethodManager) cxt.getSystemService(
                Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);

    }
    /** 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
      * @param v
     * @param event
     * @return
     * */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null /*&& (v instanceof EditText)*/) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }
    public static boolean PointInView(View target,MotionEvent event){
        if(target==null){
            return false;
        }
        int[] l = {0, 0};
        target.getLocationInWindow(l);
        int left=l[0],top=l[1];
        Rect rt=new Rect(left,top,left+target.getWidth(),top+target.getHeight());
        int x=(int) event.getRawX();
        int y=(int)event.getRawY();
        boolean inview=rt.contains(x,y);
        return  inview;
    }
    public static void ToastDownLoadResult(Context cxt,boolean result){
        if (cxt == null) {
            return;
        }
        int nresid=result?R.string.save_media_sucess:R.string.save_media_faild;
        BunToast.showLong(cxt, nresid);
    }
//    AppOpsManager.OPSTR_READ_CONTACTS,Manifest.permission.READ_CONTACTS
    
    public static boolean checkPermission(Context context,String permission,String op) {

        boolean have;
        if (Build.VERSION.SDK_INT >= 23) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int checkOp = appOpsManager.checkOp(op, android.os.Process.myUid(), context.getPackageName());
            have=(checkOp == AppOpsManager.MODE_ALLOWED);
        } else {
            have = (ActivityCompat.checkSelfPermission(context, permission)== PackageManager.PERMISSION_GRANTED);
        }
        return have;
    }



}
