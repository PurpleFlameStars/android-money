package com.cashLoan.money.network;

import com.cashLoan.money.network.Interface.GoLemonMoneyHost;
import com.dzfd.gids.baselibs.listener.IRemoteData;
import com.dzfd.gids.baselibs.listener.NetWorkListener;
import com.dzfd.gids.baselibs.listener.RequestCell;
import com.dzfd.gids.baselibs.network.NetWorkObserver;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zheng on 2019/3/15.
 */
@Deprecated //Deprecated by zheng
public abstract class GolemonRequest<T extends IRemoteData> {

    private T mLastData;
    public void DoRequest(Observable<T> observable, final RequestCell cell, NetWorkListener<T> Callback){
        if(observable!=null){
            observable.subscribeOn(Schedulers.io())
                    .subscribe(new NetWorkObserver(Callback,cell){
                        @Override
                        public void onNext(IRemoteData iRemoteData) {
                            mLastData=(T)iRemoteData;
                            if(mLastData!=null && mLastData.dataIsValid()){
                                OnDataReady(mLastData);
                            }
                            super.onNext(iRemoteData);
                        }
                    });
        }
    }
    public boolean DoRequest(Observable<T> observable, NetWorkListener<T> listener){
        GoLemonMoneyHost.ILemonLiveService service= GoLemonMoneyHost.getIns().getHttpService();
        if(service==null){
            return false;
        }
        RequestCell cell=new RequestCell();
        DoRequest(observable,cell,listener);
        return true;
    }
    public T getmLastData(){return mLastData;}
    protected abstract void OnDataReady(T data);


}
