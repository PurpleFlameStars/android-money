package com.cashLoan.money.splash;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;

import com.cashLoan.money.JumpHelper;
import com.cashLoan.money.R;
import com.cashLoan.money.base.activity.BunBaseActivity;
import com.cashLoan.money.splash.listener.FragmentEvent;
import com.cashLoan.money.utils.MoneyConfig;
import com.cashLoan.money.utils.statusbar.StatusBarUtil;
import com.dzfd.gids.baselibs.stat.StatEntity;
import com.dzfd.gids.baselibs.stat.StatHelper;
import com.dzfd.gids.baselibs.utils.SPUtils;

public class SplashActivity extends BunBaseActivity implements FragmentEvent {
    private static final String TAG = "SplashActivity";
    private static boolean firstEnter = true; // 是否首次进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (mNeedFinish) {
//            JumpHelper.jumpMainActivity(this);
//            return;
//        }
//        AdpterCutout();
//        mScrollbackEnable = true;
        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase("android.intent.action.MAIN")) {
                StatHelper.onEvent("launch", new StatEntity("", "active"));
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public int getFragmentcontainerViewId() {
        return R.id.common_content_layout;
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public Fragment getMainFragment() {
        return !firstEnter ? new GuideFragment() : new SplashFragment();
    }

    @Override
    public boolean isShowImmerseLayout() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void OnFragmentFinished() {
        firstEnter=SPUtils.getBoolean(MoneyConfig.CONFIG_FILE, MoneyConfig.KEY_SHOW_INTEREST_GUIDE, false);
        if (!firstEnter){
            startActivity(new Intent(this,SplashActivity.class));
            finish();
        }else {
            JumpHelper.jumpMainActivity(getIntent(), SplashActivity.this);
            finish();
        }

    }



    @Override
    public void onBackPressed() {
        OnFragmentFinished();
    }
}
