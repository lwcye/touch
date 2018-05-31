package com.byid.model;

/**
 * <p>describe</p><br>
 *
 * @author - lwc
 * @date - 2018/5/31
 * @note -
 * -------------------------------------------------------------------------------------------------
 * @modified -
 * @date -
 * @note -
 */
public class ApiBean {
    public int status;
    public Info info;

    public static class Info {
        public int id;
        public String title;
        /** 登陆的api */
        public String apiurl;
        /** 触摸屏的api */
        public String weburl;
        public String create_time;
        public String update_time;
        /** 登陆接口的token */
        public String token;
    }
}