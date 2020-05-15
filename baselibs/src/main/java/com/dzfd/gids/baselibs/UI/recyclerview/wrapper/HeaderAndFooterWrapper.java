package com.dzfd.gids.baselibs.UI.recyclerview.wrapper;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.UI.recyclerview.BunRecycleViewAdpter;
import com.dzfd.gids.baselibs.UI.recyclerview.BunViewHolder;
import com.dzfd.gids.baselibs.listener.ListResult;
import com.dzfd.gids.baselibs.network.HttpLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.functions.Predicate;


public class HeaderAndFooterWrapper extends RecyclerView.Adapter<BunViewHolder>
{
    private int BASE_ITEM_TYPE_FOOTER_START=20000;
    private   int BASE_ITEM_TYPE_HEADER = 100000;
    private   int BASE_ITEM_TYPE_FOOTER = BASE_ITEM_TYPE_FOOTER_START;

    private SparseArrayCompat<View> mHeaderViews = new SparseArrayCompat<>();
    private SparseArrayCompat<View> mFootViews = new SparseArrayCompat<>();

    private BunRecycleViewAdpter mInnerAdapter;
    private IBunLoadMore.BunLoadState mLoadMoreState= IBunLoadMore.BunLoadState.INIT;

    public HeaderAndFooterWrapper(BunRecycleViewAdpter adapter)
    {
        mInnerAdapter = adapter;
    }

