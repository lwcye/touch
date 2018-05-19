package com.byid.model;

/**
 * Created by 41569 on 2018/5/19.
 */

public class SyncBean {
    public int status;
    public InfoBean info;

    public static class InfoBean {
        public int id;
        public String mac;
        public String title;
        public String tname;
        public String townsid;
        public String apiurl;
        public int status;
        public int is_update;
        public int nums;
        public String create_time;
        public int update_time;
        public String beizhu;
    }
}