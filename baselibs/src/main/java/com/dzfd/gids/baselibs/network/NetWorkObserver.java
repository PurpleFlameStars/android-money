package com.dzfd.gids.baselibs.network;


import android.content.Context;

import com.dzfd.gids.baselibs.listener.IRemoteData;
import com.dzfd.gids.baselibs.listener.NetWorkListener;
import com.dzfd.gids.baselibs.listener.RequestCell;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;


public class NetWorkObserver<T extends IRemoteData> implements Observer<T> {

    private NetWorkListener<T> mcallback;
    private T data;
    private RequestCell mcell;

    public NetWorkObserver(NetWorkListener<T> calback, RequestCell cell){
        mcallback=calback;
        mcell=cell;
    }
    @Override
    public void onSubscribe(Disposable d) {
        if(mcallback!=null){
            mcallback.onLoading(mcell);
        }
    }

    @Override
    public void onNext(T t) {
        this.data=t;
        if(this.data == null ){
            if(mcallback !=null){
                mcallback.onFailed(mcell,124,"server not return data");
            }
        } else if(!this.data.dataIsValid()){
            if(mcallback !=null){
                mcallback.onFailed(mcell,data.getErrCode(),data.getErrMsg());
            }
        } else{
            if(mcallback !=null){
                mcallback.OnSucess(t,mcell);
            }
        }
        if(t.isLastPage() && mcallback!=null){
            mcallback.OnLastPage(mcell);
        }
        Context cxt=(mcell!=null)?mcell.getContext():null;
        Runnable runnable=(mcell!=null)?mcell.getRunable():null;
        t.doTaskAward(cxt,runnable);
    }

    @Override
    public void onError(Throwable e) {

       int errcode=0;
        String errmsg="";
        if(e instanceof HttpException){
            HttpException exception=(HttpException)e;
            errcode=exception.response().code();
            errmsg=exception.response().message();
        }else if(e instanceof Exception){
            Exception exception=(Exception)e;
            errcode=20;
            errmsg=exception.getMessage();
        } else if(data!=null){
            errcode=data.getErrCode();
            errmsg=data.getErrMsg();
        }
        if(mcallback !=null){
            mcallback.onFailed(mcell,errcode,errmsg);
        }
    }

    @Override
    public void onComplete() {
        if(mcallback!=null){
            mcallback.onComplete();
            mcallback=null;
        }
    }



}
