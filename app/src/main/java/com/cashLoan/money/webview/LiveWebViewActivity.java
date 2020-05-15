package com.cashLoan.money.webview;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.Nullable;


import com.cashLoan.money.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 客户端默认Webview页面
 */
public class LiveWebViewActivity extends WebViewActivity {
    public static final String URL_CONTENT = "url_content";
    public static final String FROM_NATIVE = "from_native";
    public static final String FILL_CODE = HTTP_DOMAIN + "/fillCode";
    public static final String INVITE = HTTP_DOMAIN + "/invite?tab=";

    public static final String RANKING = HTTP_DOMAIN + "ranking";
    public static final String ROOM_RANKING = HTTP_DOMAIN + "moneyRank";

    public static final String FAQ = HTTP_DOMAIN + "faq?lang=";
    public static final String PROTOCOL = HTTP_DOMAIN + "protocol?lang=";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public String getRequestUrl() {
        String requestUrl;
        if (TextUtils.equals(getIntent().getAction(), getString(R.string.action_webview))) {
            getIntent().putExtra(FROM_NATIVE, true);
            Uri uri=getIntent().getData();
            if(uri==null){
                return "";
            }
            requestUrl = uri.getQueryParameter("url");
            if(TextUtils.isEmpty(requestUrl)){
                String base64url=uri.getQueryParameter("base64url");
                /*if(!TextUtils.isEmpty(base64url)){
                    requestUrl= CompressUtils.decryptBASE64(base64url);
                }*/
            }
        } else {
            requestUrl = getIntent().getStringExtra(URL_CONTENT);
        }
        if(TextUtils.isEmpty(requestUrl)){
            return "";
        }
        /*if (getIntent().getBooleanExtra(FROM_NATIVE, false) && requestUrl.contains(HTTP_DOMAIN)) {
            getIntent().putExtra(HIDE_TITLE, true);
        }*/
        return requestUrl;
    }

    @Override
    public boolean isFromNative() {
        return getIntent().getBooleanExtra(FROM_NATIVE, false);
    }

}
