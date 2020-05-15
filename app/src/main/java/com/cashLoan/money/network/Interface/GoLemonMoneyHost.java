package com.cashLoan.money.network.Interface;


import com.cashLoan.money.BuildConfig;
import com.cashLoan.money.network.GoLemonHttpLoader;
import com.cashLoan.money.network.LemonRemoteData;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public class GoLemonMoneyHost {

    public static final String HTTP_SCHME_HTTP = "http://";
    public static final String HTTP_SCHME_HTTPS = "https://";
    public static final String HTTP_DOMAIN;
    public static final String HTTP_BASE_URL;
    private static volatile GoLemonMoneyHost g_insans;

    static {
        if (BuildConfig.IS_RELEASE_HOST) {
            HTTP_DOMAIN = BuildConfig.hostAddress;
            HTTP_BASE_URL = HTTP_SCHME_HTTPS + HTTP_DOMAIN;
        } else {
            HTTP_DOMAIN = BuildConfig.hostAddress;
            HTTP_BASE_URL = HTTP_SCHME_HTTP + HTTP_DOMAIN;
        }
    }

    public static GoLemonMoneyHost getIns() {
        if (g_insans == null) {
            synchronized (GoLemonMoneyHost.class) {
                if (g_insans == null) {
                    g_insans = new GoLemonMoneyHost();
                }
            }
        }
        return g_insans;
    }

    private GoLemonMoneyHost() {
        _HttpService = GoLemonHttpLoader.getIns().AddHttpService(getHttpBaseUrl(), GoLemonMoneyHost.ILemonLiveService.class);
    }

    private GoLemonMoneyHost.ILemonLiveService _HttpService;

    public interface ILemonLiveService {

        /**
         * 离开房间
         */
        @POST("live/live/leaveLiveRoom")
        @FormUrlEncoded
        Observable<LemonRemoteData> leaveLiveRoom(
                @Field("room_id") String roomId, @Field("is_tourist") int isTourist);


    }

    public String getHttpBaseUrl() {
        return HTTP_BASE_URL;
    }

    public GoLemonMoneyHost.ILemonLiveService getHttpService() {
        return _HttpService;
    }
}
