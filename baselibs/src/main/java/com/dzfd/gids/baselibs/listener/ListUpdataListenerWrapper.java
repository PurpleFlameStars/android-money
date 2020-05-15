package com.dzfd.gids.baselibs.listener;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.network.HttpLoader;

public class ListUpdataListenerWrapper<T extends BunItem> implements ListUpdataListener<T> {
    public ListUpdataListener _innner;
    public ListUpdataListenerWrapper(ListUpdataListener listener){
        _innner=listener;
    }
    @Override
    public boolean CanLoad(HttpLoader.HttpLoadPos postion) {
        if(_innner!=null){
            return _innner.CanLoad(postion);
        }
        return false;
    }

    @Override
    public void onLoading(HttpLoader.HttpLoadPos postion) {
        if(_innner!=null){
            _innner.onLoading(postion);
        }
    }

    @Override
    public void OnSucess(ListResult<T> Data, HttpLoader.HttpLoadPos postion) {
        if(_innner!=null){
            _innner.OnSucess(Data,postion);
        }
    }

    @Override
    public void onFailed(int code, String msg, HttpLoader.HttpLoadPos postion) {
        if(_innner!=null){
            _innner. onFailed(code,msg,postion);
        }
    }

    @Override
    public void OnLastPage() {
        if(_innner!=null){
            _innner. OnLastPage();
        }
    }
}
