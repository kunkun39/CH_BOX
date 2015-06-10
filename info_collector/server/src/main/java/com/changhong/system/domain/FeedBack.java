package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by kerio on 2015/6/8.
 */
public class FeedBack extends EntityBase{
    private String content;
    private String status;
    private ClientUser clientUser;
    private int year;
    private int month;
    private int day;

    public FeedBack() {
    }

    public FeedBack(String content, String status, ClientUser clientUser, int year, int month, int day) {
        this.content = content;
        this.status = status;
        this.clientUser = clientUser;
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
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

    public ClientUser getClientUser() {
        return clientUser;
    }

    public void setClientUser(ClientUser clientUser) {
        this.clientUser = clientUser;
    }
}
