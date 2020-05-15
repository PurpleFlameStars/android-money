package com.dzfd.gids.baselibs.UI.listview;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.UI.listview.holder.ListHolder;

/**
 * Created by zheng on 2018/12/25.
 */

public class BunListAdpter extends ArrayAdapter<BunItem> {
    private HolderFactory _factory;

    public BunListAdpter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public void setHolderFactory( HolderFactory factory){
        _factory=factory;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BunItem item = getItem(position);
        if(item == null){
            return null;
        }

        if(_factory == null){
            return null;
        }
        ListHolder holder=_factory.getItemHolder(item);
        if(holder == null){
            return null;
        }
        int layoutid=holder.getLayoutId();
        if(layoutid <=0){
            return null;
        }
        View root= LayoutInflater.from(getContext()).inflate(layoutid,parent,false);
        if(root!=null){
            return holder.InitHolder(root,item,position);
        }
        return null;
    }

}
