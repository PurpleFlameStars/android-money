package com.cashLoan.money;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.cashLoan.money.ui.activity.OcrActivity;
import com.cashLoan.money.ui.activity.OcrNextActivity;
import com.cashLoan.money.ui.bean.DetectorInfo;
import com.cashLoan.money.utils.MoneyConfig;
import com.cashLoan.money.webview.LiveWebViewActivity;
import com.cashLoan.money.webview.WebViewActivity;


public class JumpHelper {

    /**
     * 使用kfc
     * @param context
     */
    public static void jumpOcrActivity(Context context) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, OcrActivity.class);
        checkContext(context, intent);
        context.startActivity(intent);
    }

    public static void jumpOcrNextActivity(Context context, DetectorInfo info) {
        if (context == null) {
            return;
        }
        Intent intent = new Intent(context, OcrNextActivity.class);
        intent.putExtra(MoneyConfig.KFC_DATA,info);
        checkContext(context, intent);
        context.startActivity(intent);
    }



    public static void jumpMainActivity(Context cxt) {
        jumpMainActivity(null, cxt);
    }

    public static void jumpMainActivity(Intent intent, Context cxt) {
        if (cxt == null) {
            return;
        }
        if (intent == null) {
            intent = new Intent();
        }
        intent.setClass(cxt, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        cxt.startActivity(intent);
    }




    public static void jumpWebviewPage(Context context, String urlContent, boolean isFromNative) {
        Intent intent = new Intent(context, LiveWebViewActivity.class);
        jumpWebView(context,intent,urlContent,isFromNative);
    }

    private static void jumpWebView(Context context,Intent intent, String urlContent, boolean isFromNative){
        intent.putExtra(WebViewActivity.URL_CONTENT, urlContent);
        intent.putExtra(WebViewActivity.FROM_NATIVE, isFromNative);
        checkContext(context, intent);
        context.startActivity(intent);
    }


    public static void checkContext(Context context, Intent intent) {
        if (context == null || intent == null) {
            return;
        }
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }
}
