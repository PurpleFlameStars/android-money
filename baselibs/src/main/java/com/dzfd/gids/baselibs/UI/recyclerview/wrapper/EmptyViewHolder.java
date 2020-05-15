package com.dzfd.gids.baselibs.UI.recyclerview.wrapper;

import androidx.annotation.NonNull;
import android.view.View;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.UI.recyclerview.BunViewHolder;

/**
 * Created by zheng on 2019/3/8.
 */

public class EmptyViewHolder extends BunViewHolder {

    public EmptyViewHolder(View root) {
        super(root);
    }

    @Override
    public View InitView(@NonNull View root, @NonNull BunItem data) {
        return root;
    }

    @Override
    public void OnRootViewClick(View mroot) {

    }
}
