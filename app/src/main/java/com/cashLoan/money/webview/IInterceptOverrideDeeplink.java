package com.cashLoan.money.webview;


public interface IInterceptOverrideDeeplink {

    boolean intercept(String loadUrl);

    void onOpenSuccess();

    void onOpenFail();

}
