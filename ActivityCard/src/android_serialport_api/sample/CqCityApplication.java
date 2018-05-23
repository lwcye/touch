package com.cqmc.cqcity.app;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.baidu.mapapi.SDKInitializer;
import com.cqmc.bus.master.network.BmApi;
import com.cqmc.bus.master.network.BmSearchApi;
import com.cqmc.cityhot.network.ChOpenPlatApi;
import com.cqmc.citynews.network.CnOpenPlatApi;
import com.cqmc.cqcity.BuildConfig;
import com.cqmc.cqcity.R;
import com.cqmc.cqcity.constant.DynamicValues;
import com.cqmc.cqcity.constant.ENVs;
import com.cqmc.cqcity.constant.URLs;
import com.cqmc.cqcity.receiver.NotificationReceiver;
import com.cqmc.cqcity.router.ExportRouter;
import com.cqmc.cqcity.sp.CqcPreference;
import com.cqmc.cqcity.sp.UserPreference;
import com.cqmc.cqcity.ui.activity.LoginActivity;
import com.cqmc.cqco2oclient.network.O2OOpenPlatApi;
import com.cqmc.cqco2oclient.router.O2OExportRouter;
import com.cqmc.libdev.common.base.BaseActivity;
import com.cqmc.libdev.common.base.BaseApplication;
import com.cqmc.libdev.common.network.retrofit.RetrofitWrapper;
import com.cqmc.libdev.common.router.core.AppRouter;
import com.cqmc.libdev.common.service.UriProviderService;
import com.cqmc.libdev.common.utils.SimpleTitleBarBuilder;
import com.cqmc.libdev.common.utils.UriProviderHelper;
import com.cqmc.libdev.common.utils.loader.IOnImageLoadListener;
import com.cqmc.libdev.common.utils.loader.LoaderFactory;
import com.cqmc.libdev.common.utils.loader.Options;
import com.cqmc.libdev.cqcity.constant.URLConst;
import com.cqmc.libdev.cqcity.network.OpenPlatApi;
import com.cqmc.libdev.cqcity.network.StatisticApi;
import com.cqmc.libdev.cqcity.network.bean.LoginInfo;
import com.cqmc.libdev.cqcity.network.exception.SessionStatusException;
import com.cqmc.libdev.cqcity.network.interceptor.SessionInterceptor;
import com.cqmc.libdev.cqcity.network.service.UserLoginService;
import com.cqmc.libdev.cqcity.service.StatisticAnalysisService;
import com.cqmc.libdev.cqcity.service.XmppService;
import com.cqmc.libdev.cqcity.utils.BaiduLocationUtil;
import com.cqmc.opsrc.blankj.utilcode.util.AppUtils;
import com.cqmc.opsrc.blankj.utilcode.util.ConvertUtils;
import com.cqmc.opsrc.blankj.utilcode.util.DeviceUtils;
import com.cqmc.opsrc.blankj.utilcode.util.LogUtils;
import com.cqmc.opsrc.blankj.utilcode.util.PhoneUtils;
import com.cqmc.opsrc.blankj.utilcode.util.ResUtils;
import com.cqmc.payment.network.PayOpenPlatApi;
import com.cqmc.violation.query.network.VqGateWayApi;
import com.cqmc.violation.query.network.VqNotGateWayApi;
import com.cqmc.violation.query.network.VqTrafficGuideApi;
import com.cqmc.yek.Yek;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechUtility;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.services.DownloadMgrInitialParams;
import com.meituan.android.walle.WalleChannelReader;
import com.morgoo.droidplugin.PluginHelper;
import com.umeng.socialize.PlatformConfig;

import java.net.Proxy;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;

