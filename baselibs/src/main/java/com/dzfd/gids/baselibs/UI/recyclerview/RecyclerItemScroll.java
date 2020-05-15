package com.dzfd.gids.baselibs.UI.recyclerview;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by zheng on 2019/2/8.
 */

public class RecyclerItemScroll extends RecyclerView.OnScrollListener {

    public boolean _isScrollToTop;

    public RecyclerItemScroll() {
        Reset();
    }
    public void Reset(){
        _isScrollToTop = true;

    }



    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        int count =manager.getItemCount();
        if(count<1){
            return;
        }
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            int firstItemPosition = -1;
            if (manager instanceof LinearLayoutManager) {
                firstItemPosition = ((LinearLayoutManager) manager).findFirstCompletelyVisibleItemPosition();
            } else if (manager instanceof StaggeredGridLayoutManager) {
                int columnCount = ((StaggeredGridLayoutManager) manager).getSpanCount();
                int positions[] = new int[columnCount];
                ((StaggeredGridLayoutManager) manager).findFirstCompletelyVisibleItemPositions(positions);
                firstItemPosition = positions[0];
            }
            _isScrollToTop = (firstItemPosition == 0);
        } else {
            _isScrollToTop = false;
        }
    }


}
