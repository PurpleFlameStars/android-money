package com.dzfd.gids.baselibs.UI.imageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.dzfd.gids.baselibs.utils.Utils;
import com.facebook.drawee.view.SimpleDraweeView;


public class RedDotImageView extends SimpleDraweeView {

    private Paint paint1;//绘制红点
    private final Paint paint2;//绘制数字
    private int msgNum;//数字值
    private Rect textBounds;//文字的bound
    private int redDotSize = 6;//红点大小
    private boolean IsShowRed=true;

    public RedDotImageView(Context context) {
        this(context,null);
    }

    public RedDotImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RedDotImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint1 = new Paint();
        paint2 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setColor(getResources().getColor(android.R.color.holo_red_light));
        paint2.setColor(getResources().getColor(android.R.color.white));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!IsShowRed){
            return;
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();

        int radius;
        if (measuredHeight > measuredWidth) {
            radius = measuredWidth / redDotSize;

        } else {
            radius = measuredHeight / redDotSize;
        }
        String strnum=(msgNum>99)?"99+":String.valueOf(msgNum);

        if(msgNum>0){
            paint2.setTextSize(Utils.dip2px(14));
            textBounds = new Rect();
            paint2.getTextBounds(strnum, 0, strnum.length(), textBounds);
            int halftwidth=textBounds.width()/2;
            int halfheight=textBounds.height()/2;
            int Squr=halftwidth*halftwidth+halfheight*halfheight;
            radius=(int) Math.sqrt(Squr)+4;
        }
        canvas.drawCircle(measuredWidth - radius, radius, radius, paint1);
        if(msgNum>0){
            paint2.setTextAlign(Paint.Align.LEFT);
            Paint.FontMetricsInt fontMetrics = paint2.getFontMetricsInt();
            int baseline = (radius * 2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(strnum, getMeasuredWidth() - radius - textBounds.width() / 2, baseline, paint2);
        }

    }

    public void setMsgNum(int msgNum) {
       if(msgNum <0)
        {
            this.msgNum = 0;
        }else
        {
            this.msgNum = msgNum;
        }
        postInvalidate();
    }

    public void setRedDotSize(int redDotSize) {
        if(redDotSize >10 || redDotSize <=1)
            return;
        this.redDotSize = redDotSize;
        postInvalidate();
    }
    public void setShowRed(Boolean value){
        if(value == this.IsShowRed){
            return;
        }
        this.IsShowRed=value;
        postInvalidate();
    }


}
