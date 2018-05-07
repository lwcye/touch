package com.hbln.touch.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.lzyzsd.jsbridge.BridgeUtil;
import com.hbln.touch.base.MyApplication;
import com.hbln.touch.constant.ENVs;
import com.hbln.touch.web.AppInterface;
import com.hbln.touch.web.WebViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \
 */
public class WebViewUtil {
    private static WebViewUtil ourInstance = new WebViewUtil();
    /** Javascript注入规则映射 */
    private Map<String, String> mJavascriptInjectionMap = new HashMap<>();

    private WebViewUtil() {
    }

    public static WebViewUtil getInstance() {
        return ourInstance;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initWebView(Activity activity, WebView mWebView) {
        WebSettings mWebSettings = mWebView.getSettings();
        // 自适应窗口
        mWebSettings.setUseWideViewPort(true);
        mWebSettings.setLoadWithOverviewMode(true);
        // 允许本地存储数据
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        mWebSettings.setAppCacheEnabled(true);
        // 允许访问文件
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccessFromFileURLs(true);
        mWebSettings.setAllowUniversalAccessFromFileURLs(true);
        // 缩放功能
        mWebSettings.setSupportZoom(false);
        mWebSettings.setBuiltInZoomControls(false);
        // 允许H5读取定位
        mWebSettings.setGeolocationEnabled(true);

        // 允许自动播放音频
        mWebSettings.setMediaPlaybackRequiresUserGesture(false);
        mWebSettings.setJavaScriptEnabled(true);

        // init webview settings
        mWebSettings.setAllowContentAccess(true);
        mWebSettings.setSavePassword(false);
        mWebSettings.setSaveFormData(false);
        // 设置UserAgent
        mWebSettings.setUserAgentString("chrome");
        WebView.setWebContentsDebuggingEnabled(true);
        // 是否允许访问缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 设置WebView边缘不出现阴影
        mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 允许下载
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            // 使用系统浏览器下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getInstance().startActivity(intent);
        });
        // 状态处理
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 网络图片延迟加载
                view.getSettings().setBlockNetworkImage(false);
                // 注入Javascript代码
                injectJavascript(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                return true;
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @note For Android < 3.0
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @note For Android 3.0+
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooser(uploadMsg, acceptType, "");
            }

            /**
             * 打开文件
             *
             * @param uploadMsg
             * @param acceptType
             * @param capture
             * @note For Android 4.1+
             */
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            }

            /**
             * 定位提示的回调
             *
             * @param origin 请求地址
             * @param callback 定位权限的回调
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                //给网页赋予定位权限
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                DialogUtil.getInstance().defAlert(activity, message);

                return true;
                //return super.onJsAlert(view, url, message, result);
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                DialogUtil.getInstance().defConfirm(activity, message, aBoolean -> {
                    if (aBoolean) {
                        result.confirm();
                    } else {
                        result.cancel();
                    }
                });
                return true;
                //return super.onJsConfirm(view, url, message, result);
            }

            @Override
            public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                DialogUtil.getInstance().defPrompt(activity, message, string -> {
                    if (TextUtils.isEmpty(string)) {
                        result.cancel();
                    } else {
                        result.confirm(string);
                    }
                });
                return true;
                //return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });
    }

    public void registerAppInterface() {
        AppInterface.getInstance().clear();
        AppInterface.getInstance().register();
    }

    public void injectAppInterface() {
        mJavascriptInjectionMap.put("*", ENVs.INJECT_APP_INTERFACE);
    }

    /**
     * 注入javascript代码
     *
     * @param webview webview
     * @param url url
     */
    private void injectJavascript(WebView webview, String url) {
        List<String> injectedRecord = new ArrayList<>();

        for (Map.Entry<String, String> entry : mJavascriptInjectionMap.entrySet()) {
            String startUrl = entry.getKey();
            String javascriptPath = entry.getValue();
            if ("*".equals(startUrl) || WebViewManager.matchStartUrl(url, startUrl)) {
                if (!injectedRecord.contains(javascriptPath)) {
                    // 规则匹配则注入javascript
                    BridgeUtil.webViewLoadLocalJs(webview, javascriptPath);
                    // 调用注入完成
                    webview.loadUrl("javascript:try{onInjectJS();}catch(e){}");
                    // 标记为已注入
                    injectedRecord.add(javascriptPath);
                }

                continue;
            }
        }
    }
}
