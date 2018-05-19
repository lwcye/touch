package com.byid.model;

/**
 * Created by 41569 on 2018/5/19.
 */

public class UpdateBean {
    public int status;
    public InfoBean info;

    public static class InfoBean {
        public int id;
        public String title;
        public String rompath;
    }
}