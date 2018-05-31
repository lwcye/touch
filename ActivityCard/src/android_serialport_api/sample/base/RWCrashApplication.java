package android_serialport_api.sample.base;


import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.Utils;
import com.by100.util.RWCrashHandler;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.tencent.smtt.sdk.QbSdk;

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
        boolean isLog = false;
        LogUtils.getConfig().setLogSwitch(isLog);
        LogUtils.getConfig().setLog2FileSwitch(isLog);
        LogUtils.getConfig().setGlobalTag("cqcity");
        LogUtils.getConfig().setConsoleFilter(LogUtils.D);

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

        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }

            @Override
            public void onCoreInitFinished() {
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(getApplicationContext(), cb);
    }
}
