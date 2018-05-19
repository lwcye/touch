package com.byid.utils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.byid.model.ConfigBean;
import com.byid.model.SyncBean;
import com.byid.model.UpdateBean;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.concurrent.TimeUnit;

import android_serialport_api.sample.base.RWCrashApplication;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by 41569 on 2018/5/19.
 */
public class HttpUtil {
    //HOST
    private String HTTP_HOST = "http://223.113.65.26:10080/newswulian/public/api";
    //config time
    private static final String SP_HOST = "host";
    //config time
    private static final String SP_TIME_SYNC = "time_sync";
    //config time
    private static final String SP_TIME_CONFIG = "time_config";
    //Token
    private static final String TOKEN_KEY = "api_cmtoken";

    private static final String TOKEN_VALUE = "HBAPI@20180517";
    //mac
    public static final String MAC_KEY = "mac";
    //mac
    public static final String MAC_VALUE = DeviceUtils.getMacAddress();
    //url 同步数据
    private static final String URL_SYNC = "/amain/syncinfo";
    //更新APK
    private static final String URL_UPDATE_ROM = "/amain/updaterom";
    //更新状态
    public static final String URL_CHANGE_STATUS = "/amain/romstatus";
    //获得配置文件
    public static final String URL_CONFIG = "/logintoken/getrooturl";
    //同步的请求
    private Subscription mSyncObserver;
    //配置的请求
    private Subscription mConfigObserver;
    private static final HttpUtil ourInstance = new HttpUtil();

    public static HttpUtil getInstance() {
        return ourInstance;
    }

    private HttpUtil() {
    }

    /**
     * 请求同步
     *
     * @param time 时间
     * @param b    是否强制更新
     */
    public void sync(int time, boolean b) {
        if (mSyncObserver == null) {
            b = true;
        }
        if (time == RWCrashApplication.spUtils.getInt(SP_TIME_SYNC)) {
            if (!b) {
                return;
            }
        } else {
            RWCrashApplication.spUtils.put(SP_TIME_SYNC, time);
        }
        //如果在1s内重复点击，则取消订阅，以免重复读取
        if (null != mSyncObserver) {
            mSyncObserver.unsubscribe();
        }
        mSyncObserver = Observable
                .interval(time, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        HttpUtil.getInstance().requestSync();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e(throwable.getMessage());
                    }
                });
    }

    /**
     * 请求配置参数
     *
     * @param time 间隔时间
     * @param b    是否强制更新
     */
    public void getConfig(int time, boolean b) {
        if (mConfigObserver == null) {
            b = true;
        }
        if (time == RWCrashApplication.spUtils.getInt(SP_TIME_CONFIG)) {
            if (!b) {
                return;
            }
        } else {
            RWCrashApplication.spUtils.put(SP_TIME_CONFIG, time);
        }
        //如果在1s内重复点击，则取消订阅，以免重复读取
        if (null != mConfigObserver) {
            mConfigObserver.unsubscribe();
        }
        mConfigObserver = Observable
                .interval(time, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        HttpUtil.getInstance().requestConfig();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        LogUtils.e(throwable.getMessage());
                    }
                });
    }

    /**
     * 同步数据
     */
    public void requestSync() {
        RequestParams params = new RequestParams(HTTP_HOST + URL_SYNC);
        params.addBodyParameter(TOKEN_KEY, TOKEN_VALUE);
        params.addBodyParameter(MAC_KEY, MAC_VALUE);
        x.http().post(params, new StringCallback() {
            @Override
            void onNext(String result) {
                LogUtils.e(result);
                SyncBean syncBean = new Gson().fromJson(result, SyncBean.class);
                if (syncBean.status == 1 && syncBean.info.is_update == 1) {
                    requestUpdateRom();
                }
            }

            @Override
            void onFail(Throwable ex) {

            }
        });
    }

    /**
     * 升级APK
     */
    private void requestUpdateRom() {
        RequestParams params = new RequestParams(HTTP_HOST + URL_UPDATE_ROM);
        params.addBodyParameter(TOKEN_KEY, TOKEN_VALUE);
        params.addBodyParameter(MAC_KEY, MAC_VALUE);
        x.http().get(params, new StringCallback() {
            @Override
            void onNext(String result) {
                LogUtils.e(result);
                UpdateBean updateBean = new Gson().fromJson(result, UpdateBean.class);
                if (updateBean.status == 1) {
                    FileDownUtil.getInstance().downloadApk(updateBean.info.rompath);
                }
            }

            @Override
            void onFail(Throwable ex) {
            }
        });
    }

    /**
     * 改变状态
     */
    public void requestChangeStatus(final Action1<String> action1) {
        RequestParams params = new RequestParams(HTTP_HOST + URL_CHANGE_STATUS);
        params.addBodyParameter(TOKEN_KEY, TOKEN_VALUE);
        params.addBodyParameter(MAC_KEY, MAC_VALUE);
        x.http().post(params, new StringCallback() {
            @Override
            void onNext(String result) {
                if (action1 != null) {
                    action1.call(result);
                }
            }

            @Override
            void onFail(Throwable ex) {
            }
        });
    }

    /**
     * 获得配置文件
     */
    public void requestConfig() {
        RequestParams params = new RequestParams(HTTP_HOST + URL_CONFIG);
        params.addBodyParameter(TOKEN_KEY, TOKEN_VALUE);
        x.http().post(params, new StringCallback() {
            @Override
            void onNext(String result) {
                LogUtils.e(result);
                ConfigBean configBean = new Gson().fromJson(result, ConfigBean.class);
                if (configBean.status == 1) {
                    boolean b = resetHost(configBean.info.url);
                    LogUtils.e(b);
                    sync(configBean.info.synctime, b);
                    getConfig(configBean.info.peizhitime, b);
                }
            }

            @Override
            void onFail(Throwable ex) {
                LogUtils.e(ex.getMessage());
            }
        });
    }

    /**
     * 重新设置Host
     */
    private boolean resetHost(String host) {
        if (host.equalsIgnoreCase(RWCrashApplication.spUtils.getString(SP_HOST))) {
            return false;
        } else {
            HTTP_HOST = host;
            RWCrashApplication.spUtils.put(SP_HOST, host);
            return true;
        }
    }

    /**
     * 回调
     */
    abstract class StringCallback implements Callback.CommonCallback<String> {
        abstract void onNext(String result);

        abstract void onFail(Throwable ex);

        @Override
        public void onSuccess(String result) {
            onNext(result);
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            onFail(ex);
        }

        @Override
        public void onCancelled(CancelledException cex) {

        }

        @Override
        public void onFinished() {

        }
    }
}
