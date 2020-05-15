package com.cashLoan.money.upload.request;

import com.cashLoan.money.network.Interface.GoLemonLoginHost;
import com.cashLoan.money.upload.model.UploadTokenRemoteData;

import io.reactivex.Observable;

public class PicUploadTokenRequest extends AbstractUploadTokenRequest {

    @Override
    public Observable<UploadTokenRemoteData> getExactlyObservable(String type) {
        GoLemonLoginHost.IGoLemonLoginService service = GoLemonLoginHost.getIns().getHttpService();
        if (service == null) {
            return null;
        }
        return service.getPicUploadToken(type);
    }
}
