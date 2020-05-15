package com.dzfd.gids.baselibs.UI.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.fresco.FrescoImageLoaderHelper;
import com.dzfd.gids.baselibs.utils.Utils;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by zheng on 2019/3/9.
 */

public class LableImage extends RelativeLayout {
    private SimpleDraweeView _bkimg;
    private SimpleDraweeView _lbimg;
    private int lb_width,lb_height;

    private boolean showLable;
    public LableImage(Context context) {
        this(context,null);
    }

    public LableImage(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public LableImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context,attrs);
    }
    public void InitView(Context context, AttributeSet attrs){
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bunlistview=layoutInflater.inflate(R.layout.hot_image,this);
        _bkimg=findViewById(R.id.content_img);
        _lbimg=findViewById(R.id.content_lbe);
        TypedArray types=context.obtainStyledAttributes(attrs,R.styleable.LableImage);
        if(types == null){
           return;
        }
        setImageFromTypes(_bkimg,types,R.styleable.LableImage_bk_url,R.styleable.LableImage_bk_src);
        setImageFromTypes(_lbimg,types,R.styleable.LableImage_lb_url,R.styleable.LableImage_lb_src);
        int marginright= types.getDimensionPixelSize(R.styleable.LableImage_right_offset,0);
        int margintop= types.getDimensionPixelSize(R.styleable.LableImage_top_offset,0);
        RelativeLayout.LayoutParams bklayout=(RelativeLayout.LayoutParams)_bkimg.getLayoutParams();
        bklayout.setMargins(0,margintop,marginright,0);
        _bkimg.setLayoutParams(bklayout);

        int radius=types.getDimensionPixelSize(R.styleable.LableImage_bk_radius,0);
        if(radius!=0){
            RoundingParams bitmapOnlyParams = RoundingParams.fromCornersRadius(radius) .setRoundingMethod(RoundingParams.RoundingMethod.BITMAP_ONLY);
            _bkimg.getHierarchy().setRoundingParams(bitmapOnlyParams);
        }
        boolean isshow=types.getBoolean(R.styleable.LableImage_show_lable,true);
        lb_width=types.getDimensionPixelSize(R.styleable.LableImage_lb_width, Utils.dip2px(10));
        lb_height=types.getDimensionPixelSize(R.styleable.LableImage_lb_height,Utils.dip2px(10));

        RelativeLayout.LayoutParams lblayout=(RelativeLayout.LayoutParams)_lbimg.getLayoutParams();
        lblayout.width=lb_width;
        lblayout.height=lb_height;
        _lbimg.setLayoutParams(lblayout);

        setShowLableImage(isshow);
        types.recycle();
    }
    private void setImageFromTypes(SimpleDraweeView view,TypedArray types,int urlindex,int srcindex){
        String bk_url=types.getString(urlindex);
        if(!TextUtils.isEmpty(bk_url)){
            FrescoImageLoaderHelper.setImageByUrl(view,bk_url);
        }
        Drawable bk_src= types.getDrawable(srcindex);
        if(bk_src !=null){
            view.setBackgroundDrawable(bk_src);
        }
    }
    public void  setBkImage(Bitmap bm){
        if(_bkimg!=null && bm!=null){
            _bkimg.setImageBitmap(bm);
        }
    }
    public void  setLableImage(Bitmap bm){
        if(_lbimg!=null && bm!=null){
            _lbimg.setImageBitmap(bm);
        }
    }
    public void setBkImage(int resid){
        if(_bkimg!=null && resid!=0){
            _bkimg.setImageResource(resid);
        }
    }
    public void  setLableImage(int resid){
        if(_lbimg == null){
            return;
        }
        if(resid!=0){
            _lbimg.setImageResource(resid);
        }
    }
    public void setBkImage(String url){
        if(_bkimg==null) {
            return;
        }
        if(url!=null){
            FrescoImageLoaderHelper.setImageByUrl(_bkimg,url);
        }
    }
    public void  setLableImage(String url){
        if(_lbimg == null){
            return;
        }
        if(url!=null){
            FrescoImageLoaderHelper.setImageByUrl(_lbimg,url);
        }
    }
    public void setShowLableImage(boolean value){
        int visible=value?VISIBLE:GONE;
        if(_lbimg!=null){
            _lbimg.setVisibility(visible);
        }
    }
    public boolean isShowLable(){
        if(_lbimg!=null){
           int visible= _lbimg.getVisibility();
           return visible==VISIBLE;
        }
        return false;
    }
    public void setBkImage(String url, int width, int height) {
        if(_bkimg==null) {
            return;
        }
        if(url!=null){
            FrescoImageLoaderHelper.setImageByUrl(_bkimg,url, width, height);
        }
    }
}
