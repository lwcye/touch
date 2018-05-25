package android_serialport_api.sample.base;


import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.by100.util.RWCrashHandler;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;

import org.xutils.x;

import java.io.File;
import java.net.Proxy;

import android_serialport_api.sample.BuildConfig;

public class RWCrashApplication extends Application {
    public static SPUtils spUtils;
    public static RWCrashApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        RWCrashHandler crashHandler = RWCrashHandler.getInstance();
        crashHandler.init(this);
        application = this;
        init();
    }

    private void init() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);

        Utils.init(this);

        // TODO: lwc 2018/5/25 改LOG的配置
        LogUtils.getConfig().setLogSwitch(false);
        LogUtils.getConfig().setGlobalTag("cqcity");
        LogUtils.getConfig().setConsoleFilter(LogUtils.D);
        LogUtils.getConfig().setLog2FileSwitch(true);

        File file = new File(getExternalCacheDir() + "/crash");
        if (!file.exists()) {
            file.mkdir();
        }
        CrashUtils.init(file);

        spUtils = SPUtils.getInstance("config");

        // 文件下载器
        FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(10_000)
                        .readTimeout(10_000)
                        .proxy(Proxy.NO_PROXY)
                )));
    }
}
