package com.cashLoan.money.webview;

import android.content.Context;

/**
 * WebView通用JS方法
 */
public class JavascriptInterface {
    private static final String TAG = "JavascriptInterface";
    protected Context mContext;
    protected WebViewCallback mCallback;
    private WebViewWrapper mWebView;
    public boolean interceptKeyDown;
    public boolean hasBackKeyDownListenner;

    public JavascriptInterface(Context context, WebViewWrapper webView) {
        mContext = context;
        mWebView = webView;
    }

    public void setWebView(WebViewWrapper webView) {
        mWebView = webView;
    }

    public void setWebViewCallback(WebViewCallback cb) {
        mCallback = cb;
    }

    public void destroy() {
        if (mCallback != null) {
            mCallback = null;
        }
        if (mWebView != null) {
            mWebView = null;
        }
        if (mContext != null) {
            mContext = null;
        }
    }

}
