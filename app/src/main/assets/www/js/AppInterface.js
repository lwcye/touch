(function() {
    /**
     * 获取App信息
     *
     * @param responseCallback 应答回调
     *
     * 应答：
     * {
     *     "code": xx,
     *     "msg": "xx",
     *     "data": {
     *         "versionCode": "xx",
     *         "versionName": "xx"
     *     }
     * }
     */
    function getAppInfo(responseCallback) {
        callNative(
            "getAppInfo",
            null,
            responseCallback
        );
    }

    /**
     * 获取手机信息
     *
     * @param responseCallback 应答回调
     *
     * 应答：
     * {
     *     "code": xx,
     *     "msg": "xx",
     *     "data": {
     *         "imei": "xx",
     *         "imsi": "xx"
     *     }
     * }
     */
    function getPhoneInfo(responseCallback) {
        callNative(
            "getPhoneInfo",
            null,
            responseCallback
        );
    }

    /**
     * 调用Native方法
     */
    function callNative(funcName, param, responseCallback) {
        // 检测对象是否创建
        if (window.WebViewJavascriptBridge) {
            window.WebViewJavascriptBridge.callHandler(
                funcName,
                param,
                responseCallback
            );

        } else {
            // 未创建则监听Ready消息
            document.addEventListener(
                'WebViewJavascriptBridgeReady'
                , function() {
                    window.WebViewJavascriptBridge.callHandler(
                        funcName,
                        param,
                        responseCallback
                    );
                },
                false
            );
        }
    }

    // 定义全局对象
    AppInterface = {
        getAppInfo: getAppInfo,
        getPhoneInfo: getPhoneInfo,
        callHandler: callNative
    };
})();