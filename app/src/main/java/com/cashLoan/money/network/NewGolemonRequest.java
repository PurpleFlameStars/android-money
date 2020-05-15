package com.cashLoan.money.network;

import com.GoLemon.supplier.BunItem;
import com.dzfd.gids.baselibs.listener.IRemoteData;
import com.dzfd.gids.baselibs.listener.ListResult;
import com.dzfd.gids.baselibs.listener.ListUpdataListener;
import com.dzfd.gids.baselibs.listener.NetWorkListener;
import com.dzfd.gids.baselibs.listener.RequestCell;
import com.dzfd.gids.baselibs.network.DownloadFileObserver;
import com.dzfd.gids.baselibs.network.NetWorkObserver;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by zheng on 2019/3/15.
 */

public abstract class NewGolemonRequest {
    public static final  int ERROR_NO_LOGIN=10001;
    public static final  int ERROR_USER_SETCLASS_TAG=10003;

    public static final  int CLIENT_ERROR_NOFOLLOWER=20001;
    public static final int REMOTE_IS_EMPTY=20002;

    public static final  int ERROR_FOLLOW_INSERTCHAT=400;

    public <T extends IRemoteData> boolean DoRequest(Observable<T> observable, NetWorkListener<T> listener){
        return DoRequest(observable,null,listener,null);
    }
    public <T extends IRemoteData> boolean DoRequest(Observable<T> observable, NetWorkListener<T> listener, GolemonHttpResponse<T> response){
        return  DoRequest(observable,null,listener,response);
    }
    public <T extends IRemoteData> boolean DoRequest(Observable<T> observable, final RequestCell cell, NetWorkListener<T> Callback) {
        return  DoRequest(observable,cell,Callback,null);

    }
    public <T extends IRemoteData> boolean DoRequest(Observable<T> observable, final RequestCell cell, NetWorkListener<T> Callback, final GolemonHttpResponse<T> response){
        if(observable==null){
            return false;
        }

        observable.subscribeOn(Schedulers.io())
                .subscribe(new NetWorkObserver<T>(Callback,cell){
                    @Override
                    public void onNext(T iRemoteData) {
                        if(response!=null){
                            response.OnDataReady(iRemoteData);
                        }
                        super.onNext(iRemoteData);
                    }
                });
        return true;
    }

    public interface GolemonHttpResponse<T>{
       void OnDataReady(T data);

    }
    protected void _OnSucess(LemonRemoteData Data, RequestCell cell, ListUpdataListener<BunItem> listener){
        if(Data == null || listener == null){
            return;
        }
        ListResult<BunItem> retsult = new ListResult<>(Data.getItems(), cell.addmodel);
        listener.OnSucess(retsult, cell.loadpos);
    }
    protected void _OnSucess(LemonRemoteData Data, RequestCell cell, List<InsertBunItem> InsertItems, ListUpdataListener<BunItem> listener){
        if(listener == null){
            return;
        }
        ListResult<BunItem> retsult = new ListResult<>(Data.getItems(), cell.addmodel);
        if(InsertItems!=null && !InsertItems.isEmpty()){
            for(InsertBunItem item:InsertItems){
                item.InsertTo(retsult.data);
            }
        }
        listener.OnSucess(retsult, cell.loadpos);
    }

    //=========================================================================================
    public void DoDownLoad(Observable<ResponseBody> observable, RequestCell cell, NetWorkListener<String> listener, String filepath){
        if(observable == null){
            return;
        }
        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new DownloadFileObserver(listener,filepath,cell));
    }


}
