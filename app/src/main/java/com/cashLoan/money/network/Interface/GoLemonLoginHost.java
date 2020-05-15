package com.cashLoan.money.network.Interface;

import com.cashLoan.money.BuildConfig;
import com.cashLoan.money.network.GoLemonHttpLoader;
import com.cashLoan.money.upload.model.UploadLogTokenData;
import com.cashLoan.money.upload.model.UploadTokenRemoteData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by zheng on 2019/3/18.
 */

public class GoLemonLoginHost {


    public static final String HTTP_BASE_URL;
    private static volatile GoLemonLoginHost g_insans;
    public static final String HTTP_DOMAIN;

    static {

        if (BuildConfig.IS_RELEASE_HOST) {
            HTTP_DOMAIN = BuildConfig.hostAddress;
            HTTP_BASE_URL = GoLemonMoneyHost.HTTP_SCHME_HTTPS + HTTP_DOMAIN;
        } else {
            HTTP_DOMAIN = BuildConfig.hostAddress;
            HTTP_BASE_URL = GoLemonMoneyHost.HTTP_SCHME_HTTP + HTTP_DOMAIN;
        }
    }

    public static GoLemonLoginHost getIns() {
        if (g_insans == null) {
            synchronized (GoLemonLoginHost.class) {
                if (g_insans == null) {
                    g_insans = new GoLemonLoginHost();
                }
            }
        }
        return g_insans;
    }

    private GoLemonLoginHost() {
        _HttpService = GoLemonHttpLoader.getIns().AddHttpService(getHttpBaseUrl(), IGoLemonLoginService.class);
    }

    private IGoLemonLoginService _HttpService;

    public interface IGoLemonLoginService {

        @GET("user/assets/token/avatar")
        Observable<UploadTokenRemoteData> getHeadIconUploadToken();

        @GET("user/assets/token/image")
        Observable<UploadTokenRemoteData> getPicUploadToken(@Query("type") String type);

        @GET("user/assets/token/video")
        Observable<UploadTokenRemoteData> getVideoUploadToken(@Query("type") String type);



        @GET("log/token/imlog")
        Observable<UploadLogTokenData> getLogUploadToken();
    }

    public String getHttpBaseUrl() {
        return HTTP_BASE_URL;
    }

    public IGoLemonLoginService getHttpService() {
        return _HttpService;
    }


}
