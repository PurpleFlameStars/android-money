package com.dzfd.gids.baselibs.listener;

import com.dzfd.gids.baselibs.network.HttpLoader;

/**
 * Created by zheng on 2019/1/28.
 */

public interface ListUpdataListener<T> {
    boolean CanLoad(HttpLoader.HttpLoadPos postion);
    void onLoading(HttpLoader.HttpLoadPos postion);
    void OnSucess(ListResult<T> Data, HttpLoader.HttpLoadPos postion);//-1/up,0/none,1/bottom
    void onFailed(int code, String msg,HttpLoader.HttpLoadPos postion);
    void OnLastPage();
}
