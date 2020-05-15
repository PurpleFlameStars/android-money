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

/**
 * Created by zheng on 2019/3/8.
 */

public class BunHeaderView extends RelativeLayout implements View.OnClickListener ,IBunLoadMore{

    private View mRootView;
    private View vSplitLeft,vSplitRight;
    private TextView txtContent;
    private ProgressBar loadbar;
    private BunLoadState _state;
    private RecyclerScrollListener._LoadMoreData _loadmore;
    private HttpLoader.HttpLoadPos _loadpos;

    public BunHeaderView(Context context) {
        this(context,null);
    }

    public BunHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BunHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context,attrs);
    }
    private void InitView(Context context, AttributeSet attrs) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mRootView= layoutInflater.inflate(R.layout.recycelview_header, this);
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
        android.os.Handler msghandler=new android.os.Handler();
        msghandler.post(new Runnable() {
            @Override
            public void run() {
                switch (state){
                    case LOADING:
                    {
                        mRootView.setVisibility(VISIBLE);
                        vSplitRight.setVisibility(GONE);
                        vSplitLeft.setVisibility(GONE);
                        loadbar.setVisibility(VISIBLE);
                        txtContent.setText(R.string.loadmore_loading);
                        break;
                    }
                    case END:
                    {
                        setVisibility(GONE);
                        break;
                    }
                    case INIT:
                    {
                        setVisibility(GONE);
                        break;
                    }
                    case FAILED:
                    {
                        mRootView.setVisibility(VISIBLE);
                        vSplitRight.setVisibility(GONE);
                        vSplitLeft.setVisibility(GONE);
                        loadbar.setVisibility(GONE);
                        txtContent.setVisibility(VISIBLE);
                        txtContent.setText(R.string.loadmore_failed);
                        break;
                    }
                }
            }
        });
    }
}
