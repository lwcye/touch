package com.byid.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.byid.custom.X5WebView;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import android_serialport_api.sample.R;
import android_serialport_api.sample.base.BaseActivity;

public class FullScreenActivity extends BaseActivity {

    /**
     * 用于演示X5webview实现视频的全屏播放功能 其中注意 X5的默认全屏方式 与 android 系统的全屏方式
     */

    X5WebView mWebView;
    public final static String PARAM_URL = "param_url";
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filechooser_layout);
        mWebView = (X5WebView) findViewById(R.id.web_filechooser);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        initData();
        initView();
    }

    @Override
    public void initView() {
        initWebView();
    }

    @Override
    public void initData() {
        handleIntent();
    }

    private void initWebView() {
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
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("http") && !url.startsWith("https") && !url.startsWith("yy")) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        getActivity().startActivity(intent);
                        return true;
                    } catch (Exception ignored) {
                        LogUtils.d(ignored);
                    }
                }
                view.loadUrl(url);
                return true;
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }
        });
        mWebView.loadUrl(mUrl);
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(PARAM_URL);
        if (TextUtils.isEmpty(mUrl)) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
