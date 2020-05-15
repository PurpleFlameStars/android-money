package com.dzfd.gids.baselibs.UI.pullrefresh;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.dzfd.gids.baselibs.utils.Utils;


public class TipDrawable extends RefreshDrawable {

    private RectF mBounds;
    private int mWidth;
    private int mHeight;
    private Paint mPaint;
    private String mDrawTextFailed;
    private int mTextSize;
    private int mPadding;
    private int mBackColor;
    private int mTextColor;
    private Animator animator;
    private int mSize;
    private boolean isRunning;

    TipDrawable(Context context, BunRefreshLayout layout) {
        super(context, layout);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mSize = mTextSize = Utils.dp2px(context, 15);
        mPadding = Utils.dp2px(context, 12);
    }

    public void setText(String text) {
        mDrawTextFailed = text;
    }

    @Override
    public void setPercent(float percent) {

    }

    @Override
    public void setColorSchemeColors(int[] colorSchemeColors) {
    }

    @Override
    public void offsetTopAndBottom(int offset) {

    }

    @Override
    public void setColor(int color) {
        mBackColor = color;
    }

    public void setTextColor(int color) {
        mTextColor = color;
    }

    @Override
    public void start() {
        isRunning = true;
        animator = generateAnimation();
        animator.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        if (animator != null && animator.isRunning()) {
            animator.end();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        mWidth = getRefreshLayout().getFinalOffset();
        mHeight = mWidth;
        bounds.bottom = bounds.top + mHeight;
        mBounds = new RectF(bounds.left, bounds.top, bounds.right, bounds.bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        mPaint.setColor(mBackColor);
        canvas.drawRect(mBounds.left, mBounds.top, mBounds.right, mBounds.bottom, mPaint);

        mPaint.setTextSize(mTextSize);
        //该方法即为设置基线上那个点究竟是left,center,还是right  这里我设置为center
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(mTextColor);
        //   canvas.drawText(mDrawTextFailed, mBounds.centerX(), mBounds.centerY() - 1.0f * mPadding - (mSize - mTextSize) / 2, mPaint);
        //文本居中显示
        //   canvas.drawText(mDrawTextFailed, mBounds.centerX(), mBounds.centerY(), mPaint);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        int baseLineY = (int) (mBounds.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式
        canvas.drawText(mDrawTextFailed, mBounds.centerX(), baseLineY, mPaint);
    }

    protected void setSize(int size) {
        mTextSize = size;
        invalidateSelf();
    }

    protected float getSize() {
        return mTextSize;
    }

    private Animator generateAnimation() {
        AnimatorSet animatorSet = new AnimatorSet();
        //alpha animation
        ObjectAnimator sizeAnimator = ObjectAnimator.ofInt(this, "size", (int) (mSize * 0.6f), mSize);
        sizeAnimator.setDuration(200);
        sizeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(sizeAnimator);
        return animatorSet;
    }
}
