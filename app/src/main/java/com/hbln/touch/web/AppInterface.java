package com.hbln.touch.web;

import android.annotation.SuppressLint;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.hbln.touch.model.ActionResult;
import com.hbln.touch.web.result.AppInfoResult;
import com.hbln.touch.web.result.PhoneInfoResult;

/**
 * Js对象注入实现
 *
 * @author mos
 * @date 2017.07.18
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class AppInterface {
    /** 实例 */
    private static AppInterface sOurInstance = new AppInterface();

    /**
     * 私有化构造函数
     */
    private AppInterface() {
    }

    /**
     * 获取实例
     *
     * @return 获取实例
     */
    public static AppInterface getInstance() {
        return sOurInstance;
    }

    /**
     * 清除回调
     */
    public AppInterface clear() {
        WebViewManager manager = WebViewManager.getInstance();
        manager.clearJsCallbacks();

        return this;
    }

    /**
     * 注册native调用
     */
    public AppInterface register() {
        WebViewManager manager = WebViewManager.getInstance();

        /*
         * 注册Js到Native的调用函数
         */
        // 获取app信息
        manager.addJsCallback("getAppInfo", new GetAppInfo());
        // 获取手机信息
        manager.addJsCallback("getPhoneInfo", new GetPhoneInfo());
        return this;
    }

    /**
     * 添加js回调
     *
     * @param funcName 函数名称
     * @param callback 回调函数
     * @note 若回调需要context, 则在外面注册。
     */
    public void addJsCallback(String funcName, WebViewManager.JsCallback callback) {
        WebViewManager.getInstance().addJsCallback(funcName, callback);
    }

    /**
     * 获取app信息
     */
    private class GetAppInfo extends WebViewManager.JsCallback<Object, AppInfoResult> {

        /**
         * 构造函数
         */
        public GetAppInfo() {
            super(Object.class, THREAD_MODE_MAIN);
        }

        @Override
        public AppInfoResult onJsCallback(Object data) {
            AppInfoResult result = new AppInfoResult(ActionResult.CODE_SUCCESS, "success");
            result.data = new AppInfoResult.AppInfo();
            result.data.versionCode = AppUtils.getAppVersionCode();
            result.data.versionName = AppUtils.getAppVersionName();

            return result;
        }
    }

    /**
     * 获取电话信息
     */
    private class GetPhoneInfo extends WebViewManager.JsCallback<Object, PhoneInfoResult> {

        /**
         * 构造函数
         */
        public GetPhoneInfo() {
            super(Object.class, THREAD_MODE_MAIN);
        }

        @SuppressLint("MissingPermission")
        @Override
        public PhoneInfoResult onJsCallback(Object param) {
            PhoneInfoResult result = new PhoneInfoResult(ActionResult.CODE_SUCCESS, "success");
            result.data = new PhoneInfoResult.PhoneInfo();
            result.data.imei = PhoneUtils.getIMEI();
            result.data.imsi = PhoneUtils.getIMSI();

            return result;
        }
    }
}
