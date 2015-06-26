package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by kerio on 2015/6/26.
 */
public class App extends EntityBase {
    private String appname;
    private String appkey;
    private String appdes;

    public App() {
    }

    public App(String appname, String appkey, String appdes) {
        this.appname = appname;
        this.appkey = appkey;
        this.appdes = appdes;
    }

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public String getAppdes() {
        return appdes;
    }

    public void setAppdes(String appdes) {
        this.appdes = appdes;
    }
}