    @Override
    public BunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mHeaderViews.get(viewType) != null) {
            View headerView = mHeaderViews.get(viewType);
            return new EmptyHeaderHolder(headerView);
        } else if (mFootViews.get(viewType) != null) {
            View footView = mFootViews.get(viewType);
            if(viewType ==BASE_ITEM_TYPE_FOOTER_START){
                return new EmptyFooterHolder(footView);
            }else if(viewType == (BASE_ITEM_TYPE_FOOTER_START+1)){
                return new EmptyFooter1Holder(footView);
            }
        }
        return mInnerAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public int getItemViewType(int position)
    {
        if (isHeaderViewPos(position))
        {
            return mHeaderViews.keyAt(position);
        } else if (isFooterViewPos(position))
        {
            return mFootViews.keyAt(position - getHeadersCount() - getRealItemCount());
        }
        return mInnerAdapter.getItemViewType(position - getHeadersCount());
    }

    private int getRealItemCount()
    {
        return mInnerAdapter.getItemCount();
    }


    @Override
    public void onBindViewHolder(BunViewHolder holder, int position)
    {
        if (isHeaderViewPos(position))
        {
            return;
        }
        if (isFooterViewPos(position))
        {
            return;
        }
        mInnerAdapter.onBindViewHolder(holder, position - getHeadersCount());
    }
    @Override
    public void onViewRecycled(@NonNull BunViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onViewRecycled();
    }

    @Override
    public int getItemCount()
    {
        return getHeadersCount() + getFootersCount() + getRealItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView)
    {
        WrapperUtils.onAttachedToRecyclerView(mInnerAdapter, recyclerView, new WrapperUtils.SpanSizeCallback()
        {
            @Override
            public int getSpanSize(GridLayoutManager layoutManager, GridLayoutManager.SpanSizeLookup oldLookup, int position)
            {
                int viewType = getItemViewType(position);
                if (mHeaderViews.get(viewType) != null)
                {
                    return layoutManager.getSpanCount();
                } else if (mFootViews.get(viewType) != null)
                {
                    return layoutManager.getSpanCount();
                }
                if (oldLookup != null)
                    return oldLookup.getSpanSize(position);
                return 1;
            }
        });
    }

    @Override
    public void onViewAttachedToWindow(BunViewHolder holder)
    {
        mInnerAdapter.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (isHeaderViewPos(position) || isFooterViewPos(position))
        {
            WrapperUtils.setFullSpan(holder);
        }
    }

    private boolean isHeaderViewPos(int position)
    {
        return position < getHeadersCount();
    }

    private boolean isFooterViewPos(int position)
    {
        return position >= getHeadersCount() + getRealItemCount();
    }


    public synchronized void addHeaderView(View view)
    {
        int insertpos=BASE_ITEM_TYPE_HEADER;
        mHeaderViews.put(insertpos, view);
        int index=mHeaderViews.indexOfKey(insertpos);
        notifyItemInserted(index);
        BASE_ITEM_TYPE_HEADER++;

    }
    public void RemoveHeader(View view){
        if(mHeaderViews.size()<1 && view==null){
            return;
        }
        int index =mHeaderViews.indexOfValue(view);
        if(index == -1){
            return;
        }
        mHeaderViews.remove(index);
        notifyItemRemoved(index);

    }

    public synchronized void addFootView(View view)
    {
        int insertpos=BASE_ITEM_TYPE_FOOTER;
        int count=getItemCount();
        mFootViews.put(insertpos, view);
        notifyItemInserted(count);
        BASE_ITEM_TYPE_FOOTER++;
    }

    public int getHeadersCount()
    {
        return mHeaderViews.size();
    }

    public int getFootersCount()
    {
        return mFootViews.size();
    }
    public void UpdataLoadingState(HttpLoader.HttpLoadPos loadpos, IBunLoadMore.BunLoadState state){
        SparseArrayCompat<View> Loadingview=null;
        switch (loadpos){
            case POSTION_BOTTOM_LOADMORE:
            {
                Loadingview=mFootViews;
                break;
            }
        }
        if(Loadingview==null || Loadingview.size()<1){
            return;
        }
        for(int index=0;index<Loadingview.size();index++){
           int key= Loadingview.keyAt(index);
           View view = Loadingview.get(key);
           if(view!=null && view instanceof IBunLoadMore){
               ((IBunLoadMore) view).UpdataState(state);
           }
        }
        mLoadMoreState=state;
    }
    public IBunLoadMore.BunLoadState GetLoadMoreState(){
        return mLoadMoreState;
    }
    public void AddItemToTop(ListResult<BunItem> result, List<BunItem> items){
       if(mInnerAdapter != null){
           mInnerAdapter.AddItemToTop(result,items);
           notifyDataSetChanged();
       }
    }

    public void AddItemToBottom(int bottomoffset,List<BunItem> items){
        if(mInnerAdapter!=null){
            int refstart=getHeadersCount() +getRealItemCount();
            int count=items.size();
            mInnerAdapter.AddItemToBottom(bottomoffset,items);
            notifyItemRangeInserted(refstart,count);
        }
    }
    public boolean InsertItemToTop(int topoffset,BunItem item) {
        if(mInnerAdapter == null || topoffset<0 || item==null){
            return false;
        }
        List<BunItem> newitems=new ArrayList<>();
        newitems.add(item);
        return InsertItemToTop(topoffset,newitems);
    }
    public boolean InsertItemToTop(int topoffset,List<BunItem> items){
        if(mInnerAdapter == null || topoffset<0 || items==null || items.isEmpty()){
            return false;
        }
        int offset =mInnerAdapter.InsertItemToTop(topoffset,items);
        if(offset!=-1){
            int realpos=getHeadersCount() +offset;
            notifyItemRangeInserted(realpos,items.size());
        }
        return offset!=-1;
    }

    public void ClearData(){
        if(mInnerAdapter!=null){
            mInnerAdapter.ClearData(true);
            notifyDataSetChanged();
        }
    }

    public int getCount(){
        return getRealItemCount();
    }
    public void RefreshItem(int position){
         if(mInnerAdapter!=null){
            mInnerAdapter.notifyItemChanged(position);
        }
    }
    public void RefreshItem(int position, BunItem bunItem){
        if(mInnerAdapter!=null){
            mInnerAdapter.notifyItemChanged(position,bunItem);
        }
    }
    public BunRecycleViewAdpter getInnerAdpter(){
        return mInnerAdapter;
    }
    public void _notifyItemMoved(int fromPosition, int toPosition){
        if(mInnerAdapter!=null){
            boolean bres =mInnerAdapter.MoveItem(fromPosition,toPosition);
            if(bres){
                int headercount=getHeadersCount();
               notifyItemMoved(fromPosition+headercount,toPosition+headercount);
            }

        }
    }
    public void _notifyItemRemovedAndChanged(int postion){
        if(mInnerAdapter!=null){
           boolean bres = mInnerAdapter.RemoveItem(postion);
           if(bres){
                int headercount=getHeadersCount();
                final int removPos=postion+headercount;
                notifyItemRemoved(removPos);
                final int refreshcount=getItemCount()-removPos-getFootersCount();
                if(refreshcount<=0){
                    return;
                }
                //延迟刷新以便能看到动画
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        notifyItemRangeChanged(removPos,refreshcount);
                    }
                }, 600);
           }
        }
    }
    public void _notifyItemChanged(int postion,BunItem item){
        if(mInnerAdapter == null){
            return;
        }
        if(mInnerAdapter.ResetItem(postion,item)){
            int headercount=getHeadersCount();
            notifyItemChanged(postion+headercount);
        }

    }
    public Iterator<BunItem> getDataBefroPosition(int pos, int count){
        int endpos=(pos-count);
        endpos=(endpos<0)?0:endpos;
        List<BunItem> retlist=new ArrayList<>();
        for(int index=pos;index>=endpos;index--){
            if (isHeaderViewPos(index)) {
                continue;
            }
            if (isFooterViewPos(index)) {
                continue;
            }
            BunItem item = mInnerAdapter.getItem(index-getHeadersCount());
            if(item!=null){
                retlist.add(item);
            }
        }
        return retlist.iterator();
    }
    public Iterator<BunItem> getDataBefroPosition(int pos, int count,Predicate<BunItem> predicate){
        List<BunItem> retlist=new ArrayList<>();
        for(int index=pos;index>=0;index--){
            if (isHeaderViewPos(index)) {
                continue;
            }
            if (isFooterViewPos(index)) {
                continue;
            }
            BunItem item = mInnerAdapter.getItem(index-getHeadersCount());
            if(item==null) {
                continue;
            }
            try {
                boolean bres= predicate.test(item);
                if(bres){
                    retlist.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(retlist.size()>=count){
                break;
            }
        }
        return retlist.iterator();
    }

    public List<BunItem> getDataAfterPostion(int pos,int count){
        List<BunItem> retlist=new ArrayList<>();
        if(pos<0 || pos>=getItemCount()){
            return null;
        }
        int endpos=((pos+count)>getItemCount())?getItemCount():(pos+count);
        for(int index=pos;index<endpos;index++){
            if (isHeaderViewPos(index)) {
                continue;
            }
            if (isFooterViewPos(index)) {
                continue;
            }
            BunItem item = mInnerAdapter.getItem(pos-getHeadersCount());
            if(item!=null){
                retlist.add(item);
            }
        }
        return retlist;
    }
    public BunItem getItemDataByPostion(int pos){
        if(pos<0 || pos>=getItemCount()){
            return null;
        }
        BunItem item = mInnerAdapter.getItem(pos-getHeadersCount());
        return item;
    }
    public Iterator<BunItem> getTopData(int start, int count, Predicate<BunItem> predicate) {
        if(mInnerAdapter!=null){
           return mInnerAdapter.getTopData(start,count,predicate);
        }
        return new ArrayList<BunItem>().iterator();
    }



}
