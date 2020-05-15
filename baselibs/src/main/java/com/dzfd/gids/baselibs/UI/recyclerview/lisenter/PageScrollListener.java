package com.dzfd.gids.baselibs.UI.recyclerview.lisenter;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import android.view.View;

/**
 * Created by zheng on 2019/8/23.
 */

public class PageScrollListener extends RecyclerView.OnScrollListener {
    private SnapHelper snapHelper;
    private OnPageChangeListener onPageChangeListener;
    private int oldPosition = -1;//防止同一Position多次触发
    private RecyclerView.OnScrollListener _wraplistener;

    public PageScrollListener(SnapHelper snapHelper, OnPageChangeListener onPageChangeListener,RecyclerView.OnScrollListener wrap) {
        this.snapHelper = snapHelper;
        this.onPageChangeListener = onPageChangeListener;
        this._wraplistener=wrap;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (onPageChangeListener != null) {
            onPageChangeListener.onScrolled(recyclerView, dx, dy);
        }
        if(_wraplistener!=null){
            _wraplistener.onScrolled(recyclerView,dx,dy);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        int position = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        //获取当前选中的itemView
        View view = snapHelper.findSnapView(layoutManager);
        if (view != null) {
            //获取itemView的position
            position = layoutManager.getPosition(view);
        }
        if (onPageChangeListener != null) {
            onPageChangeListener.onScrollStateChanged(recyclerView, newState);
            //newState == RecyclerView.SCROLL_STATE_IDLE 当滚动停止时触发防止在滚动过程中不停触发
            if (newState == RecyclerView.SCROLL_STATE_IDLE && oldPosition != position) {
                oldPosition = position;
                View targetview = layoutManager.findViewByPosition(position);
                onPageChangeListener.onPageSelected(position,targetview);
            }
        }
        if(_wraplistener!=null){
            _wraplistener.onScrollStateChanged(recyclerView,newState);
        }
    }

}
