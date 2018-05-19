package com.byid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.byid.android.ByIdActivity;
import com.byid.utils.HttpUtil;

import android_serialport_api.sample.PowerOperate;
import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortPreferences;
import android_serialport_api.sample.SerialPortPreferencesFinish;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 41569 on 2018/5/19.
 */

public class HomeActivity extends ByIdActivity implements View.OnClickListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findViewById(R.id.btn_read).setOnClickListener(this);
        Observable.just(null)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object o) {
                        setting();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        ToastUtils.showLong(throwable.getMessage());
                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        ToastUtils.showLong("read");
                        readSfCode();
                    }
                });
        HttpUtil.getInstance().getConfig(10, true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readSfCode();
    }

    /**
     * 读取身份证
     */
    private void readSfCode() {
        if (isOpen == false & source == true & SerialPortPreferences.switching == true) {

            isOpen = true;
            source = true;
            ToastUtils.showLong("开始读卡");
            return;

        }
        if (isOpen == false & source == false & SerialPortPreferences.switching == false) {

            //PowerOperate.enableRIFID_Module_5Volt();
            source = true;
            ToastUtils.showLong("开始读卡");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    try {
                        // btread.performClick();

                        isOpen = true;
                        return;
                    } catch (Exception e) {
                    }


                }
            }, 1500);


        } else {
            if (isOpen == true & source == true & SerialPortPreferences.switching == false) {

                source = false;
                isOpen = false;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {


                        try {
                            // btread.performClick();
                            //PowerOperate.disableRIFID_Module_5Volt();
                            ToastUtils.showLong("已经关闭");
                        } catch (Exception e) {
                        }
                    }
                }, 1500);
            } else {
                if (isOpen == true & source == true & SerialPortPreferences.switching == true) {

                    isOpen = false;
                    source = false;
                    SerialPortPreferences.switching = false;
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {


                            try {
                                //btread.performClick();
                                PowerOperate.disableRIFID_Module_5Volt();
                                ToastUtils.showLong("已经关闭");
                            } catch (Exception e) {
                            }
                        }
                    }, 500);
                }

            }
        }
        if (isOpen == false & source == false & SerialPortPreferences.switching == true) {

            PowerOperate.enableRIFID_Module_5Volt();
            source = true;
            ToastUtils.showLong("开始读卡");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {


                    try {
                        // btread.performClick();
                        isOpen = true;

                    } catch (Exception e) {
                    }
                }
            }, 1500);
        }

    }

    /**
     * 设置
     */
    private void setting() {
        startActivity(new Intent(this, SerialPortPreferencesFinish.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_read:
                readSfCode();
                break;
        }
    }
}
