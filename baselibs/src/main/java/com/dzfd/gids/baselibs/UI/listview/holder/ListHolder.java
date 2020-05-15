package com.dzfd.gids.baselibs.UI.listview.holder;

import androidx.annotation.NonNull;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.listener.HostFragmentListener;
/**
 * Created by zheng on 2018/12/14.
 */

public abstract class ListHolder implements View.OnClickListener {
    private final SparseArray<View> mViews;

    protected View mroot;
    protected BunItem mCoreData;
    private long lastclickTime=0;
    protected HostFragmentListener FragmentListener;

    public ListHolder(){
        mViews=new SparseArray<>();
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
    public void setHostFragmentCallback(HostFragmentListener callback){
        FragmentListener=callback;
    }

    public View InitHolder(View root, BunItem data, int position){
        if(root==null || data==null){
            return null;
        }
        mroot=root;
        mCoreData=data;
        mroot.setTag(R.id.video_list_item_postion,position);
        mroot.setOnClickListener(this);
        return InitView(root,data);
    }
    protected View getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null || mroot!=null) {
            view = mroot.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return view;
    }
    public ListHolder setText(int viewId, CharSequence text) {
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
        if(v ==mroot){
            OnRootViewClick(mroot);
        }
    }
    abstract public View InitView(@NonNull View root, @NonNull BunItem data);
    abstract public int getLayoutId();
    abstract  public void OnRootViewClick(View mroot);

}
