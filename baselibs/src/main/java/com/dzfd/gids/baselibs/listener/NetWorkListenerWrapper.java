package com.dzfd.gids.baselibs.listener;

/**
 * Created by zheng on 2019/3/13.
 */

public class NetWorkListenerWrapper<T extends IRemoteData> implements NetWorkListener<T> {
    private NetWorkListener<T> _listener;

    public NetWorkListenerWrapper() {

    }

    public NetWorkListenerWrapper(NetWorkListener callback) {
        _listener = callback;
    }

    @Override
    public boolean CanLoad(RequestCell cell) {
        if (_listener != null) {
            return _listener.CanLoad(cell);
        }
        return false;
    }

    @Override
    public void onLoading(RequestCell cell) {
        if (_listener != null) {
            _listener.onLoading(cell);
        }
        if(_listener instanceof NetWorkListenerImpl){
            return;
        }
        if (cell != null) {
            cell.DoRequstReport();
        }
    }

    @Override
    public void onFailed(RequestCell cell, int code, String msg) {
        if (_listener != null) {
            _listener.onFailed(cell, code, msg);
        }
        if(_listener instanceof NetWorkListenerImpl){
            return;
        }
        if (cell != null) {
            cell.DoResultReport(false, code, msg);
        }
    }

    @Override
    public void OnSucess(T Data, RequestCell cell) {
        if (_listener != null) {
            _listener.OnSucess(Data,cell);
        }
        if(_listener instanceof NetWorkListenerImpl){
            return;
        }
        if (cell != null) {
            cell.DoResultReport(true, 0, "");
        }
    }

    @Override
    public void OnLastPage(RequestCell cell) {
        if (_listener != null) {
            _listener.OnLastPage(cell);
        }
        if(_listener instanceof NetWorkListenerImpl){
            return;
        }
        if (cell != null) {
            cell.DoLastPageReport();
        }
    }

    @Override
    public void onComplete() {
        if (_listener != null) {
            _listener = null;
        }
    }

}
