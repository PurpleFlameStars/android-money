package com.dzfd.gids.baselibs.UI.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.utils.Utils;


public class LoadingLayout extends LinearLayout {

    private AnimationDrawable animationDrawable;
    private ImageView mImageView;
    private RotateAnimation mAnimation;
    private ProgressBar _loadingbar;
    private TextView _LoadTxtView;

    public LoadingLayout(Context context) {
        this(context,null);
    }

    public LoadingLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LoadingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_loading,this, true);
        initView(context);
    }

    private void initView(Context context) {
        _loadingbar=findViewById(R.id.loading_bar);
        _LoadTxtView=findViewById(R.id.loading_text);


       /* mImageView = new ImageView(context);
        mImageView.setImageResource(R.drawable.loading);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mImageView, params);
        CreateRotaAnimation();*/
    }
    private void CreateRotaAnimation(){
        mAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setInterpolator(new LinearInterpolator());
    }

    public void startLoadingAnimation() {
        /*if(mImageView!=null && mAnimation!=null){
            mImageView.startAnimation(mAnimation);
        }*/
        if(_loadingbar!=null){
            _loadingbar.setVisibility(VISIBLE);
        }
    }

    public void stopLoadingAnimation() {
        /*if (mImageView != null){
            mImageView.clearAnimation();
        }*/
        if(_loadingbar!=null){
            _loadingbar.setVisibility(GONE);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility == VISIBLE){
            startLoadingAnimation() ;
        }else{
            stopLoadingAnimation();
        }
    }
    public  void setLoadingText(String str){
        Utils.setTextViewText(_LoadTxtView,str);
    }
    public void setLoadingText(int resid){
        Utils.setTextViewText(_LoadTxtView,resid);
    }
}