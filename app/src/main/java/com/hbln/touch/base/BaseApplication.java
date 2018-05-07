package com.hbln.touch.base;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

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
    private void init() {
        LogUtils.getConfig().setLogSwitch(true);
        LogUtils.getConfig().setGlobalTag("cqcity");
        LogUtils.getConfig().setConsoleFilter(LogUtils.D);
        LogUtils.getConfig().setLog2FileSwitch(true);

    }

    public static BaseApplication getInstance() {
        return sBaseApplication;
    }
}
