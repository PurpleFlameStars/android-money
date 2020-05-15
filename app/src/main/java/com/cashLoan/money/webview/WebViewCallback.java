package com.cashLoan.money.webview;


public interface WebViewCallback {
    /**
     * JS对调接口
     * @return  根据具体函数而定
     * param    参数1
     * param2   参数2
     * reserve  保留参数
     */
    Object onJSCall(String function, String param, String param2, Object reserve);
}
