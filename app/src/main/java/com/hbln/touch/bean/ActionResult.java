package com.hbln.touch.bean;


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
public class ActionResult extends BaseActionResult {
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
