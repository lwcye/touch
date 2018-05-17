package com.cmcc.myinstallapk.base;

import android.annotation.SuppressLint;
import android.app.Application;

import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.cmcc.myinstallapk.BuildConfig;

import org.xutils.x;

import java.io.File;

/**
 * Created by 41569 on 2018/5/17.
 */

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @SuppressLint("MissingPermission")
    private void init() {

        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.

        Utils.init(this);
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
}
