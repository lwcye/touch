package com.hbln.touch.web;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.blankj.utilcode.util.LogUtils;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.hbln.touch.base.BaseActivity;
import com.hbln.touch.base.MyApplication;
import com.hbln.touch.model.ActionResult;
import com.hbln.touch.model.JsPendingActionResult;
import com.hbln.touch.utils.JsonUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * WebView管理器
 *
 * @author mos
 * @date 2017.04.03
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class WebViewManager {
    /** 未处理 */
    public static int PROCESS_NOTHING = 0;
    /** 已处理 */
    public static int PROCESS_OK = 1;
    /** 实例 */
    private static WebViewManager sOurInstance = new WebViewManager();

    static {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            // 允许接收cookie
            CookieManager.getInstance().setAcceptCookie(true);
        }
    }

    /** JS回调函数映射 */
    private Map<String, JsCallback> mJs2NativeMap = new HashMap<>();

    /**
     * 私有化构造函数
     */
    private WebViewManager() {
    }

    /**
     * 获取实例
     *
     * @return 实例
     */
    public static WebViewManager getInstance() {
        return sOurInstance;
    }

    /**
     * 是否匹配开头的Url
     *
     * @param baseUrl 基准url
     * @param compareUrl 参与比较的url
     * @return true -- 匹配 false -- 不匹配
     * @note 同时支持http，https，以及去掉协议头的字符串
     */
    public static boolean matchStartUrl(String baseUrl, String compareUrl) {
        boolean match = false;
        if (TextUtils.isEmpty(baseUrl) || TextUtils.isEmpty(compareUrl)) {

            return match;
        }

        if (baseUrl.startsWith(compareUrl)) {
            match = true;
        } else if (baseUrl.startsWith("http://") && baseUrl.substring(7).startsWith(compareUrl)) {
            match = true;
        } else if (baseUrl.startsWith("https://") && baseUrl.substring(8).startsWith(compareUrl)) {
            match = true;
        }

        return match;
    }

    /**
     * 添加js回调
     *
     * @param funcName 函数名称
     * @param callback 回调函数
     * @return this
     */
    public WebViewManager addJsCallback(String funcName, JsCallback callback) {
        if (TextUtils.isEmpty(funcName) || callback == null) {

            return this;
        }
        mJs2NativeMap.put(funcName, callback);

        return this;
    }

    /**
     * 获取Js调用Native的映射
     *
     * @return 映射map
     */
    private Map<String, JsCallback> getJs2NativeMap() {

        return mJs2NativeMap;
    }

    /**
     * 清除所有回调
     */
    public void clearJsCallbacks() {
        mJs2NativeMap.clear();
    }

    /**
     * 在容器中创建WebView
     *
     * @param activity 页面
     * @param container 容器
     * @return web view对象
     */
    public BridgeWebView createWebView(BaseActivity activity, ViewGroup container) {
        if (container == null) {

            return null;
        }

        Context context = activity == null ? MyApplication.getInstance() : activity;
        final BridgeWebView webView = new BridgeWebView(context);
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        container.addView(webView);

        // 默认调用处理
        webView.setDefaultHandler(new BridgeHandler() {
            @Override
            public void handler(String data, CallBackFunction function) {
                // 暂不处理
            }
        });

        // 按键处理
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // 返回按键处理
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    if (webView.canGoBack()) {
                        webView.goBack();

                        return true;
                    }
                }

                return false;
            }
        });

        return webView;
    }

    /**
     * 注册Js回调
     *
     * @param webView BridgeWebView
     */
    public void registerJsCallback(BridgeWebView webView) {
        Map<String, JsCallback> js2NativeCallbacks = WebViewManager.getInstance().getJs2NativeMap();
        for (Map.Entry<String, JsCallback> entry : js2NativeCallbacks.entrySet()) {
            final String funcName = entry.getKey();
            final WebViewManager.JsCallback callback = entry.getValue();

            webView.registerHandler(funcName, new BridgeHandler() {
                @Override
                public void handler(final String data, final CallBackFunction function) {
                    // 此函数在主线程被回调
                    Observable.just(null)
                            .compose(new Observable.Transformer<Object, Object>() {
                                @Override
                                public Observable<Object> call(Observable<Object> objectObservable) {
                                    //  确定执行线程
                                    if (callback.getThreadMode() == JsCallback.THREAD_MODE_IO) {

                                        return objectObservable.subscribeOn(Schedulers.io());
                                    }

                                    return objectObservable;
                                }
                            })
                            .map(new Func1<Object, Object>() {
                                @Override
                                public Object call(Object o) {
                                    // 数据转对象，并回调
                                    Object innerResult;
                                    if (data == null) {
                                        innerResult = callback.onJsCallback(null);
                                    } else {
                                        Type paramType = callback.getType();
                                        Class paramClazz = callback.getTypeClass();
                                        if (paramType != null) {
                                            // 泛型
                                            innerResult = callback.onJsCallback(JsonUtil.stringToObject(data,
                                                    paramType));

                                        } else if (paramClazz != null) {
                                            // 普通类型
                                            innerResult = callback.onJsCallback(JsonUtil.stringToObject(data,
                                                    paramClazz));
                                        } else {
                                            // 其他
                                            innerResult = callback.onJsCallback(null);
                                        }
                                    }

                                    if (innerResult instanceof ActionResult) {
                                        // 同步输出结果
                                        return JsonUtil.objectToString(innerResult);

                                    } else if (innerResult instanceof JsPendingActionResult) {
                                        // 记录回调，且不输出结果
                                        ((JsPendingActionResult) innerResult).setCallback(function);

                                        return innerResult;

                                    } else {

                                        return null;
                                    }
                                }
                            })
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Object>() {
                                @Override
                                public void call(Object result) {
                                    if (result != null) {
                                        // 字符串结果
                                        if (result instanceof String) {
                                            function.onCallBack((String) result);
                                        }
                                    } else {
                                        ActionResult error = new ActionResult(ActionResult.CODE_ERROR, "internal error");
                                        function.onCallBack(JsonUtil.objectToString(error));
                                    }
                                }
                            }, new Action1<Throwable>() {
                                @Override
                                public void call(Throwable throwable) {
                                    LogUtils.d(throwable);
                                }
                            });
                }
            });
        }
    }

    /**
     * 通用处理Url
     *
     * @param view web view
     * @param url url
     * @return 参见处理结果(PROCESS_NOTHING等)
     */
    public int processShouldOverrideUrlLoading(WebView view, String url) {
        // 所有非http，https的schema，先尝试使用系统intent处理，若异常，再让子类处理。(yy是jsbridge的重定向)
        if (!url.startsWith("http") && !url.startsWith("https") && !url.startsWith("yy")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MyApplication.getInstance().startActivity(intent);
                return PROCESS_OK;

            } catch (Exception ignored) {
                LogUtils.d(ignored);
            }
        }

        return PROCESS_NOTHING;
    }

    /**
     * Js回调类
     *
     * @note T为Js调用Native时，传递的参数(JSON -> Class)
     */
    public static abstract class JsCallback<T, R extends ActionResult> {
        /** IO线程调度 */
        public static final int THREAD_MODE_IO = 0;
        /** 主线程调度 */
        public static final int THREAD_MODE_MAIN = 1;

        /** 类型 */
        private Class<T> mTypeClass;
        /** 类型(泛型使用) */
        private Type mTypeOfT;
        /** 调度线程类型 */
        private int mThreadMode;

        /**
         * 构造函数(泛型使用)
         *
         * @param typeOfT 回调参数type
         * @param threadMode 调度线程类型(参见THREAD_MODE_MAIN等)
         */
        public JsCallback(Type typeOfT, int threadMode) {
            mTypeOfT = typeOfT;
            mThreadMode = threadMode;
        }

        /**
         * 构造函数(泛型使用)
         *
         * @param typeOfT 回调参数type
         */
        public JsCallback(Type typeOfT) {
            this(typeOfT, THREAD_MODE_MAIN);
        }


        /**
         * 构造函数
         *
         * @param typeClass 回调参数类型
         * @param threadMode 调度线程类型(参见THREAD_MODE_MAIN等)
         */
        public JsCallback(Class<T> typeClass, int threadMode) {
            mTypeClass = typeClass;
            mThreadMode = threadMode;
        }

        /**
         * 构造函数
         *
         * @param typeClass 回调参数类型
         */
        public JsCallback(Class<T> typeClass) {
            mTypeClass = typeClass;
            mThreadMode = THREAD_MODE_MAIN;
        }

        /**
         * Js回调
         *
         * @param data js传递给native的数据
         * @return native处理后返回给js的数据
         * @note 子类重写此函数时，需要对参数进行判断，因为js调用可能传递参数错误
         */
        public abstract R onJsCallback(T data);

        /**
         * 获取类型
         *
         * @return 类型
         */
        public Class<T> getTypeClass() {

            return mTypeClass;
        }

        /**
         * 获取类型(泛型使用)
         *
         * @return 类型
         */
        public Type getType() {

            return mTypeOfT;
        }

        /**
         * 回去线程调度类型
         *
         * @return 调度类型
         */
        public int getThreadMode() {

            return mThreadMode;
        }
    }

    /**
     * 基于页面的Js回调类
     *
     * @note T为Js调用Native时，传递的参数(JSON -> Class)
     */
    public static abstract class ActivityJsCallback<T> extends JsCallback<T, ActionResult> {
        /** Activity弱引用 */
        private WeakReference<BaseActivity> mBaseActivityRef;

        /**
         * 构造函数
         *
         * @param activity activity
         * @param typeClass 模板参数T的class类型
         */
        public ActivityJsCallback(BaseActivity activity, Class typeClass) {
            super(typeClass);
            mBaseActivityRef = new WeakReference<>(activity);
        }

        /**
         * 构造函数
         *
         * @param activity activity
         * @param typeClass 类型
         * @param threadMode 调度线程类型(参见THREAD_MODE_MAIN等)
         */
        public ActivityJsCallback(BaseActivity activity, Class typeClass, int threadMode) {
            super(typeClass, threadMode);
            mBaseActivityRef = new WeakReference<>(activity);
        }

        /**
         * 获取Activity
         *
         * @return BaseActivity
         */
        public BaseActivity getActivity() {
            return mBaseActivityRef.get();
        }
    }
}
