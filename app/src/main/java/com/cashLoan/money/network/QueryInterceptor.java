package com.cashLoan.money.network;

import android.text.TextUtils;

import com.cashLoan.money.BuildConfig;
import com.dzfd.gids.baselibs.network.NetUtils;
import com.dzfd.gids.baselibs.stat.StatEntity;
import com.dzfd.gids.baselibs.stat.StatHelper;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class QueryInterceptor implements Interceptor {

    private String TAG = "QueryInterceptor";
    private static long start = 5211314;
    private static long end = 69969966;
    private Map<String,String> _Querys=new HashMap<>();
    public QueryInterceptor(){
        if(_Querys== null){
            _Querys=new HashMap<>();
        }
        _Querys.put("m2", NetUtils.getIns().getUid());
        _Querys.put("v", NetUtils.getIns().getAppVertion());
        _Querys.put("os", NetUtils.getIns().getOs());
        _Querys.put("md", NetUtils.getIns().getModel());
        _Querys.put("mf", NetUtils.getIns().getMf());
        _Querys.put("channel", NetUtils.getIns().getChannel());
        _Querys.put("network", NetUtils.isWiFI(true) ? "wifi" : "mobile");
        _Querys.put("vc", BuildConfig.VERSION_CODE + "");
        TimeZone zone= TimeZone.getDefault();
        String zoneid=zone.getID();
        if(zoneid==null){
            zoneid="";
        }
        _Querys.put("tz",zoneid);


    }

    public static String getSign(Map<String, String> params) {

        Map<String, String> treeMap = new TreeMap<>(
                (obj1, obj2) -> obj1.compareTo(obj2));

        if (params != null) {
            treeMap.putAll(params);
        }

        StringBuffer buffer = new StringBuffer(String.valueOf(start));
        //params不能含有value=null的值
        for (String key : treeMap.keySet()) {
            Object value = treeMap.get(key);
            if (value == null) {
                treeMap.put(key, "");
                value = "";
            }
            buffer.append(key + "=" + value.toString());
        }
        buffer.append(end);
        String beformd5=buffer.toString();
        String m = md5(beformd5);
        return m;
    }


    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes());
            byte[] res = md.digest();
            return byte2hex(res);
        } catch (Exception e) {
            return null;
        }
    }

    public static String byte2hex(byte[] data) {
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < data.length; i++) {
            int high = (data[i] >> 4) & 0x0F;
            int low = data[i] & 0x0F;

            sb.append(Integer.toHexString(high));
            sb.append(Integer.toHexString(low));
        }

        return sb.toString();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {


        //获取到request
        Request request = chain.request();

        //获取到方法
        HttpUrl httpUrlurl = request.url();

        HashMap<String, String> rootMap = new HashMap<>();
        rootMap.putAll(_Querys);
        long tms=System.currentTimeMillis();
        rootMap.put("tm",String.valueOf(tms));
        //通过请求地址(最初始的请求地址)获取到参数列表
        Set<String> parameterNames = httpUrlurl.queryParameterNames();
        for (String key : parameterNames) {  //循环参数列表
            if(TextUtils.isEmpty(key)){
                continue;
            }
            if(key.compareToIgnoreCase("sign") == 0){
                continue;
            }
            if(key.compareToIgnoreCase("tm") == 0){
                continue;
            }
            String value=httpUrlurl.queryParameter(key);
            if(value==null){
                value="";
            }
            rootMap.put(key, value);
        }//
        String sign=getSign(rootMap);
        rootMap.put("sign",sign);

        String url = httpUrlurl.toString();
        int index = url.indexOf("?");
        if (index > 0) {
            url = url.substring(0, index);
        }
        StringBuffer sb=new StringBuffer(url);
        boolean isFrist=true;
        for(String key:rootMap.keySet()){
            if(isFrist){
                sb.append("?");
                isFrist=false;
            }else{
                sb.append("&");
            }
            sb.append(key);
            sb.append("=");
            sb.append(rootMap.get(key));
        }
        String aftesign=sb.toString();

        request = request.newBuilder().url(aftesign).build();  //重新构建请求

        String m2 = request.url().queryParameter("m2");
        if (TextUtils.isEmpty(m2)) {
            StatEntity entity = new StatEntity(url, request.url().toString());
            StatHelper.onEvent("m2_empty", entity);
        }

        return chain.proceed(request);
    }

}
