package com.hbln.touch.base;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2018/5/3
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class MyApplication extends Application {
    private static MyApplication myApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        myApplication = this;
        init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * 初始化
     */
    private void init() {
        LogUtils.getConfig().setLogSwitch(true);
        LogUtils.getConfig().setGlobalTag("cqcity");
        LogUtils.getConfig().setConsoleFilter(LogUtils.D);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            CrashUtils.init();
        }

    }

    public static MyApplication getInstance() {
        return myApplication;
    }
}
