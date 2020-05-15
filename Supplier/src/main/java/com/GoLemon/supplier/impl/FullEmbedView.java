package com.GoLemon.supplier.impl;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.GoLemon.supplier.player.PlayerView;

public class FullEmbedView extends RelativeLayout {

    public static final String TAG = "full_embed_view_tag";

    private PlayerView playerView;


    public FullEmbedView(Context context, PlayerView playerView) {
        this(context, null, 0);
        this.playerView = playerView;
    }

    public FullEmbedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullEmbedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //虚拟按键穿透的黑屏的问题
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    private void init() {
        setVisibility(View.GONE);
    }

    public boolean onConfigurationChanged(PlayerView player, boolean portrait) {

        try {
            if (portrait) {
                setVisibility(View.GONE);
                removeAllViews();
                int showFlags=0;
                int version=android.os.Build.VERSION.SDK_INT;
                if (version >= Build.VERSION_CODES.KITKAT) {
                    showFlags= View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                }else if ( version>= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    showFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            //     | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                }
                if(showFlags>0){
                    setSystemUiVisibility(showFlags);
                }
                player.ShowNormalModel(this);

            } else {

                player.ShowFullModel(this);
                setVisibility(View.VISIBLE);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    int hideFlags = View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            ;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        hideFlags = hideFlags | View.SYSTEM_UI_FLAG_IMMERSIVE;
                    }

                    setSystemUiVisibility(hideFlags);
                }

                return true;
            }
        } catch (Throwable e) {
        }
        return false;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean portrait = newConfig.orientation == Configuration.ORIENTATION_PORTRAIT;
        doHandleConfigurationChanged(portrait);
    }

    public boolean doHandleConfigurationChanged(boolean portrait) {
        if (portrait) {
            this.playerView.doOnConfigurationChanged(true, false);
            return true;
        }

        return true;
    }

    @Override
    public void removeAllViews() {
        super.removeAllViews();
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
    }
}
