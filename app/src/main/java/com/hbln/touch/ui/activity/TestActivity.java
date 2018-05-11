package com.hbln.touch.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.byid.android.ByIdActivity;
import com.hbln.touch.R;
import com.hbln.touch.base.BaseActivity;
import com.wits.autoonoff.AutoOnoffActivity;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ToastUtils.showLong(ScreenUtils.getScreenWidth() + " X " + ScreenUtils.getScreenHeight());
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public void video(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra("url", "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4");
        intent.putExtra("title", "测试");
        startActivity(intent);
    }

    public void photo(View view) {

    }

    public void main(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void webview(View view) {
        Intent intent = new Intent(this, NormalBrowserActivity.class);
        intent.putExtra(NormalBrowserActivity.PARAM_URL, "http://www.tuwen.json");
        startActivity(intent);
    }

    public void shutdown(View view) {
        Intent intent = new Intent(getActivity(), AutoOnoffActivity.class);
        getActivity().startActivity(intent);
    }

    public void serial(View view) {
        Intent intent = new Intent(getActivity(), ByIdActivity.class);
        getActivity().startActivity(intent);
    }
}
