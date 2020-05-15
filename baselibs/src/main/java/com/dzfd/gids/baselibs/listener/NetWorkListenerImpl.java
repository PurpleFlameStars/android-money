package com.dzfd.gids.baselibs.listener;

/**
 * Created by zheng on 2019/1/28.
 */

public class NetWorkListenerImpl<T> implements NetWorkListener<T>{

    @Override
    public boolean CanLoad(RequestCell cell) {
        return true;
    }

    @Override
    public void onLoading(RequestCell cell) {
        if(cell!=null){
            cell.DoRequstReport();
        }
    }

    @Override
    public void OnSucess(T Data, RequestCell cell) {
        if(cell!=null){
            cell.DoResultReport(true,0,"");
        }
    }

    @Override
    public void onFailed(RequestCell cell, int code, String msg) {
        if(cell!=null){
            cell.DoResultReport(false,code,msg);
        }
    }

    @Override
    public void OnLastPage(RequestCell cell) {
        if(cell!=null){
            cell.DoLastPageReport();
        }
    }

    @Override
    public void onComplete() {

    }
}
