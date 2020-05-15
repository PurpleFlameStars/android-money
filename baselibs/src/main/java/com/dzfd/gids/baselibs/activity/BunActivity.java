package com.dzfd.gids.baselibs.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.immersive.ImmersiveUtils;
import com.dzfd.gids.baselibs.UI.immersive.ImmersiveView;
import com.dzfd.gids.baselibs.UI.widgets.ScrollbackLayout;
import com.dzfd.gids.baselibs.utils.AndroidUtilsCompat;
import com.dzfd.gids.baselibs.utils.BunToast;
import com.dzfd.gids.baselibs.utils.ScrollbackUtils;
import com.dzfd.gids.baselibs.utils.thread.MainThreadExecutor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by zheng on 2018/12/24.
 */

public class BunActivity extends AppCompatActivity implements ScrollbackLayout.OnScrollbackListener {
    protected boolean mScrollbackEnable = false;
    protected ScrollbackLayout mScrollbackLayout;
    protected boolean mNeedFinish;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        try {
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
                fixOrientation();
            }
            super.onCreate(savedInstanceState);
        } catch (Throwable e) {
            mNeedFinish = true;
            return;
        }
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
        } catch (Exception e) {
            callUpActivity();
        }

        if (mNeedFinish) {
            finish();
        }
    }

    @Override
    public void onScrollEnd(boolean isClose) {
        if (!isClose) {
            ScrollbackUtils.convertActivityFromTranslucent(this);
        }
    }

    @Override
    public void onStartScroll() {
        ScrollbackUtils.convertActivityToTranslucent(this);
    }

    protected void convertActivityFromTranslucent() {
        if (!mScrollbackEnable) {
            return;
        }
        // 设置成不透明会导致Activity切换动画失效，延时500ms执行
        MainThreadExecutor.getGlobalExecutor().postDelayed(new Runnable() {
            @Override
            public void run() {
                ScrollbackUtils.convertActivityFromTranslucent(BunActivity.this);
            }
        }, 500);
    }

    private void setScrollBackEnable(boolean enable) {
        mScrollbackEnable = enable;
        if (mScrollbackEnable && mScrollbackLayout == null) {
            mScrollbackLayout = (ScrollbackLayout) LayoutInflater.from(this).inflate(R.layout.layout_scrollback, null);
            mScrollbackLayout.attachToActivity(this);
        }

        if (mScrollbackLayout != null) {
            mScrollbackLayout.setScrollbackEnable(mScrollbackEnable);
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (getScrollbackEndable()) {
            setEnableScrollBack(true);
        }
        convertActivityFromTranslucent();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        if (getScrollbackEndable()) {
            setEnableScrollBack(true);
        }
        convertActivityFromTranslucent();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (getScrollbackEndable()) {
            setEnableScrollBack(true);
        }
        convertActivityFromTranslucent();
    }

    public void addScrollBackExcludeView(View v) {
        if (mScrollbackLayout != null)
            mScrollbackLayout.addExcludeView(v);
    }

    public void removeScrollBackExcludeView(View v) {
        if (mScrollbackLayout != null)
            mScrollbackLayout.removeExcludeView(v);
    }

    protected boolean getScrollbackEndable() {
        return true;
    }

    protected void setEnableScrollBack(boolean enable) {
        setScrollBackEnable(enable);
        if (mScrollbackLayout != null && enable) {
            mScrollbackLayout.setOnScrollbackListener(this);
        }
    }

    protected void showShortToast(String text) {
        if (!AndroidUtilsCompat.isFinish(this))
            BunToast.showShort(this, text);
    }

    protected void showShortToast(int resId) {
        if (!AndroidUtilsCompat.isFinish(this))
            BunToast.showShort(this, resId);
    }

    protected void setImmerseLayout() {
        if (!ImmersiveUtils.isSupported()) {
            return;
        }
        ImmersiveUtils.setImmerseLayout(this);
        showImmerseLayout(true);
    }

    protected void showImmerseLayout(boolean show) {
        final View v = getWindow().getDecorView().findViewWithTag(ImmersiveView.TAG);
        if (show)
            ImmersiveUtils.showView(v);
        else
            ImmersiveUtils.dismissView(v);
    }

    /**
     * 在android8.0上的手机，如果工程中targetVersion不是26，且Activity设置成了透明，则manifest不能指定该Activity的方向，
     * 否则回在OnCreate中抛出“Only fullscreen activities can request orientation”异常
     * <p>
     * 解决方案：如果发现是8.0手机，且Activity设置了透明属性，在调用OnCreate前去掉方向锁定，并重写setRequestedOrientation();
     *
     * @return
     */
    private boolean fixOrientation() {
        try {
            Field field = Activity.class.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            ActivityInfo o = (ActivityInfo) field.get(this);
            o.screenOrientation = -1;
            field.setAccessible(false);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isTranslucentOrFloating() {
        boolean isTranslucentOrFloating = false;
        try {
            int[] styleableRes = (int[]) Class.forName("com.android.internal.R$styleable").getField("Window").get(null);
            final TypedArray ta = obtainStyledAttributes(styleableRes);
            Method m = ActivityInfo.class.getMethod("isTranslucentOrFloating", TypedArray.class);
            m.setAccessible(true);
            isTranslucentOrFloating = (boolean) m.invoke(null, ta);
            m.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating;
    }


    /**
     * 4 Caused by:
     * 5 java.lang.IllegalArgumentException:
     * 6 android.os.Parcel.readException(Parcel.java:1687)
     * 7 android.os.Parcel.readException(Parcel.java:1636)
     * 8 android.app.ActivityManagerProxy.isTopOfTask(ActivityManagerNative.java:5577)
     * 9 android.app.Activity.isTopOfTask(Activity.java:5963)
     * 10 android.app.Activity.onResume(Activity.java:1254)
     * 11 android.support.v4.app.FragmentActivity.onResume(FragmentActivity.java:514)
     * 12 com.GoLemon.Base.activity.BunBaseActivity.onResume(BunBaseActivity.java:295)
     * 13 com.GoLemon.Base.activity.VideoActivity.onResume(VideoActivity.java:110)
     * 14 com.GoLemon.MainActivity.onResume(MainActivity.java:477)
     */
    private void callUpActivity() {
        try {
            Class superActivity = Activity.class;
            Field callField = superActivity.getDeclaredField("mCalled");
            callField.setAccessible(true);
            callField.setBoolean(this, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()) {
            return;
        }
        super.setRequestedOrientation(requestedOrientation);
    }

    @TargetApi(28)
    protected void AdpterCutout() {
        if (Build.VERSION.SDK_INT < 28) {
            return;
        }
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        try {
            Class<?> clazz = Class.forName("android.view.WindowManager$LayoutParams");
            Field laout = clazz.getField("layoutInDisplayCutoutMode");

            laout.setInt(lp, 1);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        getWindow().setAttributes(lp);
        hideSystemUI();
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void finish() {
        ScrollbackUtils.convertActivityToTranslucent(this);
        super.finish();
    }
}
