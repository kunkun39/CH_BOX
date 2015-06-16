package com.changhong.system.web.facade.dto;

/**
 * Created by maren on 2015/6/15.
 */
public class TvChannelInfoDTO {
    private String tvChannelName;
    private String tvProgramName;
    private String appKey;
    private String userMac;
    private String year;
    private String month;
    private String day;
    private String hour;

    public TvChannelInfoDTO(String day, String hour, String month, String tvChannelName, String tvProgramName, String userMac, String year) {
        this.day = day;
        this.hour = hour;
        this.month = month;
        this.tvChannelName = tvChannelName;
        this.tvProgramName = tvProgramName;
        this.userMac = userMac;
        this.year = year;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

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

    public String getUserMac() {
        return userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
