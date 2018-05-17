package com.cmcc.myinstallapk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.blankj.utilcode.util.IntentUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.cmcc.myinstallapk.utils.AssetsUtil;
import com.wits.autoonoff.AutoOnoffActivity;

import java.io.File;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Observable.just(null)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        AssetsUtil.copeAssets2CacheDir("ActivityCard.apk");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showLong(throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Intent installAppIntent = IntentUtils.getInstallAppIntent(Utils.getApp().getExternalCacheDir() + File.separator + "ActivityCard.apk", true);
                        MainActivity.this.startActivity(installAppIntent);
                        ToastUtils.showLong("完成安装");
                    }
                });

        startActivity(new Intent(this, AutoOnoffActivity.class));
    }
}
