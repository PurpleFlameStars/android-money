package com.cashLoan.money.network;

import android.os.Build;
import android.text.TextUtils;

import com.cashLoan.money.BuildConfig;
import com.dzfd.gids.baselibs.utils.LogUtils;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by zheng on 2019/3/6.
 */

public class GoLemonHttpLoader {
    private static final String TAG = "GoLemonHttpLoader";

   private GoLemonHttpLoader(){

    }
    private static volatile GoLemonHttpLoader g_insans;

    public static GoLemonHttpLoader getIns(){
        if(g_insans == null){
            synchronized (GoLemonHttpLoader.class){
                if(g_insans == null){
                    g_insans=new GoLemonHttpLoader();
                }
            }
        }
        return g_insans;
    }

    //===========================================================
    public <T> T AddHttpService(String baseurl,Class<T> cls){
        Retrofit retrofit=CreateHtRequest(baseurl);
        return retrofit.create(cls);
    }

    private static OkHttpClient okHttpClient = null;
    private static QueryInterceptor _QueryInterceptor;
    static {
        initOkHttpClient();
    }

    public static void initOkHttpClient() {
//        ProviderInstaller();//安装最新升级
        _QueryInterceptor=new QueryInterceptor();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new HeaderInterceptor())
                .addInterceptor(_QueryInterceptor)
                .readTimeout(20, TimeUnit.SECONDS)
                .connectTimeout(10,TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followRedirects(true)
                .followSslRedirects(true);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    LogUtils.d(TAG, "log() called with: message = [" + message + "]");
                }
            });
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
        }
        /*if (!BuildConfig.IS_RELEASE_HOST) {
            builder.addInterceptor(new NetLoggingInterceptor());
        }*/
        okHttpClient =enableTls12OnPreLollipop(builder).build();

    }
    public static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                X509TrustManager x509TrustManager = getx509();
                if(x509TrustManager==null){
                    client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                }else{
                    client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()),x509TrustManager);
                }

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
            }
        }

        return client;
    }
    protected static X509TrustManager getx509(){
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            return null;
        }
        X509TrustManager trustManager = (X509TrustManager) trustManagers[0];
        return trustManager;
    }

    protected Retrofit createRetrofit(String baseurl){
        if(TextUtils.isEmpty(baseurl)){
            return null;
        }
        Retrofit request=new Retrofit.Builder().baseUrl(baseurl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
        return request;
    }
    protected   Retrofit CreateHtRequest(String baseurl){
        if(TextUtils.isEmpty(baseurl)){
            return null;
        }
        Retrofit request=createRetrofit(baseurl);
        if(request == null){
            return null;
        }
        return request;
    }





}
