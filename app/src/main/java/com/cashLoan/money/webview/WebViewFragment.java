package com.cashLoan.money.webview;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.GoLemon.webview.WebViewClientCallback;
import com.cashLoan.money.R;
import com.dzfd.gids.baselibs.utils.LogUtils;


public class WebViewFragment extends Fragment implements View.OnClickListener, WebViewClientCallback, WebChromeClientCallback, KeyboardShowObserver.OnKeyboardShowListener, ProgressNotifyCenter.INotifyCallback, WebViewCallback {
    private WebViewWrapper webView;
    private View rootView;
    private HorizontalProgressView progressView;
    private String requestUrl;
    private String defaultUrl;
    private WebViewCallback callback;
    private JavascriptInterface javascriptInterface;

    private boolean mInit = false;
    private boolean shouldIntercept = false;
    private boolean enableScrollBar = false;
    private boolean isLoaded = false;

    public static WebViewFragment getInstance(Bundle bundle) {
        WebViewFragment webViewFragment = new WebViewFragment();
        webViewFragment.setArguments(bundle);
        return webViewFragment;
    }

    public void setJavascriptInterface(JavascriptInterface javascriptInterface) {
        this.javascriptInterface = javascriptInterface;
    }

    public void setWebViewCallback(WebViewCallback callback) {
        this.callback = callback;
    }

    public void callWebViewJs(String js) {
        if (webView != null) {
            webView.callWebViewJs(js);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_webview, container, false);
        progressView = rootView.findViewById(R.id.progress);
        ProgressNotifyCenter.INSTANCE.addNotifyCallback(this);
        KeyboardShowObserver.getInstance().registerKeyboardShowListener(this, rootView, true);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            requestUrl = bundle.getString(WebViewActivity.REQUEST_URL);
            defaultUrl = bundle.getString(WebViewActivity.DEFAULT_URL);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint() && !mInit) {
            init();
        }
        if (webView != null) {
            webView.callWebViewJs("onClientResumeHandler()");
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (webView != null) {
            webView.callWebViewJs("onClientPauseHandler()");
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ProgressNotifyCenter.INSTANCE.removeNotifyCallback(this);
        KeyboardShowObserver.getInstance().registerKeyboardShowListener(this, rootView, false);
        if(webView!=null){
            webView.destroy();
            webView=null;
        }
    }

    private void init() {
        if (mInit || rootView == null) {
            return;
        }
        mInit = true;
        initWebView();
        loadUrl();
    }

    private void initWebView() {
        final FrameLayout webViewContainer = rootView.findViewById(R.id.webviewcontainer);
        try {
            if (javascriptInterface != null) {
                webView = new WebViewWrapper(getActivity(), null, javascriptInterface);
                javascriptInterface.setWebView(webView);
                javascriptInterface.interceptKeyDown = shouldIntercept;
            } else {
                webView = new WebViewWrapper(getActivity());
            }

            webView.setWebChromeClientCallback(this);
            webView.setWebviewClientCallback(this);
            webView.setVerticalScrollBarEnabled(enableScrollBar);
            webView.setHorizontalScrollBarEnabled(enableScrollBar);
            webView.setWebViewCallback(this);
        } catch (Throwable e) {
            String msg = Log.getStackTraceString(e);
            if (msg.contains("Cannot load WebView")
                    || msg.contains("PackageManager$NameNotFoundException")
                    || msg.contains("java.lang.NoClassDefFoundError: android/webkit/JniUtil")
                    || msg.contains("No WebView installed")
                    || msg.contains("Unsupported ABI: null")) {
                if (getActivity() != null && !getActivity().isDestroyed()) {
                    Toast.makeText(getActivity(), "WebView package does not exist, " + e.getMessage(), Toast.LENGTH_SHORT);
                }
                if (LogUtils.isDebug()) {
                    e.printStackTrace();
                }
                getActivity().finish();
                return;
            } else {
                throw e;
            }
        }
        webViewContainer.addView(webView);
    }

    private void loadUrl() {
        if (!isLoaded && webView != null) {
            try {
                webView.loadUrl(requestUrl);
            } catch (Exception e) {
            }
            isLoaded = true;
        }
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (getActivity() instanceof WebViewActivity && !getActivity().isFinishing()) {
            WebViewActivity activity = (WebViewActivity) getActivity();
            if (TextUtils.isEmpty(activity.title) || webView.canGoBack()) {
                activity.updateTitle(title);
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (!TextUtils.isEmpty(defaultUrl)) {
            try {
                webView.loadUrl(defaultUrl);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
    }

    @SuppressLint("ResourceType")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        /*if (url.endsWith(WebViewActivity.FONT_REQUEST_URL)) {
            InputStream is = null;
            try {
                is=getResources().openRawResource(R.font.roboto_regular);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
            if (is != null) {
                if (android.os.Build.VERSION.SDK_INT > 20) {
                    HashMap<String, String> header = new HashMap<>();
                    header.put("Access-Control-Allow-Origin", "*");
                    return new WebResourceResponse("application/x-font-otf", "UTF-8", 200, "OK", header, is);
                } else {
                    return new WebResourceResponse("application/x-font-otf", "UTF-8", is);
                }
            }
        }*/
        return null;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (webView != null && webView.mInnerWebChromeClient != null) {
            webView.mInnerWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onKeyboardHeightChange(int height) {
        callWebViewJs("onKeyboardChange(" + height + ")");
    }

    @Override
    public void onProgressChange(String url, int progress) {
        if (progress <= 0 || progress >= 100) {
            progressView.setVisibility(View.GONE);
        } else {
            progressView.setVisibility(View.VISIBLE);
            progressView.setProgress(progress);
        }
    }

    @Override
    public void onPageClose(String url) {

    }

    @Override
    public Object onJSCall(String function, String param, String param2, Object reserve) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return null;
        }

        if (callback != null) {
            return callback.onJSCall(function, param, param2, reserve);
        }
        return null;
    }
}
