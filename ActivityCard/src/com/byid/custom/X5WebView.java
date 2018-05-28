package com.byid.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.byid.utils.DialogUtil;
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback;
import com.tencent.smtt.export.external.interfaces.JsPromptResult;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import android_serialport_api.sample.base.RWCrashApplication;

public class X5WebView extends WebView {

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {
        WebSettings mWebSettings = getSettings();
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
        WebView.setWebContentsDebuggingEnabled(true);
        // 是否允许访问缓存
        mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        // 设置WebView边缘不出现阴影
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // 允许下载
        this.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            // 使用系统浏览器下载
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            RWCrashApplication.application.startActivity(intent);
        });
        setWebChromeClient(new com.tencent.smtt.sdk.WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

            }

            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
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
             * @param geolocationPermissionsCallback 定位权限的回调
             */
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissionsCallback geolocationPermissionsCallback) {
                //给网页赋予定位权限
                geolocationPermissionsCallback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, geolocationPermissionsCallback);
            }

            @Override
            public boolean onJsAlert(WebView webView, String s, String s1, JsResult jsResult) {
                DialogUtil.getInstance().defAlert(((Activity) getContext()), s1);
                jsResult.confirm();
                return true;
            }


            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                DialogUtil.getInstance().defConfirm(((Activity) getContext()), message, aBoolean -> {
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
                DialogUtil.getInstance().defPrompt(((Activity) getContext()), message, string -> {
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
}
