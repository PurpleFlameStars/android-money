package com.dzfd.gids.baselibs.UI.recyclerview;

import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.recyclerview.lisenter.IBunViewHolder;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by zheng on 2019/3/5.
 */

public abstract class BunViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, IBunViewHolder {
    protected BunItem mCoreData;
    private long lastclickTime=0;
    private final SparseArray<View> mViews;
    protected int _HolderPostion;
    protected HolderUpdataListener _HolderListener;
    protected int _RecyclerId=-1;

    public BunViewHolder(View root) {
        super(root);
        mViews=new SparseArray<>();
    }
    public BunViewHolder(View root,HolderUpdataListener listener){
        this(root);
        _HolderListener=listener;
    }
    public void setRecycleViewId(int rid){
        _RecyclerId=rid;
    }
    protected boolean canFireClick(){
        long curtime= System.currentTimeMillis();
        long offset=curtime-lastclickTime;
        if(offset<1000){
            return false;
        }
        lastclickTime=curtime;
        return true;
    }
    public View InitHolder(BunItem data, int position){
        if(!InitHolderData(data,position)){
            return null;
        }
        return InitView(this.itemView,data);
    }
    public void UpdataData(BunItem data){
        if(_HolderListener!=null){
            _HolderListener.UpdataItem(_HolderPostion,data);
        }
    }
    public void InsertItem(int postion,BunItem item){
        if(_HolderListener!=null){
            _HolderListener.InsertItem(postion,item);
        }
    }
    public boolean InitHolderData(BunItem data, int position){
        if(this.itemView==null || data==null){
            return false;
        }
        mCoreData=data;
        _HolderPostion=position;
        this.itemView.setTag(R.id.video_list_item_postion,position);
        this.itemView.setOnClickListener(this);
        return true;
    }
    protected View getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null || this.itemView!=null) {
            view = this.itemView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }
    protected int getViewPostion(){
        Object object = this.itemView.getTag(R.id.video_list_item_postion);
        if(object!=null && (object instanceof Integer)){
            return (Integer)object;
        }
        return -1;
    }
    public BunViewHolder setText(int viewId, CharSequence text) {
        TextView view = (TextView) getView(viewId);
        if(view !=null){
            view.setText(text);
        }
        return this;
    }
    @Override
    public void onClick(View v) {
        if(v == null){
            return;
        }
        if(v ==this.itemView){
            boolean bres=canFireClick();
            if(bres){
                OnRootViewClick(this.itemView);
            }
        }
    }
    public void onViewRecycled(){

    }

    public void onViewAttachedToWindow(BunViewHolder holder){
    }

    public void onViewDetachedFromWindow(BunViewHolder holder) {
    }


    public BunItem getBindData(){
        return mCoreData;
    }

    public void refreshSingleItem(BunItem data){ }

    public interface HolderUpdataListener{
        void UpdataItem(int postion,BunItem item);
        void InsertItem(int postion,BunItem item);
        void InsertItem(int postion, List<BunItem> items);
        boolean RemoveItem(int postion);
    }
}
