package com.GoLemon.supplier.impl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

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
    public static void ScaleY(View view,int from,int to){
        ObjectAnimator animator=ObjectAnimator.ofFloat(view,"scaleY",from,to);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        animator.setDuration(1000);//设置动画时间
        animator.start();
    }
    public static void HighSizeChange(final View view, int to, final Runnable endrun){
        int from = view.getHeight();
        ValueAnimator va =ValueAnimator.ofInt(from,to);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
               ViewGroup.LayoutParams layoutParams= view.getLayoutParams();
               layoutParams.height=h;
                view.setLayoutParams(layoutParams);
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
        //开始动画
        va.start();
    }



    public static void RotationAnimal(View view,float to, final Runnable onStart, Runnable onEnd){
        float from=view.getRotation();
        ObjectAnimator  objectAnimator = ObjectAnimator.ofFloat(view,"rotation",from,to);
        objectAnimator.setDuration(500);
        objectAnimator.addListener(new AnimalEventImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(onStart!=null){
                    onStart.run();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        objectAnimator.start();

    }
    public static void RotationAnimalWithXy(View view,float to, float x, float y,final Runnable onStart, Runnable onEnd){
        float from=view.getRotation();
        ObjectAnimator  objectAnimator = ObjectAnimator.ofFloat(view,"rotation",from,to);
        objectAnimator.setDuration(1000);
        view.setPivotX(x);
        view.setPivotY(y);
        objectAnimator.addListener(new AnimalEventImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(onStart!=null){
                    onStart.run();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }
        });
        objectAnimator.start();

    }
    public static void AlphaIn(View view, Runnable onStart, Runnable onEnd){
        AlphaAnimal(view,onStart,onEnd,0.0f,1.0f);
    }
    public static void AlphaOut(View view, Runnable onStart, Runnable onEnd){
        AlphaAnimal(view,onStart,onEnd,1.0f,0.0f);
    }
    public static void AlphaOut(View view, Runnable onStart, Runnable onEnd,int duration){
        AlphaAnimal(view,onStart,onEnd,1.0f,0.0f,duration);
    }
    private static void AlphaAnimal(View view, final Runnable onStart, final Runnable onEnd,float startvalue,float endvalue){
       AlphaAnimal(view,onStart,onEnd,startvalue,endvalue,500);
    }
    private static void AlphaAnimal(View view, final Runnable onStart, final Runnable onEnd,float startvalue,float endvalue,int duration){
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,"alpha",startvalue,endvalue);
        objectAnimator.setDuration(duration);
        objectAnimator.addListener(new AnimalEventImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(onStart!=null){
                    onStart.run();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(onEnd!=null){
                    onEnd.run();
                }
            }
        });

        try {
            // 4.4手机动画崩溃
            objectAnimator.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void TransXRightHide(final View oldView, final Runnable endrun) {
        float end=oldView.getWidth();
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(oldView, "translationX", 0 ,end);

        animator1.addListener(new AnimalEventImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
               if(endrun!=null){
                   endrun.run();
               }
            }
        });
        animator1.setDuration(5000000).start();
    }
    public static void TransXAnimal(final View View,float from,float to ,final Runnable startrun,final Runnable endrun) {

        ObjectAnimator animator1 = ObjectAnimator.ofFloat(View, "translationX", from ,to);

        animator1.addListener(new AnimalEventImpl() {
            @Override
            public void onAnimationStart(Animator animation) {
                if(startrun!=null){
                    startrun.run();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(endrun!=null){
                    endrun.run();
                }
            }
        });
        animator1.setDuration(500).start();
    }

}
