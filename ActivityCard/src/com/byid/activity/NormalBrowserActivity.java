package com.byid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.byid.utils.WebViewUtil;
import com.github.lzyzsd.jsbridge.BridgeWebView;

import android_serialport_api.sample.R;
import android_serialport_api.sample.base.BaseActivity;

/**
 * Created by 41569 on 2018/5/6.
 */
public class NormalBrowserActivity extends BaseActivity {
    public final static String PARAM_URL = "param_url";
    private String mUrl;
    private BridgeWebView mWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);
        initView();
        initData();
        mWebView.loadUrl(mUrl);
    }

    @Override
    public void initView() {
        // init webview
        mWebView = findViewById(R.id.webview);
        initWebView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewUtil.getInstance().onDestroy(mWebView);
    }

    private void initWebView() {
        WebViewUtil.getInstance().initWebView(getActivity(), mWebView);
        // 注册需要Context的AppInterface
        WebViewUtil.getInstance().registerAppInterface(mWebView);
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
