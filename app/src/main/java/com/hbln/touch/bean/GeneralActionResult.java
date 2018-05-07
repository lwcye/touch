package com.hbln.touch.bean;

import com.blankj.utilcode.util.LogUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONObject;

/**
 * 内部组件的调用结果(通用)
 *
 * @author mos
 * @date 2017.04.13
 * @note 1. 用于String转为ActionResult子类的数据格式
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class GeneralActionResult extends ActionResult {
    /** 数据 */
    public JsonObject data;

    /**
     * 构造函数
     *
     * @param code 代码
     * @param result 消息
     */
    public GeneralActionResult(int code, String result) {
        super(code, result);
    }

    /**
     * 构造函数，解析json
     *
     * @param str 数据
     */
    public GeneralActionResult(String str) {
        super(CODE_ERROR, "parse error");

        try {
            JSONObject obj = new JSONObject(str);
            String code = obj.getString("code");
            String result = obj.getString("result");
            String date = obj.optString("date");
            JSONObject dataObj = obj.optJSONObject("data");

            this.code = Integer.valueOf(code);
            this.result = result;
            this.date = date;
            if (dataObj != null) {
                this.data = new JsonParser().parse(dataObj.toString()).getAsJsonObject();
            }
        } catch (Exception e) {
            LogUtils.d(e);
        }
    }
}
