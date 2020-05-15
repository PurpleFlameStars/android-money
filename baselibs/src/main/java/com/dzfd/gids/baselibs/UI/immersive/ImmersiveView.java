package com.dzfd.gids.baselibs.UI.immersive;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.dzfd.gids.baselibs.utils.DensityUtils;

public class ImmersiveView extends View {

    public static final String TAG = "common_immersive_tag";
    public static final String TAG_WRAPPER = "common_immersive_wrapper";

    private static int sStatusBarHeight = -1;

    public ImmersiveView(Context context) {
        this(context, null);
    }

    public ImmersiveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImmersiveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ImmersiveUtils.isSupported()) {
            setMeasuredDimension(widthMeasureSpec, getStatusBarHeight(getContext().getApplicationContext()));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight != -1) {
            return sStatusBarHeight;
        }
        sStatusBarHeight = ImmersiveUtils.getStatusBarHeight(context);
        if (sStatusBarHeight < 0) {
            sStatusBarHeight = DensityUtils.dip2px(25);
        }
        return sStatusBarHeight;
    }

}
