package com.dzfd.gids.baselibs.UI.recyclerview.lisenter;

import android.view.View;

import com.GoLemon.supplier.BunItem;

import androidx.annotation.NonNull;

public interface IBunViewHolder {
    View InitView(@NonNull View root, @NonNull BunItem data);
    void OnRootViewClick(View mroot);
}
