package com.GoLemon.supplier.player;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.PowerManager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.GoLemon.supplier.impl.AnimalEventImpl;
import com.GoLemon.supplier.impl.FullEmbedView;
import com.GoLemon.supplier.impl.ScreenHelper;
import com.GoLemon.supplier.player.listener.PlayControlListener;
import com.GoLemon.supplier.player.listener.PlayerStateListener;
import com.GoLemon.supplier.player.listener.PlayerStateListenrPorxy;
import com.GoLemon.supplier.video.VideoItem;

/**
 * Created by zheng on 2018/12/27.
 */

public abstract class PlayerView extends RelativeLayout {
    protected  ViewGroup _PlayerRootView;
    protected ViewGroup _ParentView;
    private FullEmbedView fullEmbedView;
    protected  IPlayerController mCtrlView;
    protected VideoItem _videoData;

    protected int initWidth = 0;
    protected int initHeight = 0;
    protected boolean mIsVideoLandscape = true;//视频原始宽>高为true，宽小于高为false

    protected PlayerStateListenrPorxy StateListenerProxy=new PlayerStateListenrPorxy();
    protected PlayControlListener mCtrlListener;
    private PowerManager.WakeLock wakeLock;
    protected boolean fullCtrlChange;

    protected boolean portrait = true; //横竖屏


    public PlayerView(Context context) {
        super(context);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        setPortrait(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT);
        fullViewChanged(false);
    }

    public void ShowFullModel(ViewGroup fullview){
        ChangeShowModel(_PlayerRootView,fullview);

    }
    public void ShowNormalModel(ViewGroup fullview){
        ChangeShowModel(fullview,_PlayerRootView);
    }

    public void setPlayerRootView(ViewGroup parent){
        if(parent == null || parent==_ParentView){
            return;
        }
        _ParentView=parent;
        this._PlayerRootView =(ViewGroup) parent.getParent();
    }
    public PlayerView setPlayerWH(int width, int height) {

        this.initWidth = width;
        this.initHeight = height;
        return this;
    }
    public void setPlayerCtrl(IPlayerController controller) {
        if(mCtrlView!=null && mCtrlView==controller){
            return;
        }
        mCtrlView=controller;
    }
    public void setVideo(VideoItem item) {
        _videoData=item;
    }

    public boolean addPlayerStateListener(PlayerStateListener stateListener){

        return this.StateListenerProxy.addCallback(stateListener);
    }
    public void removeStateListener(PlayerStateListener stateListener){
        this.StateListenerProxy.removeCallback(stateListener);
    }
    public void removeStateListener(){
        this.StateListenerProxy.RemoveAllListener();
    }


