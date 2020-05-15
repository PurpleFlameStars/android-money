package com.dzfd.gids.baselibs.UI.listview;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.UI.listview.holder.ListHolder;

/**
 * Created by zheng on 2019/2/25.
 */

public interface HolderFactory {
    ListHolder getItemHolder(BunItem item);
}
