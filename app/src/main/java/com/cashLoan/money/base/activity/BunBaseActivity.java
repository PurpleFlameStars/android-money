package com.cashLoan.money.base.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cashLoan.money.R;
import com.cashLoan.money.language.utils.LocalManageUtil;
import com.dzfd.gids.baselibs.UI.widgets.LoadingDialog;
import com.dzfd.gids.baselibs.activity.BunActivity;
import com.dzfd.gids.baselibs.keyborad.KeyboardHeightObserver;
import com.dzfd.gids.baselibs.keyborad.KeyboardHeightProvider;
import com.dzfd.gids.baselibs.stat.StatEntity;
import com.dzfd.gids.baselibs.stat.StatHelper;


public abstract class BunBaseActivity extends AppCompatActivity implements
        KeyboardHeightObserver {

    private LoadingDialog loadingDialog;
    private long _mStatBegin;
    private KeyboardHeightProvider keyboardHeightProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (mNeedFinish) {
//            StatHelper.onEvent("active", new StatEntity("", "", "onCreate error"));
//            return;
//        }

        //打开有webview页面之后会导致系统内语言使用系统语言设置的bug，在所有activity setContentView之前重新设置语言
        LocalManageUtil.setLocal(this);

        int layoutid = getActivityLayoutId();
        if (layoutid > 0) {
            setContentView(layoutid);
        }
//        boolean showImmerLayout = isShowImmerseLayout();
//        if (showImmerLayout) {
//            setImmerseLayout();
//        }
        Fragment maininst = getMainFragment();
        if (maininst != null) {
            addMainFragment(maininst);
        }
        StatHelper.onEvent("active", new StatEntity());

    }

    @Override
    protected void onDestroy() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
            loadingDialog = null;
        }
        if (keyboardHeightProvider != null) {
            keyboardHeightProvider.setKeyboardHeightObserver(null);
            keyboardHeightProvider.close();
            keyboardHeightProvider = null;
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocalManageUtil.onConfigurationChanged(this);
    }


    protected void HideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiflags = decorView.getSystemUiVisibility();
        uiflags |= (View.SYSTEM_UI_FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(uiflags);
    }

    protected void ShowStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiflags = decorView.getSystemUiVisibility();
        uiflags &= ~(View.SYSTEM_UI_FLAG_FULLSCREEN);
        decorView.setSystemUiVisibility(uiflags);
    }

    /**
     * 在有navigation的时候某些页面需要及时的隐藏naviBar
     */
    protected void hideSystemUIListener() {
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                View view = getWindow().getDecorView();
                view.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
        });
    }

    protected void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void addMainFragment(Fragment fragment) {
        int viewid = getFragmentcontainerViewId();
        getSupportFragmentManager().beginTransaction().add(viewid, fragment).commit();
    }

    public void ReplaceFragment(Fragment fragment) {
        int viewid = getFragmentcontainerViewId();
        getSupportFragmentManager().beginTransaction().replace(viewid, fragment).commitAllowingStateLoss();
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(getFragmentcontainerViewId());
    }

    public void AdpterSystemTheme() {
        if (setAndroidNativeLightStatusBar(true)) {
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View immersiveView = findViewById(R.id.immersive_view);
            if (immersiveView != null) {
                immersiveView.setBackgroundColor(getResources().getColor(R.color.main_Immerse_color));
            }
        }
    }

    protected void setKeyboardHeightProvider(KeyboardHeightObserver observer) {
        if (observer == null || keyboardHeightProvider != null) {
            return;
        }
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        keyboardHeightProvider.setKeyboardHeightObserver(observer);
        View decorview = getWindow().getDecorView();
        decorview.post(() -> {
            if (keyboardHeightProvider != null) {
                keyboardHeightProvider.start();
            }
        });
    }

    public boolean setAndroidNativeLightStatusBar(boolean lightTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            if (lightTheme) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            return true;
        }
        return false;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && imm.isActive()) {
            View focusView = getCurrentFocus();
            if (focusView != null) {
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
            }
        }
    }

    abstract public int getFragmentcontainerViewId();

    abstract public int getActivityLayoutId();

    abstract public Fragment getMainFragment();

    abstract public boolean isShowImmerseLayout();

    @Override
    protected void onResume() {
        super.onResume();
//        if (mNeedFinish) {
//            return;
//        }
        _mStatBegin = System.currentTimeMillis();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (_mStatBegin != 0) {
            long duration = System.currentTimeMillis() - _mStatBegin;
            _mStatBegin = 0;
            //BUNSTAT: zheng 2019/5/28 duration
            StatEntity entity = new StatEntity("", String.valueOf(duration), getClass().getSimpleName());
            StatHelper.onEvent("duration", entity);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


    public boolean HasPermisstion(String value) {
        if (TextUtils.isEmpty(value)) {
            return true;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(value);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }


    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }



    protected boolean AddCommentEditorView(View view) {
        return false;
    }
}
