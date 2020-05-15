package com.cashLoan.money.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.Nullable;

import com.GoLemon.webview.WebViewClientCallback;
import com.GoLemon.webview.safeWebView.SafeWebView;
import com.cashLoan.money.BuildConfig;
import com.dzfd.gids.baselibs.network.NetUtils;
import com.dzfd.gids.baselibs.utils.DensityUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;
import com.dzfd.gids.baselibs.utils.Utils;
import com.dzfd.gids.baselibs.utils.thread.ThreadUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class WebViewWrapper extends SafeWebView {


    private static final String TAG = "WebViewWrapper";
    private static final String SCHEME_HTTP="http";
    private static final String SCHEME_HTTPS="https";
    private boolean _CanDraw=true;


    public JavascriptInterface mJavaScriptinterface;
    private WebViewCallback mCallback;
    public InnerWebChromeClient mInnerWebChromeClient;
    private WebChromeClientCallback webChromeClientCallback;
    private IInterceptOverrideDeeplink mInterceptOverrideDeeplink;
    private WebViewClientCallback mWebviewClientCallback;
    private OnReceiveTitleListener onReceiveTitleListener;
    private Point pointDown, pointUp;
    private float mDensity;
    private int mPreContentHeight;
    private boolean shotOnce = false;
    private DrawContentCallback mDrawCallback;

    public WebViewWrapper(Context context) {
        this(context, null);
    }

    public WebViewWrapper(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebview();
    }

    public WebViewWrapper(Context context, AttributeSet attrs, JavascriptInterface javaScriptinterface) {
        super(context, attrs);
        mJavaScriptinterface = javaScriptinterface;
        initWebview();
    }


    public void setWebViewCallback(WebViewCallback callback) {
        mCallback = callback;
        if (mJavaScriptinterface != null)
            mJavaScriptinterface.setWebViewCallback(callback);
    }

    public void setInterceptOverrideDeeplink(IInterceptOverrideDeeplink interceptOverrideDeeplink) {
        this.mInterceptOverrideDeeplink = interceptOverrideDeeplink;
    }

    public void setWebChromeClientCallback(WebChromeClientCallback webChromeClientCallback) {
        this.webChromeClientCallback = webChromeClientCallback;
    }

    public void setWebviewClientCallback(WebViewClientCallback clientCallback) {
        this.mWebviewClientCallback = clientCallback;
    }

    public void setDrawContentCallback(DrawContentCallback callback) {
        this.mDrawCallback = callback;
    }
    @Override
    public void loadUrl(String url) {
        InitPageDraw(url);

        super.loadUrl(url);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders) {
        InitPageDraw(url);
        super.loadUrl(url, additionalHttpHeaders);
    }
    public void InitPageDraw(String url){
        if(url == null){
            return;
        }
        boolean ishttp=(url.startsWith(SCHEME_HTTP) || url.startsWith(SCHEME_HTTPS));
        if(ishttp){
            Uri urlsrc=Uri.parse(url);
            if(urlsrc!=null){
                Set<String> querys=urlsrc.getQueryParameterNames();
                if(querys!=null && !querys.isEmpty()){
                    boolean hasdrawpa=querys.contains("delaydraw");
                    if(hasdrawpa){
                        _CanDraw=!urlsrc.getBooleanQueryParameter("delaydraw",false);
                    }
                }
            }
        }
    }
    public void canDraw(boolean candraw){
        _CanDraw=candraw;

        post(new Runnable() {
            @Override
            public void run() {
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(_CanDraw){
                            invalidate();
                        }
                    }
                });
            }
        });

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void initWebview() {
        WebSettings ws = getSettings();
        try {
            ws.setJavaScriptEnabled(true);
        } catch (NullPointerException e) {
            fixedAccessibilityInjectorExceptionForSetJavaScriptEnabled();
            ws.setJavaScriptEnabled(true);
        }
        ws.setBuiltInZoomControls(true);
        ws.setSupportZoom(true);
        if (Build.VERSION.SDK_INT >= 11) {
            ws.setDisplayZoomControls(false); // 保留缩放功能但隐藏缩放控件
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        ws.setUseWideViewPort(true); // 使网页支持“视区”设置：<meta name="viewport" .../>；
        ws.setDatabaseEnabled(true);//启用webdatabase数据库
        ws.setGeolocationEnabled(true);//启用地理定位
        ws.setGeolocationDatabasePath(getContext().getApplicationContext().getDatabasePath(" ").getParent()); // 设置定位的数据库路径，传一个空格即可，避免空指针异常，主要是取父目录的路径；
        ws.setDomStorageEnabled(true);//清除地理位置缓存时，需要删除databases文件夹下的CachedGeoposition.db、GeolocationPermissions.db,重启应用。
        ws.setUserAgentString(ws.getUserAgentString() + ";" + HttpUserAgent.USER_AGENT_KEY);
        ws.setAppCacheEnabled(true);
        ws.setAllowFileAccess(true); // 活动半屏界面需要加载本地zip文件；
        ws.setTextSize(WebSettings.TextSize.NORMAL); // 助手所有界面的TextView字体使用的是dip单位，界面字体不会随着调整系统字体大小而变大，网页的字体也设置成这样；
//        ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        ws.setDomStorageEnabled(true);

        if(NetUtils.isNetworkConnected()){
            //有网络，则加载网络地址
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        }else{
            //无网络，则加载缓存路径
            ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        String cacheDirPath = getContext().getFilesDir().getAbsolutePath()+ "/webcache";//缓存路径
        ws.setDatabasePath(cacheDirPath);//设置数据库缓存路径
        ws.setAppCachePath(cacheDirPath);//设置AppCaches缓存路径



        setWebViewClient(new WebViewWrapperWebViewClient());
        mInnerWebChromeClient = new InnerWebChromeClient();
        setWebChromeClient(mInnerWebChromeClient);
        if (mJavaScriptinterface == null) {
            mJavaScriptinterface = new JavascriptInterface(getContext(), this);
        }

        mJavaScriptinterface.setWebViewCallback(mCallback);

        addJavascriptInterface(mJavaScriptinterface, "limonWebview");
        setHorizontalScrollbarOverlay(true);
        setVerticalScrollBarEnabled(true);
        setVerticalScrollbarOverlay(true);
        setScrollbarFadingEnabled(true);
        setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        if (BuildConfig.DEBUG) {
            trySetWebDebuggEnabled();
        }
        mDensity = DensityUtils.getDisplayMetrics().density;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode && KeyEvent.ACTION_DOWN == event.getAction()) {
            if (mJavaScriptinterface.hasBackKeyDownListenner) {
                callWebViewJs("onBackKeyDown()");
                return true;
            }
            if (mJavaScriptinterface.interceptKeyDown) {
                return false;
            }
            if (canGoBack()) {
                goBack();
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(!_CanDraw){
            drawEmptyPage(canvas);
            return;
        }
        super.onDraw(canvas);
        int contentHeight = getContentHeight();
        if (contentHeight != 0 && contentHeight != mPreContentHeight && !shotOnce) {
            mPreContentHeight = contentHeight;
            if (contentHeight * mDensity >= getMeasuredHeight()) {
                if (mDrawCallback != null) {
                    mDrawCallback.onDraw();
                    shotOnce = true;
                }
            }
        }
    }
    private void drawEmptyPage(Canvas canvas)
    {
        // assuming default color is WHITE
        canvas.drawColor(Color.WHITE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (pointDown == null) {
                pointDown = new Point((int) event.getX(), (int) event.getY());
            } else {
                pointDown.set((int) event.getX(), (int) event.getY());
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (pointUp == null) {
                pointUp = new Point((int) event.getX(), (int) event.getY());
            } else {
                pointUp.set((int) event.getX(), (int) event.getY());
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public Point[] getPointDownUp() {
        return new Point[]{pointDown, pointUp};
    }

    /**
     * 调用WebView的js函数
     */
    public void callWebViewJs(final String js) {
        if (!TextUtils.isEmpty(js)) {
            ThreadUtils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        loadUrl(buildTryCatchInjectJS(js)); // WebView所有方法的调用线程与WebView初始化时的线程必须是同一个线程；
                    } catch (Throwable e) {
                        if (LogUtils.isDebug()) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public class InnerWebChromeClient extends SafeWebChromeClient {

        public static final int FILE_CHOOSER_RESULT_CODE = 100;

        public ValueCallback<Uri> mUploadMessage;
        private ValueCallback<Uri[]> filePathCallback;

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (webChromeClientCallback != null) {
                webChromeClientCallback.onReceivedTitle(view, title);
            }
        }

        /**
         * 上传文件相关
         * 4.0调用上传文件方法
         */
        public void openFileChooser(ValueCallback<Uri> uploadFile, String acceptType, String capture) {
            openFile(uploadFile);
        }

        /**
         * 4.0以下调用上传文件方法
         */
        public void openFileChooser(ValueCallback<Uri> uploadFile) {
            openFile(uploadFile);
        }

        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileForL(filePathCallback);
            return true;
        }

        private void openFile(ValueCallback<Uri> uploadFile) {
            mUploadMessage = uploadFile;
            startActivityForResult();
        }

        private void openFileForL(ValueCallback<Uri[]> uploadFile) {
            this.filePathCallback = uploadFile;
            startActivityForResult();
        }

        private void startActivityForResult() {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");

            if (getContext() != null && (getContext() instanceof Activity)) {
                ((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, "选择图片"), FILE_CHOOSER_RESULT_CODE);
            }
        }

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {
            if (requestCode == FILE_CHOOSER_RESULT_CODE) {
                if (mUploadMessage != null) {
                    Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (filePathCallback != null) {
                        Uri[] results = null;
                        if (resultCode == Activity.RESULT_OK) {
                            if (intent != null) {
                                String dataString = intent.getDataString();
                                ClipData clipData;

                                clipData = intent.getClipData();

                                if (clipData != null) {
                                    results = new Uri[clipData.getItemCount()];
                                    for (int i = 0; i < clipData.getItemCount(); i++) {
                                        ClipData.Item item = clipData.getItemAt(i);
                                        results[i] = item.getUri();
                                    }
                                }
                                if (dataString != null)
                                    results = new Uri[]{Uri.parse(dataString)};
                            }
                        }
                        filePathCallback.onReceiveValue(results);
                        filePathCallback = null;
                    }
                }
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            ProgressNotifyCenter.INSTANCE.onProgressChange(view.getUrl(), newProgress);
        }

    }

    @Override
    protected void onReceiveTitle(String title) {
        if (onReceiveTitleListener != null) {
            onReceiveTitleListener.onReceiveTitle(title);
        }
    }

    public void setOnReceiveTitleListener(OnReceiveTitleListener onReceiveTitleListener) {
        this.onReceiveTitleListener = onReceiveTitleListener;
    }

    public interface OnReceiveTitleListener {
        void onReceiveTitle(String title);
    }

    private class WebViewWrapperWebViewClient extends SafeWebViewClient {

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (mWebviewClientCallback != null) {
                return mWebviewClientCallback.shouldInterceptRequest(view, url);
            }
            return super.shouldInterceptRequest(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (mWebviewClientCallback != null) {
                mWebviewClientCallback.onPageFinished(view, url);
            }
            //ensure
            ProgressNotifyCenter.INSTANCE.onProgressChange(url, 100);
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
                if (mWebviewClientCallback != null) {
                    return mWebviewClientCallback.shouldOverrideUrlLoading(view, url);
                }
                return false;
            } else {
                //如果业务层需要拦截deeplink 那么就直接返回了
                if (mInterceptOverrideDeeplink != null && mInterceptOverrideDeeplink.intercept(url)) {
                    return true;
                }
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                    if (mInterceptOverrideDeeplink != null) {
                        mInterceptOverrideDeeplink.onOpenSuccess();
                    }
                } catch (Exception ex) {
                    if (mInterceptOverrideDeeplink != null) {
                        mInterceptOverrideDeeplink.onOpenFail();
                    }
                }
                return true;
            }
        }
    }

    public static abstract class DrawContentCallback {
        private final List<String> filterModel = Arrays.asList("C106");

        abstract void onDrawContent();

        public void onDraw() {
            if (!filterModel.contains(Build.MODEL)) onDrawContent();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            super.onAttachedToWindow();
        } catch (Throwable thr) {
            LogUtils.e(TAG, "", thr);
            Context context = getContext();
            if (context != null && context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
    }

    @Override
    public void destroy() {
        if(mCallback!=null){
            mCallback=null;
        }
        if(mJavaScriptinterface!=null){
            mJavaScriptinterface.destroy();
        }
        super.destroy();
    }
}
