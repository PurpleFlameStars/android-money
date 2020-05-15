package com.dzfd.gids.baselibs.UI.recyclerview;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.listener.ListResult;
import com.dzfd.gids.baselibs.network.HttpLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.reactivex.functions.Predicate;

/**
 * Created by zheng on 2019/3/5.
 */

public class BunRecycleViewAdpter extends RecyclerView.Adapter<BunViewHolder> {
    private static final int FOOTER_TYPE = 181610;

    private List<BunItem> mCoreData;
    private BunHolderFactory _factory;
    private int _RecycelId;


    public BunRecycleViewAdpter(BunHolderFactory factory,int recycleId) {
        _factory = factory;
        mCoreData = new ArrayList<>();
        _RecycelId=recycleId;

    }

    @Override
    public BunViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (_factory == null) {
            return null;
        }
        BunViewHolder holder = _factory.getHolder(parent, viewType);
        if (holder != null) {
            holder.setRecycleViewId(_RecycelId);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(BunViewHolder holder, int position) {
        if (holder == null || mCoreData == null ||
                position < 0 || position >= mCoreData.size()) {
            return;
        }

        BunItem item = mCoreData.get(position);
        if (item == null) {
            return;
        }
        holder.InitHolder(item, position);
        if(_factory!=null){
            _factory.bindHolder(holder,position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BunViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Object item = payloads.get(0);
            if (item instanceof BunItem) {
                holder.refreshSingleItem((BunItem) item);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (mCoreData == null || _factory == null) {
            return super.getItemViewType(position);
        }
        if (position < 0) {
            return super.getItemViewType(position);
        }

        BunItem item = mCoreData.get(position);
        return _factory.getViewType(item);

    }

    @Override
    public int getItemCount() {

        return (mCoreData == null) ? 0 : mCoreData.size();
    }
    public int getCount() {
        return getItemCount();
    }

    @Override
    public void onViewRecycled(@NonNull BunViewHolder holder) {
        super.onViewRecycled(holder);
        holder.onViewRecycled();
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BunViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BunViewHolder holder) {
        holder.onViewDetachedFromWindow(holder);
        super.onViewDetachedFromWindow(holder);
    }

    //=======================================================
    public void _innerAddAll(List<BunItem> items,boolean refresh) {
        if (items == null || items.size() < 1) {
            return;
        }
        synchronized (mCoreData){
            mCoreData.addAll(items);
        }
        if(refresh){
            notifyDataSetChanged();
        }
    }
    public void ClearData( boolean refresh) {
        boolean doclear=(mCoreData!=null && !mCoreData.isEmpty());
        if(!doclear){
            return;
        }
        synchronized (mCoreData){
            mCoreData.clear();
        }
        if(_factory!=null){
            _factory.unBindAllHolder();
        }
        if(refresh){
            notifyDataSetChanged();
        }
    }

    public void AddItemToTop(ListResult<BunItem> result, List<BunItem> items) {
        HttpLoader.ItemInsertMode model = result.Addmodel;

        if (items == null || items.size() < 1) {
            return;
        }
        List<BunItem> CoreDataBack=new ArrayList<>();
        CoreDataBack.addAll(mCoreData);
        List<BunItem> resultitems = new ArrayList<>();
        if (model == HttpLoader.ItemInsertMode.REPLACE_MOEE) {
            resultitems.addAll(items);
        } else if(model == HttpLoader.ItemInsertMode.REPLACE_HOLDER_MODEL){
            resultitems.addAll(items);
            int holdcount=result.arg1;
            if(holdcount>0){
                int maxindex=(holdcount<CoreDataBack.size())?holdcount:CoreDataBack.size();
                for(int index=0;index<maxindex;index++){
                    resultitems.add(CoreDataBack.get(index));
                }
            }

        }else if(model==HttpLoader.ItemInsertMode.INSERT_AND_REPLACE){
            for(int index=0;index<result.arg1;index++){
                resultitems.add(CoreDataBack.get(index));
            }
            resultitems.addAll(items);
        }else {
            int topoffset = result.arg1;
            int nextaddpos = 0;
            if (topoffset > 0 && CoreDataBack.size() >= topoffset) {
                for (int index = 0; index < topoffset; index++) {
                    resultitems.add(CoreDataBack.get(index));
                }
                nextaddpos = topoffset;
            }
            resultitems.addAll(items);
            for (int index = nextaddpos; index < CoreDataBack.size(); index++) {
                resultitems.add(CoreDataBack.get(index));
            }
        }
        ClearData(false);
        _innerAddAll(resultitems,true);
    }

    public void AddItemToBottom(int bottomoffset, List<BunItem> items) {
        if (bottomoffset == 0) {
            int refstart=mCoreData.size();
            int count=items.size();
            _innerAddAll(items,false);
            notifyItemRangeInserted(refstart,count);
        } else {
            if (bottomoffset > 0 || (mCoreData.size() + bottomoffset) < 0) {
                return;
            }
            int addpos = mCoreData.size() + bottomoffset;
            synchronized (mCoreData){
                mCoreData.addAll(addpos, items);
            }
            notifyDataSetChanged();
        }
    }
    public int InsertItemToTop(int topoffset,List<BunItem> item){
        if(item == null){
            return -1;
        }
        if(topoffset<0){
            return -1;
        }
        int notifipos=topoffset;
        if(topoffset>=mCoreData.size()){
            synchronized (mCoreData){
                mCoreData.addAll(item);
                notifipos=mCoreData.size()-1;
            }
        }else{
            synchronized (mCoreData){
                mCoreData.addAll(topoffset,item);
            }
        }
        notifyItemRangeInserted(topoffset,item.size());
        return notifipos;
    }

    public boolean MoveItem(int frompos,int topos){
        if(mCoreData == null || mCoreData.size()<1){
            return false;
        }
        if(frompos<0 || frompos>=mCoreData.size()){
            return false;
        }
        if(topos<0 || topos>=mCoreData.size()){
            return false;
        }
        if(frompos>topos){
           BunItem itemfrom= mCoreData.get(frompos);
           mCoreData.remove(frompos);
           mCoreData.add(topos,itemfrom);
        }else{
            BunItem itemfrom= mCoreData.get(frompos);
            mCoreData.remove(frompos);
            mCoreData.add(topos,itemfrom);
        }
        return true;
    }
    public boolean RemoveItem(int postion){
        if(mCoreData == null || mCoreData.size()<1){
            return false;
        }
        if(postion<0 || postion>=mCoreData.size()){
            return false;
        }
        BunItem item =mCoreData.get(postion);
        mCoreData.remove(postion);
        if(_factory!=null){
            _factory.unBindHolder(item,postion);
        }
        notifyItemRemoved(postion);
        return true;
    }
    public boolean ResetItem(int postion,BunItem item){
        int itemcount=mCoreData.size();
        if(postion<0 || postion>=itemcount){
            return false;
        }
        mCoreData.set(postion,item);
        notifyItemChanged(postion);
        return true;
    }
    public BunItem getItem(int pos) {
        if(mCoreData==null || mCoreData.isEmpty()){
            return null;
        }
        int count=mCoreData.size();
        if(pos<0 || pos>=count){
            return null;
        }
        return mCoreData.get(pos);
    }
    public Iterator<BunItem> getTopData(int start, int count, Predicate<BunItem> predicate) {
        List<BunItem> retitem=new ArrayList<>();
        if(mCoreData == null || mCoreData.isEmpty()){
            return retitem.iterator();
        }
        if(start<0 || start>=mCoreData.size()){
            return retitem.iterator();
        }
        int checkcount=0;
        for(int index=start;index<mCoreData.size();index++){
            BunItem item=mCoreData.get(index);
            try {
                boolean bres= predicate.test(item);
                if(bres){
                    checkcount++;
                }
                retitem.add(item);
                if(checkcount == count){
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retitem.iterator();

    }
}