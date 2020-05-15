package com.dzfd.gids.baselibs.UI.recyclerview;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.listener.ListUpdataListener;

/**
 * Created by zheng on 2019/3/6.
 */

public interface BunDataLoader {
    //LOAD_POSTION_UP=-1,LOAD_POSTION_NONE=0,POSTION_BOTTOM_LOADMORE=1;
    void onLoadData(int  loadposition,ListUpdataListener<BunItem> listener);

}
