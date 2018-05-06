package com.hbln.touch.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.hbln.touch.R;
import com.hbln.touch.base.BaseActivity;
import com.hbln.touch.base.MyApplication;
import com.hbln.touch.utils.DialogUtil;
import com.hbln.touch.utils.WebViewUtil;

import rx.functions.Action1;

/**
 * Created by 41569 on 2018/5/6.
 */

public class NormalBrowserActivity extends BaseActivity {
    private String mUrl;
    public final static String PARAM_URL = "param_url";
    private WebView mWebView;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_browser);
        initView();
        initData();
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void initView() {
        // init webview
        mWebView = findViewById(R.id.webview);
        Button btnShutdown = findViewById(R.id.btn_shutdown);
        btnShutdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        initWebView();
    }

    private void initWebView() {
        WebViewUtil.initWebView(getActivity(), mWebView);
    }


    @Override
    public void initData() {
        handleIntent();
    }

    private void handleIntent() {
        Intent intent = getIntent();
        mUrl = intent.getStringExtra(PARAM_URL);
        if (TextUtils.isEmpty(mUrl)) {
            finish();
        }
    }
}