/**
 * O2O客户端应用
 *
 * @author mos
 * @date 2017.02.21
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified wuao
 * @date 2017.06.25
 * @note 添加百度地图定位服务初始化
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class CqCityApplication extends BaseApplication {
    /** 请求码 --  */
    private Object mSessionLock = new Object();

    @Override
    public void onCreate() {
        super.onCreate();

        // 日志初始化
        // TODO: lwc 2017/11/30 打包的时候一定要检查是否输出Log
        LogUtils.init(AppUtils.isAppDebug(), false, 'v', "cqcity");

        // 初始化数值
        initValues();

        // 网络初始化
        RetrofitWrapper.init(this);

        // 文件下载器
        FileDownloader.init(this, new DownloadMgrInitialParams.InitCustomMaker()
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        .connectTimeout(10_000)
                        .readTimeout(10_000)
                        .proxy(Proxy.NO_PROXY)
                )));

        // 百度地图定位服务初始化
        initLocationService();

        // 百度地图初始化
        SDKInitializer.initialize(this);

        // 初始化标题栏
        initSimpleTitleBarOptions();

        // 图片加载初始化
        initImageLoader();

        // 数据库初始化
        initDb();

        // 注册路由
        registerRouter();

        // 注册数据共享
        registerUriProvider();

        // 友盟社会化分享初始化
        initSharePlatform();

        // 初始化网络
        initNetwork();

        // 初始化服务
        initService();

        // 这里必须在super.onCreate方法之后，顺序不能变
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());

        // 检查用户登录情况，未登录跳转登陆界面
        setCheckLoginInterceptor(new BaseActivity.ICheckLoginInterceptor() {
            @Override
            public boolean checkLoginBeforeAction(String actionAfterLogin, Bundle data) {
                if (!UserPreference.getInstance().getIsLogin()) {
                    AppRouter.getInstance()
                            .createActivityRouter("router.app.login")
                            .route();
                }
                return UserPreference.getInstance().getIsLogin();
            }
        });

        //注册Activity的生命周期监听,目前用来调试Activity的生命周期，以后可能会用
        //registerActivityLifecycleCallbacks();

        Setting.setShowLog(false);
        SpeechUtility.createUtility(this, "appid=59b104de");
    }

    /**
     * 注册Activity的生命周期监听,目前用来调试Activity的生命周期
     */
    private void registerActivityLifecycleCallbacks() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivityStarted(Activity activity) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivityResumed(Activity activity) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivityPaused(Activity activity) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivityStopped(Activity activity) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                LogUtils.d(activity.getComponentName().getShortClassName());
            }
        });
    }

    /**
     * 百度地图初始化定位服务
     */
    private void initLocationService() {
        // 获取实例，以进行初始化
        BaiduLocationUtil.getInstance();
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // 停止服务
        stopService();
    }

    /**
     * 初始化数值
     */
    private void initValues() {
        // 初始化动态密钥
        com.cqmc.libdev.cqcity.constant.DynamicValues.initKeys(Yek.getDesKey(), Yek.getRsaPubKey());

        // 初始化渠道值
        String channel = WalleChannelReader.getChannel(CqCityApplication.getInstance());
        com.cqmc.libdev.cqcity.constant.DynamicValues.setChannel(channel != null ? channel : "");
        // 初始化平台名
        com.cqmc.libdev.cqcity.constant.DynamicValues.setPlatform(ENVs.PLATFORM_NAME);
    }

    /**
     * 数据库初始化
     */
    private void initDb() {
        // 主工程数据库初始化
        com.cqmc.cqcity.db.DbWrapper.init(this);
        // 重庆城支撑库数据库初始化
        com.cqmc.libdev.cqcity.db.DbWrapper.init(this);
        // 公交大师数据库初始化
        com.cqmc.bus.master.db.BmDbWrapper.init(this);
    }


    /**
     * 初始化分享账号密码
     */
    private void initSharePlatform() {
        PlatformConfig.setWeixin(BuildConfig.weChatId, BuildConfig.weChatKey);
        PlatformConfig.setQQZone(BuildConfig.qqId, BuildConfig.qqKey);
        PlatformConfig.setSinaWeibo(getString(R.string.sina_key), getString(R.string.sina_secret), getString(R.string.sina_redirect_url));
    }

    /**
     * 初始化图片加载器
     */
    private void initImageLoader() {
        LoaderFactory.getLoader().init(this, new Options(R.drawable.img_loading_default, R.drawable.img_loading_fail));
    }

    /**
     * 注册路由
     */
    private void registerRouter() {
        // 公共库路由导出
        com.cqmc.libdev.common.router.ExportRouter.registerRouter();

        // 项目支撑库路由导出
        com.cqmc.libdev.cqcity.router.ExportRouter.registerRouter();

        // 主工程路由导出
        com.cqmc.cqcity.router.ExportRouter.registerRouter();

        // 违章查询模块路由导出
        com.cqmc.violation.query.router.ExportRouter.registerRouter();

        // 公交大师模块路由导出
        com.cqmc.bus.master.router.ExportRouter.registerRouter();

        // 会员中心路由导出
        com.cqmc.payment.router.ExportRouter.registerRouter();

        // 宽带服务 路由注册
        O2OExportRouter.registerRouter();

        // 会员中心 路由注册
        com.cqmc.payment.router.ExportRouter.registerRouter();
    }

    /**
     * 注册数据共享
     */
    private void registerUriProvider() {
        // 帮助类注册
        UriProviderHelper.initProviderPackage(AppUtils.getAppPackageName());

        // 主工程数据共享注册
        DynamicValues.registerUriProvider();
        // 公共库数据共享注册
        com.cqmc.libdev.common.constant.DynamicValues.registerUriProvider();

        // 向cqcity注册主框架监听
        com.cqmc.libdev.cqcity.constant.DynamicValues.setAppListener(new com.cqmc.libdev.cqcity.constant.DynamicValues.IAppListener() {
            @Override
            public boolean isLogin() {

                return DynamicValues.getUserInfo() != null;
            }

            @Override
            public String getUserId() {

                return UserPreference.getInstance().getUserId();
            }

            @Override
            public void getUserIcon(final com.cqmc.libdev.cqcity.constant.DynamicValues.IUserIconListener listener) {
                UserPreference.getInstance().loadUserIcon(new IOnImageLoadListener() {
                    @Override
                    public void onSuccess(Object model, Bitmap bitmap) {
                        listener.onGetUserIcon(bitmap);
                    }

                    @Override
                    public void onFail(Throwable throwable) {
                        listener.onGetUserIcon(ConvertUtils.drawable2Bitmap(ResUtils.getDrawable(R.drawable.ic_default_profile_image_male)));
                    }
                });
            }
        });
    }

    /**
     * 初始化网络
     */
    private void initNetwork() {
        // 平台URL
        OpenPlatApi.setUrl(URLs.CQMC_CQCITY_URL);
        // 统计服务URL
        StatisticApi.setUrl(URLs.CQMC_INTERFACE_SPECIFICATION_URL);
        // 违章查询过网关URL
        VqGateWayApi.setUrl(URLs.CQMC_CQCITY_URL);
        // 违章查询不过网关URL
        VqNotGateWayApi.setUrl(URLs.CQMC_VIOCATION_QUERY_NOT_GATE_WAY_URL);
        // 违章查询交通办事指南URL
        VqTrafficGuideApi.setUrl(URLs.CQMC_VIOCATION_QUERY_TRAFFIC_GUIDE_URL);
        // 公交大师URL
        BmApi.setUrl(URLs.CQMC_BUS_MASTER_URL);
        // 公交大师搜索数据URL
        BmSearchApi.setUrl(URLs.CQMC_BUS_MASTER_SEARCH_URL);
        // O2O-CLIENT 的 URL
        O2OOpenPlatApi.setUrl(URLs.CQMC_CQCITY_URL);
        // 会员订购中心老服务器URL
        PayOpenPlatApi.setUrl(URLs.CQMC_CQCITY_URL);
        // 小城趣事URL
        CnOpenPlatApi.setUrl(URLs.CQMC_CQCITY_URL);
        // 城市热度URL
        ChOpenPlatApi.setUrl(URLs.CQMC_CQCITY_URL);

        // 设置Session重新创建监听
        SessionInterceptor.setSessionCreateListener(buildSessionCreateListener());
        SessionInterceptor.setSessionSnListener(new SessionInterceptor.ISessionSnListener() {
            @Override
            public String onGetSn() {
                return UserPreference.getInstance().getSn();
            }
        });
    }

    /**
     * 初始化任务
     */
    private void initService() {
        // 数据共享服务
        startService(new Intent(this, UriProviderService.class));
        // 统计服务
        startService(new Intent(this, StatisticAnalysisService.class));
        // 即时通讯
        XmppService.init(URLs.CQMC_XMPP_DOMAIN, URLs.CQMC_XMPP_HOST, Integer.valueOf(URLs.CQMC_XMPP_PORT));
        XmppService.setNotificationConfig(R.mipmap.ic_launcher, NotificationReceiver.class);
        XmppService.setPushIdListener(createPushIdListener());
        XmppService.setEnableNotification(UserPreference.getInstance().getXmppNotification());
        startService(new Intent(this, XmppService.class));
    }

    /**
     * 创建push id监听
     *
     * @return 监听
     */
    private XmppService.IPushIdListener createPushIdListener() {

        return new XmppService.IPushIdListener() {
            @Override
            public void updateMaxPushId(String pushId) {
                if (TextUtils.isEmpty(pushId)) {

                    return;
                }

                // 与本地最大push id对比
                Long curMaxId = Long.valueOf(CqcPreference.getInstance().getSP().getString(CqcPreference.MAX_PUSH_ID, "0"));
                Long cmpMaxId = Long.valueOf(pushId);
                if (cmpMaxId > curMaxId) {
                    CqcPreference.getInstance().getSP().putString(CqcPreference.MAX_PUSH_ID, pushId);
                }
            }

            @Override
            public String getMaxPushId() {

                return CqcPreference.getInstance().getSP().getString(CqcPreference.MAX_PUSH_ID, "0");
            }
        };
    }

    /**
     * 停止服务
     */
    private void stopService() {
        stopService(new Intent(this, StatisticAnalysisService.class));
        stopService(new Intent(this, UriProviderService.class));
    }

    /**
     * 初始化标题栏的参数
     */
    private void initSimpleTitleBarOptions() {
        SimpleTitleBarBuilder.DefaultOptions options = new SimpleTitleBarBuilder.DefaultOptions();

        // 背景
        options.backgroundColor = ResUtils.getColor(R.color.top_bar_background);
        options.titleBarHeight = getResources().getDimensionPixelSize(R.dimen.top_bar_height);

        // 左边
        options.leftTextColor = ResUtils.getColor(R.color.top_bar_text_color);
        options.leftTextSize = ConvertUtils.px2sp(getResources().getDimensionPixelSize(R.dimen.font_small));
        options.leftBackDrawable = R.drawable.ic_back_gray;

        // 右边
        options.rightTextColor = ResUtils.getColor(R.color.top_bar_text_color);
        options.rightTextSize = ConvertUtils.px2sp(getResources().getDimensionPixelSize(R.dimen.font_small));

        // 标题
        options.titleColor = ResUtils.getColor(R.color.top_bar_title_color);
        options.titleSize = ConvertUtils.px2sp(getResources().getDimensionPixelSize(R.dimen.font_huge));

        // 阴影
        options.shadowHeight = (int) ResUtils.getDimensionPixelSize(R.dimen.top_bar_shadow_height);
        options.shadowColor = ResUtils.getColor(R.color.top_bar_shadow_color);

        // 6.0一下系统不使用沉浸式状态栏
        options.immersive = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

        SimpleTitleBarBuilder.initDefaultOptions(options);
    }

    /**
     * 创建Session重建监听
     *
     * @return 重建监听
     */
    private SessionInterceptor.ISessionCreateListener buildSessionCreateListener() {
        return new SessionInterceptor.ISessionCreateListener() {
            @Override
            public <T> Observable<T> onCreateSession() {
                UserLoginService uls = OpenPlatApi.getUserLoginService();
                String sn = UserPreference.getInstance().getSn();

                if (!TextUtils.isEmpty(sn)) {
                    /*
                     * 重新创建会话由于需要同步锁，故需改成同步方式
                     *
                    return uls.loginSn(sn, PhoneUtils.getIMSI(), PhoneUtils.getIMEI(), ENVs.PLATFORM_NAME, DeviceUtils.getModel(),
                            String.valueOf(AppUtils.getAppVersionCode()), AppUtils.getAppVersionName(), com.cqmc.libdev.cqcity.constant
                            .DynamicValues.getChannel())
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.io())
                            .flatMap(new Func1<LoginInfo, Observable<T>>() {
                                @Override
                                public Observable<T> call(LoginInfo data) {
                                    if (data.code == URLConst.CODE_OK) {
                                        // 保存登录信息
                                        UserPreference.getInstance().saveLoginInfo(data.data, null);

                                        throw new SessionStatusException(SessionStatusException.STATUS_CREATED);
                                    } else if (data.code == URLConst.CODE_SN_OVER_TIME) {
                                        // 缓存的sn值已过期，先清除登录信息，再拉起登陆界面
                                        UserPreference.getInstance().clearLoginInfo(null);
                                        AppRouter.getInstance()
                                                .createRouter(ExportRouter.ACTIVITY_APP_LOGIN)
                                                .route();
                                        throw new SessionStatusException(SessionStatusException.STATUS_CREATE_FAILED);
                                    }

                                    throw new SessionStatusException(SessionStatusException.STATUS_CREATE_FAILED);
                                }
                            });
                    */
                    synchronized (mSessionLock) {
                        // 首先判断上次重建Session的时间，避免多次重复创建
                        long lastSessionTime = UserPreference.getInstance().getSP().getLong(UserPreference.RECREATE_SESSION_TIME, 0);
                        if (System.currentTimeMillis() - lastSessionTime <= ENVs.RECREATE_SESSION_MIN_INTERVAL) {
                            // 上次创建的Session还在有效时间内
                            throw new SessionStatusException(SessionStatusException.STATUS_CREATED);

                        } else {
                            // 上次Session已过有效时间
                            Response<LoginInfo> response;
                            try {
                                Call<LoginInfo> loginInfoCall = uls.loginSnWithCall(sn,
                                        PhoneUtils.getIMSI(), PhoneUtils.getIMEI(), ENVs.PLATFORM_NAME, DeviceUtils.getModel(),
                                        String.valueOf(AppUtils.getAppVersionCode()), AppUtils.getAppVersionName(),
                                        com.cqmc.libdev.cqcity.constant.DynamicValues.getChannel());
                                response = loginInfoCall.execute();
                            } catch (Exception e) {
                                // 网络请求异常
                                LogUtils.d(e);
                                throw new SessionStatusException(SessionStatusException.STATUS_CREATE_FAILED);
                            }

                            if (response.isSuccessful()) {
                                LoginInfo data = response.body();
                                if (data.code == URLConst.CODE_OK) {
                                    // 保存登录信息
                                    UserPreference.getInstance().saveLoginInfo(data.data, null);

                                    // 保存最后一次创建Session的时间
                                    UserPreference.getInstance().getSP().putLong(UserPreference.RECREATE_SESSION_TIME, System.currentTimeMillis());

                                    throw new SessionStatusException(SessionStatusException.STATUS_CREATED);
                                } else if (data.code == URLConst.CODE_SN_OVER_TIME || data.code == URLConst.CODE_SN_LOGIN_FROM_OTHER) {
                                    // 缓存的sn值已过期，先清除登录信息，再拉起登陆界面
                                    UserPreference.getInstance().clearLoginInfo(null);
                                    AppRouter.Router router = AppRouter.getInstance()
                                            .createRouter(ExportRouter.ACTIVITY_APP_LOGIN);

                                    // 被其他手机挤下线后，提示用户
                                    if (data.code == URLConst.CODE_SN_LOGIN_FROM_OTHER) {
                                        router.addParam(LoginActivity.EXTRA_LOGIN_REASON, data.result);
                                    }
                                    router.route();

                                    throw new SessionStatusException(SessionStatusException.STATUS_CREATE_FAILED);
                                }
                            } else {
                                // 网络请求失败
                                throw new SessionStatusException(SessionStatusException.STATUS_CREATE_FAILED);
                            }
                        }
                    }
                } else {
                    LogUtils.d("try to create session but sn is empty");

                    // 没有sn，则拉起登录界面
                    AppRouter.getInstance()
                            .createRouter("router.app.login")
                            .route();
                }

                return null;
            }
        };
    }
}
