package com.cashLoan.money;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;

import com.cashLoan.money.base.activity.BunBaseActivity;
import com.cashLoan.money.utils.statusbar.StatusBarUtil;

import ai.advance.liveness.lib.GuardianLivenessDetectionSDK;

public class MainActivity extends BunBaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        StatusBarUtil.setTranslucentStatus(this);
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN); //隐藏状态栏


        findViewById(R.id.main_ocr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              JumpHelper.jumpOcrActivity(MainActivity.this);
            }
        });

        findViewById(R.id.main_live).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!GuardianLivenessDetectionSDK.isSDKHandleCameraPermission()){
//                    GuardianLivenessDetectionSDK.letSDKHandleCameraPermission();
//                }else {
//                    JumpHelper.jumpLivenessActivity(MainActivity.this);
//                }
                JumpHelper.jumpLivenessActivity(MainActivity.this);
            }
        });

    }

    @Override
    public int getFragmentcontainerViewId() {
        return 0;
    }

    @Override
    public int getActivityLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public Fragment getMainFragment() {
        return null;
    }

    @Override
    public boolean isShowImmerseLayout() {
        return false;
    }

}
