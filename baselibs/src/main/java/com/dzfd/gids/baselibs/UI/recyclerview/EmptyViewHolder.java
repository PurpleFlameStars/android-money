package com.dzfd.gids.baselibs.UI.recyclerview;

import androidx.annotation.NonNull;
import android.view.View;

import com.GoLemon.supplier.BunItem;

/**
 * 一个默认的空ViewHolder
 */
public class EmptyViewHolder extends BunViewHolder {
    public EmptyViewHolder(View root) {
        super(root);
    }

    @Override
    public View InitView(@NonNull View root, @NonNull BunItem data) {
        return null;
    }

    @Override
    public void OnRootViewClick(View mroot) {

    }
}
