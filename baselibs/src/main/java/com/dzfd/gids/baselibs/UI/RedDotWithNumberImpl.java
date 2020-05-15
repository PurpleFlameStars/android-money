package com.dzfd.gids.baselibs.UI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.dzfd.gids.baselibs.listener.IRedDotWithNumber;
import com.dzfd.gids.baselibs.utils.Utils;

public class RedDotWithNumberImpl implements IRedDotWithNumber {

    private Paint paint1;//绘制红点
    private final Paint paint2;//绘制数字
    private int msgNum;//数字值
    private Rect textBounds;//文字的bound
    private int redDotSize = 6;//红点大小
    private boolean IsShowRed=true;

    private IViewInvalidate _listener;

    public RedDotWithNumberImpl(Context context,IViewInvalidate _listener){
        paint1 = new Paint();
        paint2 = new Paint();
        paint1.setStyle(Paint.Style.FILL);
        paint1.setAntiAlias(true);
        paint2.setAntiAlias(true);
        Resources resources=context.getResources();
        paint1.setColor(resources.getColor(android.R.color.holo_red_light));
        paint2.setColor(resources.getColor(android.R.color.white));
        this._listener=_listener;
    }
    @Override
    public void setMsgNum(int msgNum) {
        if(msgNum <0)
        {
            this.msgNum = 0;
        }else
        {
            this.msgNum = msgNum;
        }
        if(_listener!=null){
            _listener.postInvalidate();
        }
    }
    @Override
    public void setRedDotSize(int redDotSize) {
        if(redDotSize >10 || redDotSize <=1)
            return;
        this.redDotSize = redDotSize;
        if(_listener!=null){
            _listener.postInvalidate();
        }
    }
    @Override
    public void setShowRed(Boolean value){
        if(value == this.IsShowRed){
            return;
        }
        this.IsShowRed=value;
        if(_listener!=null){
            _listener.postInvalidate();
        }
    }
    public void OnDraw(Canvas canvas,int measuredWidth){
        if(!IsShowRed){
            return;
        }
        int radius=Utils.dip2px(4);
        String strnum=(msgNum>99)?"99+":String.valueOf(msgNum);

        if(msgNum>0){
            paint2.setTextSize(Utils.dip2px(8));
            textBounds = new Rect();
            paint2.getTextBounds(strnum, 0, strnum.length(), textBounds);
            int halftwidth=textBounds.width()/2;
            int halfheight=textBounds.height()/2;
            int Squr=halftwidth*halftwidth+halfheight*halfheight;
            radius=(int) Math.sqrt(Squr)+Utils.dip2px(2);
        }
        int cx=measuredWidth - radius;
        if(_listener!=null){
            cx=_listener.drawXStart();
            if(msgNum>0){
                cx+=+radius;
            }
        }
        canvas.drawCircle(cx, radius, radius, paint1);
        if(msgNum>0){
            paint2.setTextAlign(Paint.Align.LEFT);
            Paint.FontMetricsInt fontMetrics = paint2.getFontMetricsInt();
            int baseline = (radius * 2 - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
            canvas.drawText(strnum, cx -2- textBounds.width() / 2, baseline, paint2);
        }
    }
    public interface  IViewInvalidate{
        void postInvalidate();
        int drawXStart();
    }

}
