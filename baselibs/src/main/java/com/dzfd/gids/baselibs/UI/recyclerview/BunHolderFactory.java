package com.dzfd.gids.baselibs.UI.recyclerview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.GoLemon.supplier.BunItem;

/**
 * Created by zheng on 2019/3/5.
 */

public interface  BunHolderFactory {
   int getViewType(BunItem item);
   View getItemView(Context cxt, ViewGroup parent, int type);
   BunViewHolder getHolder(ViewGroup parent,int type);

   void bindHolder(BunViewHolder holder, int postion);
   void unBindHolder(BunItem item,int postion);
   void unBindAllHolder();
}
