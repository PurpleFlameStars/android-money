package com.dzfd.gids.baselibs.UI.recyclerview;

import android.content.Context;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.dzfd.gids.baselibs.UI.recyclerview.lisenter.OnPageChangeListener;
import com.dzfd.gids.baselibs.UI.recyclerview.lisenter.PageScrollListener;

/**
 * Created by zheng on 2019/8/23.
 */

public class BunPageRecycleView extends BunRecycleView implements OnPageChangeListener {
    private PagerSnapHelper _snapHelper;
    private PageChangeListener _WrapListener;

    public BunPageRecycleView(Context context) {
        super(context);
    }

    public BunPageRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BunPageRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void SetPageChangeListener(PageChangeListener listener){
        _WrapListener=listener;
    }

    @Override
    protected RecyclerView.OnScrollListener getOnScrollListener(){
        RecyclerView.OnScrollListener listener=super.getOnScrollListener();
        _snapHelper=new PagerSnapHelper();
        _snapHelper.attachToRecyclerView(mCoreView);
        PageScrollListener listener1=new PageScrollListener(_snapHelper,this,listener);
        return listener1;
    }
//=================================================
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    @Override
    public void onPageSelected(int position, View view) {
        if(_WrapListener!=null){
            _WrapListener.OnPageChange(position,view,false);
        }
    }
    //======================================================
    public interface PageChangeListener{
        void OnPageChange(int postion,View view,boolean auto);
    }
}
