package com.dzfd.gids.baselibs.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.R;
import com.dzfd.gids.baselibs.UI.listview.BunListView;
import com.dzfd.gids.baselibs.UI.listview.HolderFactory;
import com.dzfd.gids.baselibs.listener.ListUpdataListener;

/**
 * Created by zheng on 2019/2/25.
 */

public abstract class BunListFragment extends Fragment implements BunListView.BunlistViewListener {

    protected BunListView mcotentList;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {

        super.onDetach();

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int layoutid=getLayoutid();
        if(layoutid<=0){
            return null;
        }
        View mroot= inflater.inflate(layoutid, container, false);
        if (mroot == null) {
            return null;
        }
        mcotentList=mroot.findViewById(R.id.content_listview);
        mcotentList.setListener(this);
        HolderFactory factory = getGetListItemFactory();
        if(factory!=null){
            mcotentList.setHolderFactory(factory);
        }
        return mroot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

   //=====================================================================
    @Override
    public void onRetry() {

    }

    @Override
    public void onLoadData(boolean bottom) {

    }

    @Override
    public void onLoadEnd() {

    }
    //=================================================
    public ListUpdataListener<BunItem> getUpdataListener(){
        return mcotentList;
    }
    public int getLayoutid(){
        return R.layout.fragment_bunlist;
    }
    public abstract HolderFactory getGetListItemFactory();
}
