package com.byid.utils;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.blankj.utilcode.util.DeviceUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.byid.activity.BrowserActivity;
import com.byid.model.ApiBean;
import com.byid.model.ConfigBean;
import com.byid.model.SyncBean;
import com.byid.model.UpdateBean;
import com.byid.model.UserInfoBean;
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
    //mac
    public static final String MAC_KEY = "mac";
    //mac
    public static final String MAC_VALUE = DeviceUtils.getMacAddress();
    //更新状态
    public static final String URL_CHANGE_STATUS = "/amain/romstatus";
    //获得配置文件
    public static final String URL_CONFIG = "/logintoken/getrooturl";
    /** 获取该触摸屏的api */
    private static final String URL_API = "/amain/peizhiinfo";
    //url 同步数据
    private static final String URL_SYNC = "/amain/syncinfo";
    //更新APK
    private static final String URL_UPDATE_ROM = "/amain/updaterom";
    //config time
    private static final String SP_HOST = "host";
    //config time
    private static final String SP_TIME_SYNC = "time_sync";
    //config time
    private static final String SP_TIME_CONFIG = "time_config";
    //登录接口
    private static final String SP_API_URL = "api_url";
    //触摸屏的IP
    private static final String SP_CM_WEB = "cm_web";
    //触摸屏的TOKEN
    private static final String SP_CM_TOKEN = "cm_token";
    //Token
    private static final String TOKEN_KEY = "api_token";
    private static final String TOKEN_VALUE = "HBAPI@20180517";

    private static final HttpUtil ourInstance = new HttpUtil();

    //HOST
    private String HTTP_HOST = "";
    //触摸屏的登陆API
    private String API_UTL = "";
    //触摸屏的WEB地址
    private String CM_WEB = "";
    //触摸屏的TOKEN
    private String CM_TOKEN = "";
    //同步的请求
    private Subscription mSyncObserver;
    //配置的请求
    private Subscription mConfigObserver;

    private HttpUtil() {
        HTTP_HOST = RWCrashApplication.spUtils.getString(SP_HOST, "http://223.113.65.26:10080/newswulian/public/api");
        API_UTL = RWCrashApplication.spUtils.getString(SP_API_URL, "http://125.62.26.233/cqfengjie");
        CM_WEB = RWCrashApplication.spUtils.getString(SP_CM_WEB, "http://125.62.26.233/cqfengjiechumo");
        CM_TOKEN = RWCrashApplication.spUtils.getString(SP_CM_TOKEN, "HBAPI@20180516fengjie");
    }

    public static HttpUtil getInstance() {
        return ourInstance;
    }

    /**
     * 请求同步
     *
     * @param time 时间
     * @param b 是否强制更新
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
     * @param b 是否强制更新
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
                    requestApi();
                }
            }

            @Override
            void onFail(Throwable ex) {
                LogUtils.e(ex.getMessage());
            }
        });
    }

    /**
     * 获得API
     */
    public void requestApi() {
        RequestParams params = new RequestParams(HTTP_HOST + URL_API);
        params.addBodyParameter(TOKEN_KEY, TOKEN_VALUE);
        params.addBodyParameter(MAC_KEY, MAC_VALUE);
        x.http().post(params, new StringCallback() {
            @Override
            void onNext(String result) {
                LogUtils.d(result);
                ApiBean apiBean = new Gson().fromJson(result, ApiBean.class);
                if (apiBean.status == 1 && !TextUtils.isEmpty(apiBean.info.apiurl)) {
                    RWCrashApplication.spUtils.put(SP_API_URL, apiBean.info.apiurl);
                }
                if (apiBean.status == 1 && !TextUtils.isEmpty(apiBean.info.weburl)) {
                    RWCrashApplication.spUtils.put(SP_CM_WEB, apiBean.info.weburl);
                }
                if (apiBean.status == 1 && !TextUtils.isEmpty(apiBean.info.token)) {
                    RWCrashApplication.spUtils.put(SP_CM_TOKEN, apiBean.info.token);
                }
            }

            @Override
            void onFail(Throwable ex) {
                LogUtils.e(ex.getMessage());
            }
        });
    }

    /**
     * 获得配置文件
     */
    public void login(String username, String password, Activity activity) {
        if (TextUtils.isEmpty(username)) {
            return;
        }
        if (TextUtils.isEmpty(password)) {
            int length = username.length();
            password = username.substring(length - 6, length);
        }
        RequestParams params = new RequestParams(API_UTL + "/public/api/logintoken/login");
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);
        params.addBodyParameter(TOKEN_KEY, CM_TOKEN);
        LogUtils.e(params.getUri() + new Gson().toJson(params.getQueryStringParams()));
        x.http().post(params, new StringCallback() {
            @Override
            void onNext(String result) {
                LogUtils.e("result" + result);
                Gson gson = new Gson();
                UserInfoBean userInfoBean = gson.fromJson(result, UserInfoBean.class);
                if (userInfoBean.status == 1) {

                    String url = CM_WEB + "/#/home/myMoney/huiminzijin?userinfo=" + gson.toJson(gson.fromJson(userInfoBean.info.toString(), UserInfoBean.InfoBean.class));
                    LogUtils.e(url);
                    activity.startActivity(new Intent(activity, BrowserActivity.class)
                            .putExtra(BrowserActivity.PARAM_URL, url));
                } else {
                    ToastUtils.showShort(userInfoBean.info.toString());
                }
            }

            @Override
            void onFail(Throwable ex) {
                ToastUtils.showShort("网络错误");
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
