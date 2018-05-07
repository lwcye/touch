package com.hbln.touch.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;

import com.hbln.touch.R;
import com.hbln.touch.base.BaseActivity;
import com.hbln.touch.utils.SysUtil;
import com.hbln.touch.utils.WebViewUtil;

/**
 * Created by 41569 on 2018/5/6.
 */
public class NormalBrowserActivity extends BaseActivity {
    public final static String PARAM_URL = "param_url";
    private String mUrl;
    private WebView mWebView;

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
        btnShutdown.setOnClickListener(view -> SysUtil.getInstance().shutDown(getActivity()));
        initWebView();
    }

    private void initWebView() {
        WebViewUtil.getInstance().initWebView(getActivity(), mWebView);
        // 注册需要Context的AppInterface
        WebViewUtil.getInstance().registerAppInterface();

        // 在这里注册，重庆城页面相关的Native调用或者Url拦截
        WebViewUtil.getInstance().injectAppInterface();
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
