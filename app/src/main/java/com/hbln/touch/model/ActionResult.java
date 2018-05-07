package com.hbln.touch.model;


import com.blankj.utilcode.util.TimeUtils;

/**
 * 内部组件的调用结果
 *
 * @author mos
 * @date 2017.04.13
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class ActionResult {
    /** 成功 */
    public static final int CODE_SUCCESS = 1000;
    /** 成功 -- 异步回调 */
    public static final int CODE_SUCCESS_ASYNC_RESULT = 1010;
    /** 错误(通用) */
    public static final int CODE_ERROR = 1001;
    /** 参数错误 */
    public static final int CODE_PARAM_ERROR = 1002;
    /** URL不可用 */
    public static final int CODE_URL_NOT_AVAILABLE = 1003;
    /** 用户未登录 */
    public static final int CODE_USER_NOT_LOGIN = 1004;

    /** 代码 */
    public int code;
    /** 消息 */
    public String result;
    /** 时间 */
    public String date;

    /**
     * 构造函数
     *
     * @param code 代码
     * @param result 消息
     */
    public ActionResult(int code, String result) {
        this.code = code;
        this.result = result;
        this.date = TimeUtils.getNowString();
    }
}
