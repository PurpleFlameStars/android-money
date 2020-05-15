package com.dzfd.gids.baselibs.UI.recyclerview;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.dzfd.gids.baselibs.network.HttpLoader;

/**
 * Created by zheng on 2019/2/8.
 */

public class RecyclerScrollListener extends RecyclerView.OnScrollListener{
    protected static final int SCROLL_STOP=0;
    protected static final int SCROLL_UP=-1;
    protected static final int SCROLL_DOWN=1;
//    private int lastScrollDirection=SCROLL_STOP;

    //用来标记是否正在向上滑动
    private boolean isSlidingUpward = false;
    private boolean isSlidingDownword = false;

    private _LoadMoreData callback;
    private int offset_loadmore;
    private boolean hasLoadMoreUI;
    public RecyclerScrollListener(_LoadMoreData listener,int offset_loadmore,boolean loadmoreui){
        callback=listener;
        this.offset_loadmore=offset_loadmore;
        this.hasLoadMoreUI=loadmoreui;
    }


    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
//        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        // 当不滑动时
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            //获取最后一个完全显示的itemPosition
            /*RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if(manager!=null && manager instanceof LinearLayoutManager){
                 int index=((LinearLayoutManager) manager).findLastCompletelyVisibleItemPosition();
                 int count=manager.getItemCount();
                 if(index == (count-1)){
                     fireLoadmore((LinearLayoutManager) manager);
                 }
            }*/
        }
    }
    private void fireLoadmore(LinearLayoutManager manager){
        int lastItemPosition = manager.findLastVisibleItemPosition();
        int itemCount = manager.getItemCount();
        int loadmoreoffset=this.hasLoadMoreUI?1:0;
        // 判断是否滑动到了最后一个item，并且是向上滑动
        if (lastItemPosition >= (itemCount-2 - loadmoreoffset)) {
            if(callback!=null){
                callback.LoadMoreDataFromBottom(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE);
            }
        }
    }
    private void gridLoadmore(GridLayoutManager manager){
        int lastItemPosition = manager.findLastVisibleItemPosition();
        int itemCount = manager.getItemCount();
        int loadmoreoffset=this.hasLoadMoreUI?1:0;
        // 判断是否滑动到了最后一个item，并且是向上滑动
        if (lastItemPosition >= (itemCount- manager.getSpanCount() - loadmoreoffset)) {
            if(callback!=null){
                callback.LoadMoreDataFromBottom(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE);
            }
        }
    }

    private void staggeredGridLoadMore(StaggeredGridLayoutManager manager) {
        int columnCount = manager.getSpanCount();
        int positions[] = new int[columnCount];
        manager.findLastVisibleItemPositions(positions);
        for (int i = 0; i < positions.length; i++) {
            if (positions[i] >= manager.getItemCount() - columnCount * 2) {
                //这里写要调用的东西
                if(callback!=null){
                    callback.LoadMoreDataFromBottom(HttpLoader.HttpLoadPos.POSTION_BOTTOM_LOADMORE);
                }
            }
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        // 大于0表示正在向上滑动，小于等于0表示停止或向下滑动
        isSlidingUpward = dy > 0;
        isSlidingDownword=dy<0;

        boolean isSlidingRight = dx < 0;
        boolean isSlidingLeft = dx > 0;

        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
            boolean breverse= gridLayoutManager.getReverseLayout();
            if( (isSlidingDownword && breverse) || (isSlidingUpward && !breverse) ){
                gridLoadmore(gridLayoutManager);
            }
        } else if (manager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            int orientation = linearLayoutManager.getOrientation();
            if (orientation == LinearLayoutManager.VERTICAL) {
                boolean breverse = linearLayoutManager.getReverseLayout();
                if ((isSlidingDownword && breverse) || (isSlidingUpward && !breverse)) {
                    fireLoadmore(linearLayoutManager);
                }
            } else if (orientation == LinearLayoutManager.HORIZONTAL) {
                boolean breverse = linearLayoutManager.getReverseLayout();
                if ((isSlidingRight && breverse) || (isSlidingLeft && !breverse)) {
                    fireLoadmore(linearLayoutManager);
                }
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) manager;
            boolean breverse = staggeredGridLayoutManager.getReverseLayout();
            if ((isSlidingDownword && breverse) || (isSlidingUpward && !breverse)) {
                staggeredGridLoadMore(staggeredGridLayoutManager);
            }
        }

    }
    public interface _LoadMoreData{
        void LoadMoreDataFromBottom(HttpLoader.HttpLoadPos loadpos);
    }

}
