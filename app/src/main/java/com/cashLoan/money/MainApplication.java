

package com.cashLoan.money;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.cashLoan.money.language.utils.LocalManageUtil;
import com.dzfd.gids.baselibs.helper.ProcessHelper;
import com.dzfd.gids.baselibs.utils.ContextUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.flurry.android.FlurryAgent;

import java.util.Map;



public class MainApplication extends Application implements ViewModelStoreOwner {

    //TODO tip：可借助 Application 来管理一个应用级 的 SharedViewModel，
    // 实现全应用范围内的 生命周期安全 且 事件源可追溯的 视图控制器 事件通知。

    private boolean isMainProcess = false;

    private ViewModelStore mAppViewModelStore;
    private ViewModelProvider.Factory mFactory;


    private static MainApplication mInstance;

    public static String CHANNEL_NAME = "apply";//通知渠道名称

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        LocalManageUtil.saveSystemCurrentLanguage(base);
        ProcessHelper.initProcessInfo(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isMainProcess = ProcessHelper.isUiProccess();
        mInstance = this;
        ContextUtils.init(this);
        mAppViewModelStore = new ViewModelStore();
        if (isMainProcess) {
            Fresco.initialize(this);
        }

        initNotification();
    }

    private void initNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = CHANNEL_NAME;
            String channelName = "应用消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            /*channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);*/
        }
    }

    private void createNotificationChannel(String channelId, String channelName, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalManageUtil.onConfigurationChanged(getApplicationContext());
    }



    public static MainApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }

    public ViewModelProvider getAppViewModelProvider(Activity activity) {
        return new ViewModelProvider((MainApplication) activity.getApplicationContext(),
                ((MainApplication) activity.getApplicationContext()).getAppFactory(activity));
    }

    private ViewModelProvider.Factory getAppFactory(Activity activity) {
        Application application = checkApplication(activity);
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return mFactory;
    }

    private Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }
}

