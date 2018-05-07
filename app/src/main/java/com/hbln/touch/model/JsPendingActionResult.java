package com.hbln.touch.model;


import com.blankj.utilcode.util.LogUtils;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.hbln.touch.utils.JsonUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Js使用的内部组件的调用结果(即将发生)
 *
 * @author mos
 * @date 2017.12.04
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class JsPendingActionResult extends ActionResult {
    /** 回调 */
    private CallBackFunction mCallback;

    /**
     * 构造函数
     *
     * @param code 代码
     * @param result 消息
     */
    public JsPendingActionResult(int code, String result) {
        super(code, result);
    }

    /**
     * 设置回调
     *
     * @param callback 回调
     * @note 此函数仅供框架调用
     */
    public void setCallback(CallBackFunction callback) {
        mCallback = callback;
    }

    /**
     * 通知结果
     *
     * @param result 结果
     */
    public void notifyResult(ActionResult result) {
        if (mCallback != null) {
            Observable.just(result)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<ActionResult>() {
                        @Override
                        public void call(ActionResult result) {
                            mCallback.onCallBack(JsonUtil.objectToString(result));
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            LogUtils.d(throwable);
                        }
                    });
        }
    }
}
