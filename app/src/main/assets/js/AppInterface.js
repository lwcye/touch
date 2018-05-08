(function() {

     function connectWebViewJavascriptBridge(callback) {
                if (window.WebViewJavascriptBridge) {
                    callback(WebViewJavascriptBridge)
                } else {
                    document.addEventListener(
                        'WebViewJavascriptBridgeReady'
                        , function() {
                            callback(WebViewJavascriptBridge)
                        },
                        false
                    );
                }
            }

            connectWebViewJavascriptBridge(function(bridge) {
                bridge.init(function(message, responseCallback) {
                    var data = {
                        'Javascript Responds': '测试中文!'
                    };
                    if (responseCallback) {
                        responseCallback(data);
                    }
                });
            })

})();