package com.changhong.system.web.facade.dto;

import com.changhong.common.utils.CHDateUtils;
import com.changhong.common.utils.CHStringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

/**
 * Created by kerio on 2015/6/26.
 */
public class AppDTO implements Serializable {
    private int id = -1;
    private String appname;
    private String appkey;
    private String appdes;
    private Date dateTime;

    public AppDTO() {
        this.appkey= getRandomApiKey(24);
    }

    public AppDTO(int id, String appname, String appkey, String appdes, Date dateTime) {
        this.id = id;
        this.appname = appname;
        this.appkey = appkey;
        this.appdes = appdes;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getappdes() {
        return appdes;
    }

    public void setappdes(String appdes) {
        this.appdes = appdes;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public static String getRandomApiKey(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }
}
