package com.hbln.touch.base;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.hbln.touch.BuildConfig;

import org.xutils.x;

import java.io.File;

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
public class BaseApplication extends Application {
    private static BaseApplication sBaseApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
        sBaseApplication = this;
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
    @SuppressLint("MissingPermission")
    private void init() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        LogUtils.getConfig().setLogSwitch(true);
        LogUtils.getConfig().setGlobalTag("cqcity");
        LogUtils.getConfig().setConsoleFilter(LogUtils.D);
        LogUtils.getConfig().setLog2FileSwitch(true);

        File file = new File(getExternalCacheDir() + "/crash");
        if (!file.exists()) {
            file.mkdir();
        }
        CrashUtils.init(file);
    }

    public static BaseApplication getInstance() {
        return sBaseApplication;
    }
}
