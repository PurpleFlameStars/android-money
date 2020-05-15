package com.cashLoan.money.upload.request;

import com.cashLoan.money.network.GolemonRequest;
import com.cashLoan.money.network.Interface.GoLemonLoginHost;
import com.cashLoan.money.upload.model.UploadLogTokenData;
import com.dzfd.gids.baselibs.listener.NetWorkListener;

public class LogUploadTokenRequest extends GolemonRequest<UploadLogTokenData> {

    @Override
    protected void OnDataReady(UploadLogTokenData data) {

    }

    public void getToken(NetWorkListener<UploadLogTokenData> listener) {
        GoLemonLoginHost.IGoLemonLoginService service = GoLemonLoginHost.getIns().getHttpService();
        if (service != null) {
            DoRequest(service.getLogUploadToken(), listener);
        }
    }
}
