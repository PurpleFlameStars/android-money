package com.dzfd.gids.baselibs.UI.widgets;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.recyclerview.bean.LoadErrorBean;


/**
 * Created by zheng on 2018/11/14.
 */

public class NoNetWorkView extends LinearLayout implements View.OnClickListener {
    private LoadErrorBean mErrorBean;
    private TextView txtView1,txtView2;
    private Button btnRetry;
    public NoNetWorkView(@NonNull Context context) {
        this(context,null);
    }

    public NoNetWorkView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public NoNetWorkView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER);
    }
    public static NoNetWorkView getView(Context context){
        if(context == null){
            return null;
        }
        LayoutInflater layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (NoNetWorkView)layoutInflater.inflate(R.layout.view_nonetwork,null, false);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        btnRetry=findViewById(R.id.error_retry);
        if(btnRetry!=null){
            btnRetry.setOnClickListener(this);
        }
        txtView1=findViewById(R.id.txt_title1);
        txtView2=findViewById(R.id.txt_title2);
        ImageView errorimage=findViewById(R.id.error_image);
        if(errorimage!=null){
           /* int width= DeviceUtils.getDisplayMetrics(getContext()).widthPixels;
            float fwidth=(width*654);
            float height=fwidth/(float) 1080;
            ViewGroup.LayoutParams params=errorimage.getLayoutParams();
            params.width=width;
            params.height=(int) height;
            errorimage.setLayoutParams(params);*/
        }
    }

    @Override
    public void onClick(View v) {
        if(v!=null && v.getId()==R.id.error_retry){
            if(mErrorBean!=null && mErrorBean.iretry!=null){
                mErrorBean.iretry.OnRetryLoad(mErrorBean.pos,true);
            }
        }
    }

    public NoNetWorkView SetLoadErrorBean(LoadErrorBean bean){
        mErrorBean=bean;
        return this;
    }


    public void setTitle1Text(String msgtext){
        setTextView(txtView1,msgtext);

    }
    public void setTitle1Text(int resid){
        setTextView(txtView1,resid);

    }
    public void setTitle2Text(String msgtext){
        setTextView(txtView2,msgtext);

    }
    public void setTitle2Text(int resid){
        setTextView(txtView2,resid);

    }
    public void setButtonText(String msgtext){
        setTextView(btnRetry,msgtext);
    }
    public void setButtonText(int resid){
        setTextView(btnRetry,resid);
    }

    private void setTextView(TextView view, String str){
        if(view!=null && !TextUtils.isEmpty(str)){
            view.setText(str);
        }
    }
    private void setTextView(TextView view, int strid){
        if(view!=null && strid!=0){
            view.setText(strid);
        }
    }
    public void ResetAllString(){
        SetAllString(0,0,0);
    }
    public NoNetWorkView SetAllString(int titleid,int contentid,int butid){
        if(titleid==0){
            titleid=R.string.nonetview_title1;
        }
        setTitle1Text(titleid);
        if(contentid==0){
            contentid=R.string.nonetview_title2;
        }
        setTitle2Text(contentid);
        if(butid ==0){
            butid=R.string.nonetview_button_retry;
        }
        setButtonText(butid);
        reSetAllVisibility();
        return this;
    }

    private void reSetAllVisibility() {
        if (txtView1 != null) {
            txtView1.setVisibility(VISIBLE);
        }
        if (txtView2 != null) {
            txtView2.setVisibility(VISIBLE);
        }
        if (btnRetry != null) {
            btnRetry.setVisibility(VISIBLE);
        }
    }

    public void hideRetryBtn() {
        if (btnRetry != null) {
            btnRetry.setVisibility(GONE);
        }
    }

    public NoNetWorkView SetAllString(int titleid,String content,int butid){
        SetAllString(titleid,0,butid);
        if(content!=null){
            setTitle2Text(content);
        }
        return this;
    }


}
