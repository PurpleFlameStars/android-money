package com.cashLoan.money.webview;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.cashLoan.money.BuildConfig;
import com.cashLoan.money.R;
import com.dzfd.gids.baselibs.activity.BunActivity;
import com.dzfd.gids.baselibs.utils.BarUtils;
import com.dzfd.gids.baselibs.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class WebViewActivity extends BunActivity {

    public static final String URL_CONTENT = "url_content";
    public static final String FROM_NATIVE = "from_native";

    public static final String HIDE_TITLE = "hide_title";
    public static final String FULL_SCREEN = "full_screen";
    public static final String ENABLE_SCROLL = "enable_scroll";

    public static final String REQUEST_URL = "request_url";
    public static final String DEFAULT_URL = "default_url";

    private static final String HOST_HTTP = "http://";
    private static final String HOST_HTTPS = "https://";
    public static final String HTTP_DOMAIN;

    private TextView titleview;
    private View titleLayout;
    protected JavascriptInterface mJavaScriptInterface;
    private WebViewFragment webViewFragment;
    private boolean hideTitle = false;
    private boolean fullScreen = false;
    private boolean enableScroll = true;
    public String title;
    private String immersiveColor;
    private Bundle startParamter;

    public static final String H5_RELEASE = "h5.hi-live365.net/";
    public static final String H5_DEBUG = "h5.hi-live365.net/";

    static {
        if (BuildConfig.IS_RELEASE_HOST) {
            HTTP_DOMAIN = HOST_HTTPS + H5_RELEASE;
        } else {
            HTTP_DOMAIN = HOST_HTTP + H5_DEBUG;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        startParamter = parseRequestUrl();
        if (fullScreen) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //打开有webview页面之后会导致系统内语言使用系统语言设置的bug，在所有activity setContentView之前重新设置语言
//            LocalManageUtil.setLocal(this);TODO
        }
        int layoutid = getActivityLayoutId();
        if (layoutid > 0) {
            setContentView(layoutid);
        }
        Fragment maininst = getMainFragment();
        if (maininst != null) {
            addMainFragment(maininst);
        }

        initView();
        if (fullScreen) {
//            AdpterCutout();
        }
        LogUtils.e("webview" , "fullScreen=" + fullScreen + " title=" + title + " hide= " + hideTitle );
    }

    private Bundle parseRequestUrl() {
        Bundle bundle = new Bundle();
        String requestUrl = getRequestUrl();
        if (isFromNative()) {
            handleCommonParams(requestUrl);
        } else {
            requestUrl = parseUrl(requestUrl);
        }

        bundle.putString(REQUEST_URL, addPublicParams(requestUrl));
        bundle.putString(DEFAULT_URL, getDefaultUrl());
        return bundle;
    }

    private void initView() {
        titleview = findViewById(R.id.title_view);
        titleview.setText("");
        titleLayout = findViewById(R.id.title_layout);
        titleLayout.setVisibility(hideTitle ? View.GONE : View.VISIBLE);
        if (titleLayout.getVisibility() == View.VISIBLE) {
            BarUtils.addMarginTopEqualStatusBarHeight(titleLayout);
        }
        setEnableScrollBack(enableScroll);
        updateTitle(title);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void updateTitle(String title) {
        if (!hideTitle && titleview != null) {
            titleview.setText(title);
        }
    }

    public void setWebViewCallback(WebViewCallback callback) {
        if (webViewFragment != null)
            webViewFragment.setWebViewCallback(callback);
    }

    public abstract String getRequestUrl();

    // 本地页面，可以不配置
    public String getDefaultUrl() {
        return "";
    }

    public abstract boolean isFromNative();

    protected JavascriptInterface getJavascriptInterface() {
        if (mJavaScriptInterface == null) {
            mJavaScriptInterface = new JavascriptInterface(this, null);
        }
        return mJavaScriptInterface;
    }

    protected void callWebViewJS(String js) {
        if (webViewFragment != null) {
            webViewFragment.callWebViewJs(js);
        }
    }

    private String parseUrl(String contentUrl) {
        JSONObject jsonObject;
        String url = "";

        if (TextUtils.isEmpty(contentUrl)) {
            return url;
        }

        try {
            jsonObject = new JSONObject(contentUrl);
            title = jsonObject.optString("title");
            url = jsonObject.optString("url");
            hideTitle = jsonObject.optBoolean("hide_title");
            immersiveColor = jsonObject.optString("color");
            if (jsonObject.optBoolean("close_slide")) {
                enableScroll = false;
            }
        } catch (JSONException e) {
        }
        return url;
    }

    /**
     * 添加通用参数
     *
     * @param originalUrl
     * @return
     */
    private String addPublicParams(String originalUrl) {
        /*if (!TextUtils.isEmpty(originalUrl)) {TODO
            // 添加语言参数
            String lang = LocalManageUtil.getCurrentLanguage(ContextUtils.getAppContext());
            if (originalUrl.contains("?")) {
                originalUrl += "&lang=" + lang;
            } else {
                originalUrl += "?lang=" + lang;
            }
        }*/
        return originalUrl;
    }

    /**
     * 解析通用参数
     *
     * @param parseUrl
     */
    private void handleCommonParams(String parseUrl) {
        if (getIntent() != null) {
            fullScreen = getIntent().getBooleanExtra(FULL_SCREEN, false);
            hideTitle = fullScreen == true ? true : getIntent().getBooleanExtra(HIDE_TITLE, false);
            enableScroll = getIntent().getBooleanExtra(ENABLE_SCROLL, true);
        }

        if (!TextUtils.isEmpty(parseUrl)) {
            Uri uri = Uri.parse(parseUrl);
            if (TextUtils.equals(uri.getQueryParameter("full_screen"), "1")) {
                fullScreen = true;
                hideTitle = true;
            }
            if (TextUtils.equals(uri.getQueryParameter("close_slide"), "1")) {
                enableScroll = false;
            }
            if (TextUtils.equals(uri.getQueryParameter("hide_title"), "1")) {
                hideTitle = true;
            }
        }
    }

    public int getActivityLayoutId() {
        return R.layout.activity_message_center;
    }

    public Fragment getMainFragment() {
        webViewFragment = WebViewFragment.getInstance(startParamter);
        webViewFragment.setJavascriptInterface(getJavascriptInterface());
        return webViewFragment;
    }

    public void addMainFragment(Fragment fragment) {
        int viewid = getFragmentcontainerViewId();
        getSupportFragmentManager().beginTransaction().add(viewid, fragment).commit();
    }

    public int getFragmentcontainerViewId() {
        return R.id.container;
    }

}
