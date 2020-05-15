package com.cashLoan.money.splash.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.dzfd.gids.baselibs.utils.CutoutUtils;

public class SplashRootView extends FrameLayout {
    public static int CUTOUT_HEIGHT;

    public SplashRootView(Context context) {
        super(context);
    }

    public SplashRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        WindowInsets retinsets = super.onApplyWindowInsets(insets);
        CUTOUT_HEIGHT = CutoutUtils.getCutoutHight(retinsets);
        return retinsets;
    }
}
