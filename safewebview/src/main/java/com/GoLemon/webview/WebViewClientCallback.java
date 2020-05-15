package com.GoLemon.webview;

import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

public interface WebViewClientCallback {
    boolean shouldOverrideUrlLoading(WebView view, String url);

    void onReceivedError(WebView view, int errorCode, String description, String failingUrl);

    void onPageFinished(WebView view, String url);

    WebResourceResponse shouldInterceptRequest(WebView view, String url);
    WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request);
}
