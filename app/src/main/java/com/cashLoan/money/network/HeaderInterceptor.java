package com.cashLoan.money.network;

import com.cashLoan.money.language.utils.LocalManageUtil;
import com.cashLoan.money.webview.HttpUserAgent;
import com.dzfd.gids.baselibs.utils.ContextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {

    private String TAG = "HeaderInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();
        Request.Builder builder = original.newBuilder();
//        String token="eyJpdiI6IjBsT1hZaEtqRzl1Q3ZlQk5rcmJOa0E9PSIsInZhbHVlIjoic1VtamhyWFVvSEE3Yzc0MmFQdmNaNG1zVW1qeFpmRTZoN0tJRmlnaG0wa3I5ekoyanZiT1RwMzJ1VTNpamYzSEJ3MlFDOTAzbXpsS1psQ0NXaDl5aVljdHRvMVBCTXlNZXdpVXRtaFN2ZURJXC81alFySyt0blYwVFFFTTR6bTZZK3pMMUpPWndGWUo5UGZDdUtmVFVxa1lUeng1cVJSTGpSaXJId1cxa2JuazZpRW1tU3V4bkF4UElcL2c5MDVmQk03VXY3YzhwZzNvb3B5VXkxYUJuZ3h0c0ZmMWVPZm53MUZVZ1pyelRWa0pRcnlFNERjU0wwZlZkVU10Z0ZcL1wvSENvSDg3Y1k2T2E1dVJnajMzRDhrU212QkdHblJvSzlLVkVHQUU1M0l1dmFXdW5ZOU9cLzUyRmZHK2xFY3BHY0xIb0tTN1RVNldSZmtSVnRNNVlvaTEyOTE1dFp1UDBNdjViWnBMSjl1eEdFYVwvTU1FM1V3WkdublcxK3ZWZVwveGZSdSIsIm1hYyI6ImI1NTNjY2RhYWZhOTRmNzdjNWY0ZGFjZGEzYTE2MGYwMmIzMmUzZTZlMWExODYzYmQ1ZWU5NGJhMWY5NDFkNmIifQ==";
//        LogUtils.e("vip", "token="+User.getInstance().getAuthToken());
        String token=getNotNullHeader("");
        builder.header("QnmAuth", token);
        builder.header("lang", getNotNullHeader(LocalManageUtil.getCurrentLanguage(ContextUtils.getAppContext())));
        builder.header("User-Agent", HttpUserAgent.getUserAgent());

        Request request = builder.method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }

    private String getNotNullHeader(Object object) {
        if (object == null) {
            return "";
        } else {
            return object.toString();
        }
    }
}
