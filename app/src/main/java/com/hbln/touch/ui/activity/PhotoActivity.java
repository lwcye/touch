package com.hbln.touch.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import com.blankj.utilcode.util.ToastUtils;
import com.github.chrisbanes.photoview.PhotoView;
import com.hbln.touch.R;
import com.hbln.touch.base.BaseActivity;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

public class PhotoActivity extends BaseActivity {
    public static final String INTENT_URL = "url";
    private PhotoView mPvPhoto;
    private String mUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();

    }

    @Override
    public void initView() {
        mPvPhoto = findViewById(R.id.pv_photo);
    }

    @Override
    public void initData() {
        mUrl = getIntent().getStringExtra(INTENT_URL);
        if (TextUtils.isEmpty(mUrl)) {
            ToastUtils.showLong("图片路径为空");
            finish();
            return;
        }
        x.image().loadDrawable(mUrl, ImageOptions.DEFAULT, new Callback.CacheCallback<Drawable>() {
            @Override
            public boolean onCache(Drawable result) {
                return true;
            }

            @Override
            public void onSuccess(Drawable result) {
                mPvPhoto.setImageDrawable(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
