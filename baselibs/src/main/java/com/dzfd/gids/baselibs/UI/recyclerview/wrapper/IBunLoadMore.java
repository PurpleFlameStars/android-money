package com.dzfd.gids.baselibs.UI.recyclerview.wrapper;

/**
 * Created by zheng on 2019/3/14.
 */

public interface IBunLoadMore {

    enum BunLoadState {
        LOADING,FAILED,END,INIT,NOMORE;
    }
//    public static final  int STATE_LOADING=1,STATE_FAILED=2,STATE_END=3,STATE_INIT=4;
//
    void UpdataState(BunLoadState state);
}
