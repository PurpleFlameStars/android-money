package com.dzfd.gids.baselibs.UI.recyclerview.wrapper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.recyclerview.RecyclerScrollListener;
import com.dzfd.gids.baselibs.network.HttpLoader;
import com.dzfd.gids.baselibs.utils.thread.ThreadUtils;

/**
 * Created by zheng on 2019/3/8.
 */

public class BunFooterView extends RelativeLayout implements View.OnClickListener ,IBunLoadMore{
//    public static final  int STATE_LOADING=1,STATE_FAILED=2,STATE_END=3,STATE_INIT=4;

    private View mRootView;
    private View vSplitLeft,vSplitRight;
    private TextView txtContent;
    private ProgressBar loadbar;
    private IBunLoadMore.BunLoadState _state;
    private RecyclerScrollListener._LoadMoreData _loadmore;
    private HttpLoader.HttpLoadPos _loadpos;
    private boolean showLoadEnd=true;

    public BunFooterView(Context context) {
        this(context,null);
    }

    public BunFooterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BunFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context,attrs);
    }
    private void InitView(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView= layoutInflater.inflate(R.layout.recycelview_footer, this);
        if(mRootView!=null){
            mRootView.setOnClickListener(this);
        }
        vSplitLeft=findViewById(R.id.spit_left);
        vSplitRight=findViewById(R.id.spit_right);
        txtContent=findViewById(R.id.txt_footer_content);
        loadbar=findViewById(R.id.loading_bar);
        UpdataState(BunLoadState.INIT);
    }
    public void  setLoadMoreInterface(RecyclerScrollListener._LoadMoreData callback,HttpLoader.HttpLoadPos loadpos){
        this._loadmore=callback;
        this._loadpos=loadpos;
    }
    public void setShowLoadEnd(boolean showLoadEnd){
        this.showLoadEnd=showLoadEnd;
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v == mRootView){
            if(this._state == BunLoadState.FAILED){
                if(_loadmore!=null){
                    _loadmore.LoadMoreDataFromBottom(_loadpos);
                }
            }
        }
    }

    @Override
    public void UpdataState(final BunLoadState state) {
        if(state == this._state){
            return;
        }
        this._state=state;
       boolean ismainthread= ThreadUtils.isMainThread();
       if(ismainthread){
           SetStateInMainThread(state);
       }else{
           ThreadUtils.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   SetStateInMainThread(state);
               }
           });
       }
    }
    private void SetStateInMainThread(BunLoadState state){
        switch (state){
            case LOADING:
            {
                vSplitRight.setVisibility(GONE);
                vSplitLeft.setVisibility(GONE);
                loadbar.setVisibility(VISIBLE);
                txtContent.setVisibility(VISIBLE);
                txtContent.setText(R.string.loadmore_loading);
                break;
            }
            case END:
            {
                int visible=showLoadEnd?VISIBLE:INVISIBLE;
                vSplitRight.setVisibility(visible);
                vSplitLeft.setVisibility(visible);
                loadbar.setVisibility(GONE);
                txtContent.setVisibility(visible);
                txtContent.setText(R.string.loadmore_end);
                break;
            }
            case INIT:
            {
                vSplitRight.setVisibility(VISIBLE);
                vSplitLeft.setVisibility(VISIBLE);
                loadbar.setVisibility(GONE);
                txtContent.setVisibility(VISIBLE);
                txtContent.setText(R.string.loadmore_init);
                break;
            }
            case FAILED:
            {
                vSplitRight.setVisibility(GONE);
                vSplitLeft.setVisibility(GONE);
                loadbar.setVisibility(GONE);
                txtContent.setVisibility(VISIBLE);
                txtContent.setText(R.string.loadmore_failed);
                break;
            }
            case NOMORE:{
                vSplitRight.setVisibility(VISIBLE);
                vSplitLeft.setVisibility(VISIBLE);
                loadbar.setVisibility(GONE);
                txtContent.setVisibility(VISIBLE);
                txtContent.setText(R.string.loadmore_nomore);
            }
        }
        if(state == BunLoadState.INIT){
            BunFooterView.this.setVisibility(GONE);
        }else {
            BunFooterView.this.setVisibility(VISIBLE);

        }
    }
}
