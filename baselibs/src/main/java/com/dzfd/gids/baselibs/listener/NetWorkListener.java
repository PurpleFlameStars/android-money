package com.dzfd.gids.baselibs.listener;

/**
 * Created by zheng on 2019/1/28.
 */

public interface NetWorkListener<T> {
    boolean CanLoad(RequestCell cell);
    void onLoading(RequestCell cell);
    void OnSucess(T Data, RequestCell cell);
    void onFailed(RequestCell cell, int code, String msg);
    void OnLastPage(RequestCell cell);
    void onComplete();
}
