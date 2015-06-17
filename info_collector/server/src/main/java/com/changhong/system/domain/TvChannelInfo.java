package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by maren on 2015/6/10.
 */
public class TvChannelInfo extends EntityBase{
    private String tvChannelName;
    private String tvProgramName;
    private String status;
    private String appKey;
    private String userMac;
    private String year;
    private String month;
    private String day;
    private String hour;

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUserMac() {
        return userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTvChannelName() {
        return tvChannelName;
    }

    public void setTvChannelName(String tvChannelName) {
        this.tvChannelName = tvChannelName;
    }

    public String getTvProgramName() {
        return tvProgramName;
    }

    public void setTvProgramName(String tvProgramName) {
        this.tvProgramName = tvProgramName;
    }
}
