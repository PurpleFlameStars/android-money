package com.cashLoan.money.upload.request;

import com.cashLoan.money.network.GolemonRequest;
import com.cashLoan.money.upload.model.UploadTokenRemoteData;
import com.dzfd.gids.baselibs.listener.NetWorkListener;

import io.reactivex.Observable;

public abstract class AbstractUploadTokenRequest extends GolemonRequest<UploadTokenRemoteData> {

    @Override
    protected void OnDataReady(UploadTokenRemoteData data) {

    }
    public abstract Observable<UploadTokenRemoteData> getExactlyObservable(String type);
    public void getToken(String type, NetWorkListener<UploadTokenRemoteData> listener) {
        DoRequest(getExactlyObservable(type), listener);
    }
}
