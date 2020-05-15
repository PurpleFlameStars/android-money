package com.dzfd.gids.baselibs.UI.helper;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.GoLemon.supplier.impl.AnimalEventImpl;

/**
 * Created by zheng on 2018/12/29.
 */

public class AnimalHelper {

    public static void animIn(final View view, final Runnable runnable) {
        if (view == null) {
            runnable.run();
            return;
        }

        view.clearAnimation();
        Animation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(animation);
        animation.startNow();
    }
    public static void animOut(final View view, final int visibility, final Runnable runnable) {
        if (view == null) {
            runnable.run();
            return;
        }

        view.clearAnimation();
        Animation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(visibility);
                if (runnable != null) {
                    runnable.run();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(animation);
        animation.startNow();
    }
    public static void AlphaAnima(final View view, float start, final float end) {


        view.clearAnimation();
        Animation animation = new AlphaAnimation(start, end);
        animation.setDuration(500);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(0);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setAlpha(end);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setAnimation(animation);
        animation.startNow();
    }
    public static void AlphaAnimaltion(final View v, boolean in, final Runnable runnable){
        float fromvalue=in?0f:1f;
        final float toValue=in?1f:0f;
         ValueAnimator animator = ValueAnimator.ofFloat(fromvalue,toValue);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();
                v.setAlpha(value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }
            @Override
            public void onAnimationEnd(Animator animation) {
                if(runnable !=null){
                    runnable.run();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.setRepeatCount(0);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
    }
    public static ValueAnimator MarginBottomChange(final View view, int to, final Runnable endrun){
        ViewGroup.MarginLayoutParams layoutParams=(ViewGroup.MarginLayoutParams) view.getLayoutParams();
        int from = layoutParams.bottomMargin;
        ValueAnimator va =ValueAnimator.ofInt(from,to,from);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //获取当前的height值
                int h =(Integer)animation.getAnimatedValue();
                //动态更新view的高度
                ViewGroup.MarginLayoutParams layoutParams1 =(ViewGroup.MarginLayoutParams)view.getLayoutParams();
                layoutParams1.bottomMargin=h;
                view.setLayoutParams(layoutParams1);
            }
        });
        va.addListener(new AnimalEventImpl(){
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(endrun!=null){
                    endrun.run();
                }
            }
        });
        va.setDuration(500);
        va.setRepeatCount(ValueAnimator.INFINITE);
        va.setRepeatMode(ValueAnimator.RESTART);
        //开始动画
        va.start();
        return va;
    }







}
