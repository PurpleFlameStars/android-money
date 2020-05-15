package com.GoLemon.webview.safeWebView;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JsPromptResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.GoLemon.webview.WebViewClientCallback;

import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SafeWebView extends WebView {
    private static final String TAG = "SafeWebView";
    private Map<String, JsCallJava> mJsCallJavas;
    private Map<String, String> mInjectJavaScripts;
    private FixedOnReceivedTitle mFixedOnReceivedTitle;
    private boolean mIsInited;
    private Boolean mIsAccessibilityEnabledOriginal;
    private WebViewClientCallback mWebviewClientCallback;

    public SafeWebView(Context context) {
        this(context, null);
    }

    public SafeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        removeSearchBoxJavaBridge();

        // WebView跨源（加载本地文件）攻击分析：http://blogs.360.cn/360mobile/2014/09/22/webview%E8%B7%A8%E6%BA%90%E6%94%BB%E5%87%BB%E5%88%86%E6%9E%90/
        // 是否允许WebView使用File协议，移动版的Chrome默认禁止加载file协议的文件；
        getSettings().setAllowFileAccess(false);

        mFixedOnReceivedTitle = new FixedOnReceivedTitle();
        mIsInited = true;
    }

    /**
     * 经过大量的测试，按照以下方式才能保证JS脚本100%注入成功：
     * 1、在第一次loadUrl之前注入JS（在addJavascriptInterface里面注入即可，setWebViewClient和setWebChromeClient要在addJavascriptInterface之前执行）；
     * 2、在webViewClient.onPageStarted中都注入JS；
     * 3、在webChromeClient.onProgressChanged中都注入JS，并且不能通过自检查（onJsPrompt里面判断）JS是否注入成功来减少注入JS的次数，因为网页中的JS可以同时打开多个url导致无法控制检查的准确性；
     * 4、注入的JS中已经在脚本（./library/doc/notRepeat.js）中检查注入的对象是否已经存在，避免注入对象被重新赋值导致网页引用该对象的方法时发生异常；
     *
     * @deprecated Android4.2.2及以上版本的addJavascriptInterface方法已经解决了安全问题，如果不使用“网页能将JS函数传到Java层”功能，不建议使用该类，毕竟系统的JS注入效率才是最高的；
     */
    @SuppressLint("JavascriptInterface")
    @Override
    public void addJavascriptInterface(Object interfaceObj, String interfaceName) {
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(interfaceObj,interfaceName);
        }else{
            if (mJsCallJavas == null) {
                mJsCallJavas = new HashMap<>();
            }
            mJsCallJavas.put(interfaceName, new JsCallJava(interfaceObj, interfaceName));
            injectJavaScript();
        }

    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        mFixedOnReceivedTitle.setWebChromeClient(client);
        super.setWebChromeClient(client);
    }

    public void setWebviewClientCallback(WebViewClientCallback clientCallback) {
        this.mWebviewClientCallback = clientCallback;
    }

    @Override
    public void destroy() {
        if (mJsCallJavas != null) {
            mJsCallJavas.clear();
        }
        if (mInjectJavaScripts != null) {
            mInjectJavaScripts.clear();
        }
        removeAllViewsInLayout();
        fixedStillAttached();
        releaseConfigCallback();
        if (mIsInited) {
            resetAccessibilityEnabled();
            super.destroy();
        }
    }

    @Override
    public void clearHistory() {
        if (mIsInited) {
            super.clearHistory();
        }
    }

    @Override
    public void setOverScrollMode(int mode) {
        try {
            super.setOverScrollMode(mode);
        } catch (NullPointerException e) {
//            Caused by: java.lang.NullPointerException: Attempt to invoke virtual method
//            'android.content.pm.PackageManager android.app.Application.getPackageManager()' on a null object reference
            destroy();
        } catch (Throwable e) {
            destroy();
//
        }
    }

    @Override
    public boolean isPrivateBrowsingEnabled() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && getSettings() == null) {
            return false; // getSettings().isPrivateBrowsingEnabled()
        } else {
            return super.isPrivateBrowsingEnabled();
        }
    }

    /**
     * 添加并注入JavaScript脚本（和“addJavascriptInterface”注入对象的注入时机一致，100%能注入成功）；
     * 注意：为了做到能100%注入，需要在注入的js中自行判断对象是否已经存在（如：if (typeof(window.Android) = 'undefined')）；
     *
     * @param javaScript
     */
    public void addInjectJavaScript(String javaScript) {
        if (mInjectJavaScripts == null) {
            mInjectJavaScripts = new HashMap<String, String>();
        }
        mInjectJavaScripts.put(String.valueOf(javaScript.hashCode()), javaScript);
        injectExtraJavaScript();
    }

    private void injectJavaScript() {
        for (Map.Entry<String, JsCallJava> entry : mJsCallJavas.entrySet()) {
            this.loadUrl(buildNotRepeatInjectJS(entry.getKey(), entry.getValue().getPreloadInterfaceJS()));
        }
    }

    private void injectExtraJavaScript() {
        for (Map.Entry<String, String> entry : mInjectJavaScripts.entrySet()) {
            this.loadUrl(buildNotRepeatInjectJS(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 构建一个“不会重复注入”的js脚本；
     *
     * @param key
     * @param js
     * @return
     */
    public String buildNotRepeatInjectJS(String key, String js) {
        String obj = String.format("__injectFlag_%1$s__", key);
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:try{(function(){if(window.");
        sb.append(obj);
        sb.append("){console.log('");
        sb.append(obj);
        sb.append(" has been injected');return;}window.");
        sb.append(obj);
        sb.append("=true;");
        sb.append(js);
        sb.append("}())}catch(e){console.warn(e)}");
        return sb.toString();
    }

    /**
     * 构建一个“带try catch”的js脚本；
     *
     * @param js
     * @return
     */
    public String buildTryCatchInjectJS(String js) {
        StringBuilder sb = new StringBuilder();
        sb.append("javascript:try{");
        sb.append(js);
        sb.append("}catch(e){console.warn(e)}");
        return sb.toString();
    }

    /**
     * 如果没有使用addJavascriptInterface方法，不需要使用这个类；
     */
    public class SafeWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (mJsCallJavas != null) {
                injectJavaScript();

            }
            if (mInjectJavaScripts != null) {
                injectExtraJavaScript();
            }
            mFixedOnReceivedTitle.onPageStarted();
            fixedAccessibilityInjectorExceptionForOnPageFinished(url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            mFixedOnReceivedTitle.onPageFinished(view);
            if (view != null) {
                onReceiveTitle(view.getTitle());
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage(view.getContext().getResources().getString(R.string.ssl_error));
            builder.setPositiveButton(view.getContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                }
            });
            builder.setNegativeButton(view.getContext().getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                }
            });
            builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                        handler.cancel();
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (mWebviewClientCallback != null) {
                mWebviewClientCallback.onReceivedError(view, errorCode, description, failingUrl);
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("http:") || url.startsWith("https:")) {
//                view.loadUrl(url);
                if (mWebviewClientCallback != null) {
                    return mWebviewClientCallback.shouldOverrideUrlLoading(view, url);
                }
                return false;
            } else {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                }
                return true;
            }
        }
    }

    /**
     * 如果没有使用addJavascriptInterface方法，不需要使用这个类；
     */
    public class SafeWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (mJsCallJavas != null) {
                injectJavaScript();

            }
            if (mInjectJavaScripts != null) {
                injectExtraJavaScript();
            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
//            LogUtils.d("chromium",message);
            if (mJsCallJavas != null && JsCallJava.isSafeWebViewCallMsg(message)) {
                JSONObject jsonObject = JsCallJava.getMsgJSONObject(message);
                String interfacedName = JsCallJava.getInterfacedName(jsonObject);
                if (interfacedName != null) {
                    JsCallJava jsCallJava = mJsCallJavas.get(interfacedName);
                    if (jsCallJava != null) {
                        result.confirm(jsCallJava.call(view, jsonObject));
                    }
                }
                return true;
            } else {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            mFixedOnReceivedTitle.onReceivedTitle();
            super.onReceivedTitle(view, title);
        }
    }

    /**
     * 解决部分手机webView返回时不触发onReceivedTitle的问题（如：三星SM-G9008V 4.4.2）；
     */
    private static class FixedOnReceivedTitle {
        private WebChromeClient mWebChromeClient;
        private boolean mIsOnReceivedTitle;

        public void setWebChromeClient(WebChromeClient webChromeClient) {
            mWebChromeClient = webChromeClient;
        }

        public void onPageStarted() {
            mIsOnReceivedTitle = false;
        }

        public void onPageFinished(WebView view) {
            if (!mIsOnReceivedTitle && mWebChromeClient != null) {
//                Samsung A8000 Android5.1.1
//                java.lang.NullPointerException: Attempt to invoke virtual method 'int org.chromium.content_public.browser.NavigationHistory.getCurrentEntryIndex()' on a null object reference
//                at com.android.webview.chromium.WebBackForwardListChromium.<init>(WebBackForwardListChromium.java:28)
//                at com.android.webview.chromium.WebViewChromium.copyBackForwardList(WebViewChromium.java:1103)
//                at android.webkit.WebView.copyBackForwardList(WebView.java:1533)
//                at android.webkit.safe.SafeWebView$FixedOnReceivedTitle.onPageFinished(AppStore:295)
//                at android.webkit.safe.SafeWebView$SafeWebViewClient.onPageFinished(AppStore:227)
//                at com.qihoo.appstore.webview.AppStoreWebView$InnerWebViewClient.onPageFinished(AppStore:372)
//                at com.android.webview.chromium.WebViewContentsClientAdapter.onPageFinished(WebViewContentsClientAdapter.java:498)
//                at org.chromium.android_webview.AwContentsClientCallbackHelper$MyHandler.handleMessage(AwContentsClientCallbackHelper.java:163)
//                at android.os.Handler.dispatchMessage(Handler.java:102)
//                at android.os.Looper.loop(Looper.java:145)
//                at android.app.ActivityThread.main(ActivityThread.java:6963)
//                at java.lang.reflect.Method.invoke(Native Method)
//                at java.lang.reflect.Method.invoke(Method.java:372)
//                at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1404)
//                at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1199)
                WebBackForwardList list = null;
                try {
                    list = view.copyBackForwardList();
                } catch (NullPointerException e) {

                }
                if (list != null
                        && list.getSize() > 0
                        && list.getCurrentIndex() >= 0
                        && list.getItemAtIndex(list.getCurrentIndex()) != null) {
                    String previousTitle = list.getItemAtIndex(list.getCurrentIndex()).getTitle();
                    mWebChromeClient.onReceivedTitle(view, previousTitle);
                }
            }
        }

        public void onReceivedTitle() {
            mIsOnReceivedTitle = true;
        }
    }

    // Activity在onDestory时调用webView的destroy，可以停止播放页面中的音频
    private void fixedStillAttached() {
        // java.lang.Throwable: Error: WebView.destroy() called while still attached!
        // at android.webkit.WebViewClassic.destroy(WebViewClassic.java:4142)
        // at android.webkit.WebView.destroy(WebView.java:707)
        ViewParent parent = getParent();
        if (parent instanceof ViewGroup) { // 由于自定义webView构建时传入了该Activity的context对象，因此需要先从父容器中移除webView，然后再销毁webView；
            ViewGroup mWebViewContainer = (ViewGroup) getParent();
            mWebViewContainer.removeAllViewsInLayout();
        }
    }

    // 解决WebView内存泄漏问题；
    private void releaseConfigCallback() {
        if (Build.VERSION.SDK_INT < 16) { // JELLY_BEAN
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {

            } catch (IllegalAccessException e) {

            }
        } else if (Build.VERSION.SDK_INT < 19) { // KITKAT
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {

            } catch (ClassNotFoundException e) {

            } catch (IllegalAccessException e) {

            }
        }
    }

    /**
     * Android 4.4 KitKat 使用Chrome DevTools 远程调试WebView
     * WebView.setWebContentsDebuggingEnabled(true);
     * http://blog.csdn.net/t12x3456/article/details/14225235
     */
    @TargetApi(19)
    protected void trySetWebDebuggEnabled() {
        setWebContentsDebuggingEnabled(true);
    }

    /**
     * 解决Webview远程执行代码漏洞，避免被“getClass”方法恶意利用（在loadUrl之前调用，如：MyWebView(Context context, AttributeSet attrs)里面）；
     * 漏洞详解：http://drops.wooyun.org/papers/548
     * <p/>
     * function execute(cmdArgs)
     * {
     * for (var obj in window) {
     * if ("getClass" in window[obj]) {
     * alert(obj);
     * return ?window[obj].getClass().forName("java.lang.Runtime")
     * .getMethod("getRuntime",null).invoke(null,null).exec(cmdArgs);
     * }
     * }
     * }
     *
     * @return
     */
    @TargetApi(11)
    protected boolean removeSearchBoxJavaBridge() {
        try {
            if (Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 17) {
                Method method = this.getClass().getMethod("removeJavascriptInterface", String.class);
                method.invoke(this, "searchBoxJavaBridge_");
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 解决部分Android4.2中开启了辅助模式后，“android.webkit.AccessibilityInjector$TextToSpeechWrapper$1.onInit中synchronized(mTextToSpeech)”空指针导致的崩溃问题；
     * https://code.google.com/p/android/issues/detail?id=40944
     * <p>
     * 如：
     * Xiaomi HM NOTE 1TD Android4.2.2
     * Samsung GT-I9500 Android4.2.2
     * ZTE V967S Android4.2.1
     * Lenovo A850 Android4.2.2
     * HUAWEI Y518-T00 Android4.2.2
     * Huawei G610-T00 Android4.2.1
     * Huawei U9508 Android4.2.2
     * OPPO R829T Android4.2.2
     * <p>
     * java.lang.NullPointerException
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper$1.onInit(AccessibilityInjector.java:753)
     * at android.speech.tts.TextToSpeech.dispatchOnInit(TextToSpeech.java:653)
     * at android.speech.tts.TextToSpeech.initTts(TextToSpeech.java:632)
     * at android.speech.tts.TextToSpeech.<init>(TextToSpeech.java:553)
     * at android.webkit.AccessibilityInjector$TextToSpeechWrapper.<init>(AccessibilityInjector.java:676)
     * at android.webkit.AccessibilityInjector.addTtsApis(AccessibilityInjector.java:480)
     * at android.webkit.AccessibilityInjector.addAccessibilityApisIfNecessary(AccessibilityInjector.java:168)
     * at android.webkit.AccessibilityInjector.updateJavaScriptEnabled(AccessibilityInjector.java:415)
     * at android.webkit.WebViewClassic.updateJavaScriptEnabled(WebViewClassic.java:2017)
     * at android.webkit.WebSettingsClassic.setJavaScriptEnabled(WebSettingsClassic.java:1214)
     * <p>
     * 必须放在 {@link WebSettings#setJavaScriptEnabled }之前执行；
     * 如：
     * try {
     * webSettings.setJavaScriptEnabled(true);
     * } catch (NullPointerException e) {
     * fixedAccessibilityInjectorExceptionForSetJavaScriptEnabled();
     * webSettings.setJavaScriptEnabled(true);
     * }
     */
    protected void fixedAccessibilityInjectorExceptionForSetJavaScriptEnabled() {
        if (Build.VERSION.SDK_INT == 17
                && mIsAccessibilityEnabledOriginal == null
                && isAccessibilityEnabled()) {
            mIsAccessibilityEnabledOriginal = true;
            setAccessibilityEnabled(false);
        }
    }

    /**
     * 解决部分Android4.1中开启了辅助模式后，url参数不合法导致的崩溃问题；
     * url参数分隔符不使用“&”而是用“;”时，如：http://m.heise.de/newsticker/meldung/TomTom-baut-um-1643641.html?mrw_channel=ho;mrw_channel=ho;from-classic=1
     * <p>
     * 参考：
     * https://code.google.com/p/android/issues/detail?id=35100
     * http://osdir.com/ml/Android-Developers/2012-07/msg02123.html
     * <p>
     * 如：
     * Huawei HUAWEI C8815 4.1.2(16)
     * ZTE ZTE N919 4.1.2(16)
     * Coolpad 8190Q 4.1.2(16)
     * Lenovo Lenovo A706 4.1.2(16)
     * Xiaomi MI 2 4.1.1(16)
     * <p>
     * java.lang.IllegalArgumentException: bad parameter
     * at org.apache.http.client.utils.URLEncodedUtils.parse(URLEncodedUtils.java:139)
     * at org.apache.http.client.utils.URLEncodedUtils.parse(URLEncodedUtils.java:76)
     * at android.webkit.AccessibilityInjector.getAxsUrlParameterValue(AccessibilityInjector.java:406)
     * at android.webkit.AccessibilityInjector.shouldInjectJavaScript(AccessibilityInjector.java:323)
     * at android.webkit.AccessibilityInjector.onPageFinished(AccessibilityInjector.java:282)
     * at android.webkit.WebViewClassic.onPageFinished(WebViewClassic.java:4129)
     * at android.webkit.CallbackProxy.handleMessage(CallbackProxy.java:325)
     * at android.os.Handler.dispatchMessage(Handler.java:99)
     * at android.os.Looper.loop(Looper.java:137)
     * at android.app.ActivityThread.main(ActivityThread.java:4794)
     * at java.lang.reflect.Method.invokeNative(Native Method)
     * at java.lang.reflect.Method.invoke(Method.java:511)
     * at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:789)
     * at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:556)
     * at dalvik.system.NativeStart.main(Native Method)
     * <p>
     * 需要在{@link WebViewClient#onPageFinished(WebView, String)}之前的{@link WebViewClient#onPageStarted(WebView, String, Bitmap)}中检测并设置；
     */
    protected void fixedAccessibilityInjectorExceptionForOnPageFinished(String url) {
        if (Build.VERSION.SDK_INT == 16
                && getSettings().getJavaScriptEnabled()
                && mIsAccessibilityEnabledOriginal == null
                && isAccessibilityEnabled()) {
            try {
                try {
                    URLEncodedUtils.parse(new URI(url), null); // AccessibilityInjector.getAxsUrlParameterValue
                } catch (IllegalArgumentException e) {
                    if ("bad parameter".equals(e.getMessage())) {
                        mIsAccessibilityEnabledOriginal = true;
                        setAccessibilityEnabled(false);

                    }
                }
            } catch (Throwable e) {

            }
        }
    }

    private boolean isAccessibilityEnabled() {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        return am.isEnabled();
    }

    private void setAccessibilityEnabled(boolean enabled) {
        AccessibilityManager am = (AccessibilityManager) getContext().getSystemService(Context.ACCESSIBILITY_SERVICE);
        try {
            Method setAccessibilityState = am.getClass().getDeclaredMethod("setAccessibilityState", boolean.class);
            setAccessibilityState.setAccessible(true);
            setAccessibilityState.invoke(am, enabled);
            setAccessibilityState.setAccessible(false);
        } catch (Throwable e) {

        }
    }

    private void resetAccessibilityEnabled() {
        if (mIsAccessibilityEnabledOriginal != null) {
            setAccessibilityEnabled(mIsAccessibilityEnabledOriginal);
        }
    }

    /**
     * 向网页设置Cookie，设置Cookie后不需要页面刷新即可生效；
     * <p>
     * 1、用一级域名设置Cookie：cookieManager.setCookie("360.cn", "key=value;path=/;domain=360.cn")（android所有版本都支持这种格式）;
     * http://www.360doc.com/content/14/0903/22/9200790_406874810.shtml
     * 2、为何不能是“.360.cn”，在android2.3及以下版本，setCookie方法中URL参数必须是地址，如“360.cn”，而不能是“.360.cn”，否则存入webview.db-cookies表中的domain字段会为空导致无法在网页中生效;
     * http://zlping.iteye.com/blog/1633213
     */
    public static void updateCookies(Context context, UpdateCookies updateCookies) {
        // 1、2.3及以下需要调用CookieSyncManager.createInstance；
        // 2、Samsung GTI9300 Android4.3，在调用cookieManager.setAcceptCookie之前不调用CookieSyncManager.createInstance会发生native崩溃：java.lang.UnsatisfiedLinkError: Native method not found: android.webkit.CookieManagerClassic.nativeSetAcceptCookie:(Z)V at android.webkit.CookieManagerClassic.nativeSetAcceptCookie(Native Method)
        try {
            CookieSyncManager.createInstance(context);
        } catch (Throwable e) {
        }
        CookieManager cookieManager = null;
        try {
            cookieManager = CookieManager.getInstance();
        } catch (Throwable e) { // 当webview内核apk正在升级时会发生崩溃（Meizu	m2 note Android5.1）
//            android.util.AndroidRuntimeException: android.content.pm.PackageManager$NameNotFoundException: com.android.webview
//            at android.webkit.WebViewFactory.getFactoryClass(WebViewFactory.java:174)
//            at android.webkit.WebViewFactory.getProvider(WebViewFactory.java:109)
//            at android.webkit.CookieManager.getInstance(CookieManager.java:42)
//            at android.webkit.safe.SafeWebView.updateCookies(AppStore:479)
//            at com.qihoo360.accounts.manager.UserLoginManager.updateCookies(AppStore:669)
//            at com.qihoo360.accounts.manager.UserLoginManager.onLoginSucceed(AppStore:604)
//            at com.qihoo360.accounts.manager.UserLoginManager$6.onLoaded(AppStore:567)
//            at com.qihoo360.accounts.manager.UserLoginManager$6.onRpcSuccess(AppStore:550)
//            at com.qihoo360.accounts.api.auth.QucRpc$LocalHandler.handleMessage(AppStore:62)
//            at android.os.Handler.dispatchMessage(Handler.java:111)
//            at android.os.Looper.loop(Looper.java:194)
//            at android.app.ActivityThread.main(ActivityThread.java:5637)
//            at java.lang.reflect.Method.invoke(Native Method)
//            at java.lang.reflect.Method.invoke(Method.java:372)
//            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:959)
//            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:754)
//            Caused by: android.content.pm.PackageManager$NameNotFoundException: com.android.webview
//            at android.app.ApplicationPackageManager.getPackageInfo(ApplicationPackageManager.java:119)
//            at android.webkit.WebViewFactory.getFactoryClass(WebViewFactory.java:146)
//            ... 15 more
//            java.lang.NullPointerException: Attempt to invoke interface method 'void android.webkit.IWebViewUpdateService.waitForRelroCreationCompleted(boolean)' on a null object reference
//            at android.webkit.WebViewFactory.loadNativeLibrary(WebViewFactory.java:393)
//            at android.webkit.WebViewFactory.getProvider(WebViewFactory.java:106)
//            at android.webkit.CookieManager.getInstance(CookieManager.java:42)
//            at com.GoLemon.webview.SafeWebView.SafeWebView.void updateCookies(android.content.Context,com.GoLemon.webview.SafeWebView.SafeWebView$UpdateCookies)(lightsky:861)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.void updateLoginCookie()(lightsky:150)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.void updateCookie(boolean)(lightsky:51)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.void onLoginStateChange(boolean,java.lang.String)(lightsky:39)
//            at com.lightsky.video.thirdpart.login.manager.UserLoginManager$1$1.void run()(lightsky:387)
//            at android.os.Handler.handleCallback(Handler.java:815)
//            at android.os.Handler.dispatchMessage(Handler.java:104)
//            at android.os.Looper.loop(Looper.java:194)
//            at android.app.ActivityThread.main(ActivityThread.java:5667)
//            at java.lang.reflect.Method.invoke(Native Method)
//            at java.lang.reflect.Method.invoke(Method.java:372)
//            at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:962)
//            at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:757)

        }
        if (cookieManager != null) {
            try {
                cookieManager.setAcceptCookie(true);
            } catch (Throwable th) {
//            java.lang.UnsatisfiedLinkError: Native method not found: android.webkit.CookieManagerClassic.nativeSetAcceptCookie:(Z)V
//            at android.webkit.CookieManagerClassic.nativeSetAcceptCookie(Native Method)
//            at android.webkit.CookieManagerClassic.setAcceptCookie(CookieManagerClassic.java:44)
//            at com.GoLemon.webview.SafeWebView.SafeWebView.updateCookies(LittleVideoHome:911)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.updateLogoutCookie(LittleVideoHome:73)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.updateCookie(LittleVideoHome:56)
//            at com.lightsky.video.thirdpart.login.manager.LoginCookieManager.onLoginStateChange(LittleVideoHome:45)
//            at com.lightsky.video.thirdpart.login.manager.UpdateCookieReceiver.onReceiveResult(LittleVideoHome:37)
            }

            if (updateCookies != null) {
                updateCookies.update(cookieManager);
            }
            CookieSyncManager.getInstance().sync();
        }
    }

    public interface UpdateCookies {
        void update(CookieManager cookieManager);
    }

    protected void onReceiveTitle(String title) {
    }
}