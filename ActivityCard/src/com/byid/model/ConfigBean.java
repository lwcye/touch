package com.byid.model;

/**
 * Created by 41569 on 2018/5/19.
 */

public class ConfigBean {
    public int status;
    public InfoBean info;

    public static class InfoBean {
        public String url;
        public int synctime;
        public int peizhitime;
    }
}