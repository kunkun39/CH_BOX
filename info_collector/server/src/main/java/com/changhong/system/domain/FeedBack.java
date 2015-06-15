package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by kerio on 2015/6/8.
 */
public class FeedBack extends EntityBase{
    private String content;
    private String status;
    private String usermac;
    private String year;
    private String month;
    private String day;

    public FeedBack() {
    }

    public FeedBack(String content, String status, String usermac, String year, String month, String day) {
        this.content = content;
        this.status = status;
        this.usermac = usermac;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsermac() {
        return usermac;
    }

    public void setUsermac(String usermac) {
        this.usermac = usermac;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
