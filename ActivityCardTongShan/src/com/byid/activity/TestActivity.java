package com.byid.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.byid.android.ByIdActivity;
import com.wits.autoonoff.AutoOnoffActivity;

import android_serialport_api.sample.R;
import android_serialport_api.sample.SerialPortPreferences;

/**
 * Created by 41569 on 2018/5/17.
 */

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        findViewById(R.id.btn_read_code).setOnClickListener(this);
        findViewById(R.id.btn_shutdown).setOnClickListener(this);
        findViewById(R.id.btn_request).setOnClickListener(this);
        findViewById(R.id.btn_home).setOnClickListener(this);

        LogUtils.e("mac" + DeviceUtils.getMacAddress());
        LogUtils.e("CPU 1" + Build.CPU_ABI);
        LogUtils.e("CPU 2" + Build.CPU_ABI2);
        LogUtils.e("DensityDpi" + ScreenUtils.getScreenDensityDpi());
        LogUtils.e("getScreenDensity" + ScreenUtils.getScreenDensity());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_read_code:
                startActivity(new Intent(this, ByIdActivity.class));
                break;
            case R.id.btn_shutdown:
                startActivity(new Intent(this, AutoOnoffActivity.class));
                break;
            case R.id.btn_request:
                startActivity(new Intent(this, SerialPortPreferences.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.btn_home:
                startActivity(new Intent(this, HomeActivity.class));
                break;
        }
    }
}
