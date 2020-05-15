package com.dzfd.gids.baselibs.listener;

/**
 * Created by zheng on 2019/3/13.
 */

public   class NetWorkListenerAdapter<T extends IRemoteData> implements NetWorkListener<T> {
    private ListUpdataListener _listener;

    public NetWorkListenerAdapter() {

    }

    public NetWorkListenerAdapter(ListUpdataListener callback){
        _listener=callback;
    }
    @Override
    public boolean CanLoad(RequestCell cell) {
        if(_listener!=null){
            return _listener.CanLoad(cell.loadpos);
        }
        return false;
    }

    @Override
    public void onLoading(RequestCell cell) {
        if(_listener!=null){
            _listener.onLoading(cell.loadpos);
        }
        if(cell!=null){
            cell.DoRequstReport();
        }
    }

    @Override
    public void onFailed(RequestCell cell, int code, String msg) {
        if(_listener!=null){
            _listener.onFailed(code,msg,cell.loadpos);
        }
        if(cell!=null){
            cell.DoResultReport(false,code,msg);
        }
    }

    @Override
    public void OnSucess(T Data, RequestCell cell) {
        if(cell!=null){
            cell.DoResultReport(true,0,"");
        }
    }

    @Override
    public void OnLastPage(RequestCell cell) {
        if(_listener!=null){
            _listener.OnLastPage();
        }
        if(cell!=null){
            cell.DoLastPageReport();
        }
    }

    @Override
    public void onComplete() {
        if(_listener!=null){
            _listener=null;
        }
    }

}