    public void setPlayCtrlListener( PlayControlListener callback){
        mCtrlListener=callback;
    }
    public void screenOff() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            this.setKeepScreenOn(false);
        }else{
            if (wakeLock != null) {
                try {
                    wakeLock.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                wakeLock = null;
            }
        }
    }
    public void screenOn() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
            this.setKeepScreenOn(true);
        }else{
            Context mcxt=getContext();
            if (wakeLock != null || mcxt == null) {
                return;
            }

            PowerManager pm = (PowerManager) mcxt.getApplicationContext().getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, PlayerView.class.getName());
            if (wakeLock != null) {
                try {
                    wakeLock.acquire();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public void setPortrait(boolean portrait) {
        this.portrait = portrait;
    }
    public boolean isPortrait(){return this.portrait;}
    public boolean isLandscape() {
        return mIsVideoLandscape;
    }
    public void treatOrientation(int width, int height) {
        if (width != 0 && height != 0) {
            mIsVideoLandscape = width > height;
        }
    }

    private void ChangeShowModel(ViewGroup oldview,ViewGroup NewView){
        if(oldview!=null){
            oldview.removeAllViews();
        }
        if(NewView!=null){
            NewView.removeAllViews();
            if(_ParentView!=null){
                NewView.addView(_ParentView,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }
        }
    }
    public boolean onBackPressed() {
        Context mcxt=getContext();
        if (ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                || ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
            ((Activity) mcxt).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return true;
        } else if (!portrait) {
            setPortrait(true);
            fullViewChanged(false);
            return true;
        }
        return false;
    }
    public void doFullScreen() {
        Context mcxt=getContext();
        if (mIsVideoLandscape) {
            if (ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {// 转小屏
                ((Activity) mcxt).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setPortrait(true);
            } else {

                if (portrait) {
                    ((Activity) mcxt).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    setPortrait(false);
                } else {
                    setPortrait(true);
                    fullViewChanged(false);
                }
            }
        } else {
            if (ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || ScreenHelper.getScreenOrientation((Activity) mcxt) == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {// 转小屏
                ((Activity) mcxt).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                setPortrait(true);
            } else {
                setPortrait(!portrait);
                fullViewChanged(true);
            }
        }
    }

    private ViewGroup contentParent;

    private void fullViewChanged(boolean withAnimation) {
        Context mcxt=getContext();
        if (fullEmbedView == null) {
            View decorView = ((Activity) mcxt).getWindow().getDecorView();
            contentParent = (ViewGroup) decorView.findViewById(android.R.id.content);
            fullEmbedView = new FullEmbedView(mcxt, this);
            if (!withAnimation) {
                fullEmbedView.setBackgroundColor(Color.BLACK);
            }
            fullEmbedView.setVisibility(GONE);
            contentParent.addView(fullEmbedView);

            /*if (fullCtrlView != null) {
                View viewById = contentParent.findViewWithTag(PLAYER_FULL_CONTROL_TAG);
                if (viewById == null) {
                    contentParent.addView(fullCtrlView);
                }
            }*/
        } else if (fullCtrlChange) {
            fullCtrlChange = false;
           /* removeCtrlView(contentParent);
            if (fullCtrlView != null && contentParent != null) {
                contentParent.addView(fullCtrlView);
            }*/
        }

        if (fullEmbedView != null) {
            if (fullEmbedView.onConfigurationChanged(this, portrait)) {
                if (!fullEmbedView.doHandleConfigurationChanged(portrait)) {
                    doOnConfigurationChanged(portrait, true);
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (fullEmbedView != null) {
                                    fullEmbedView.setBackgroundColor(Color.BLACK);
                                }
                            }
                        }, 500);

                } else if (fullEmbedView != null) {
                    fullEmbedView.setBackgroundColor(Color.BLACK);
                }
            } else {
                setPortrait(true);
                if(mCtrlView!=null){
                    mCtrlView.onFullScreenChange(!portrait);

                }
                if (fullEmbedView != null) {
                    fullEmbedView.setBackgroundColor(Color.BLACK);
                }
            }
        }
        if(mCtrlView!=null){
            mCtrlView.onFullScreenChange(!portrait);
        }
        if(StateListenerProxy !=null){
            StateListenerProxy.onFullScreenChange(!portrait);
        }
    }
    private void setFullScreen(boolean fullScreen) {
        if (getContext() != null) {
            Activity activity = (Activity) getContext();
            WindowManager.LayoutParams attrs = activity.getWindow()
                    .getAttributes();
            if (fullScreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                activity.getWindow().setAttributes(attrs);
            } else {

                activity.getWindow().setAttributes(attrs);
            }
        }
    }
    public void doOnConfigurationChanged(final boolean portrait, boolean playing) {
        setPortrait(portrait);
            setFullScreen(!portrait);
            if (portrait) {
                ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.height=ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT;
                setLayoutParams(layoutParams);
            } else {
                final DisplayMetrics metrics=getDisplayMetrics(getContext());
                final ViewGroup.LayoutParams layoutParams = getLayoutParams();
                layoutParams.width = metrics.widthPixels;

                final ValueAnimator mValueAnimator = ValueAnimator.ofFloat(0, 1);
                mValueAnimator.setDuration(1000);
                mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        layoutParams.height = (int) Math.max(getHeight(), metrics.heightPixels * value);
                        setLayoutParams(layoutParams);
                    }
                });
                mValueAnimator.addListener(new AnimalEventImpl(){
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    }
                });
                mValueAnimator.start();

            }
            if (playing) {
                if(mCtrlView !=null){
                    mCtrlView.showCtrlPanel(false);
                }
            }
    }
    public  DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (manager != null) {
            if (android.os.Build.VERSION.SDK_INT >= 17) {
                manager.getDefaultDisplay().getRealMetrics(metrics);
            } else {
                manager.getDefaultDisplay().getMetrics(metrics);
            }
        }
        return metrics;
    }
    public void setSurfaceViewReuse(boolean reuse){

    }
    public void  setImageModelMatchParent(){

    }
    //===================================================
    public abstract boolean startPlay(String url);
    public abstract boolean startPlay(String url,int StartPos);
    public abstract void OnDestroy();
    public abstract IPlayerEntity getPlayerEntity();

}
