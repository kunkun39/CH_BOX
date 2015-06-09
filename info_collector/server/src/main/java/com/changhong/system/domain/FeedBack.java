package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;
import org.joda.time.DateTime;

/**
 * Created by kerio on 2015/6/8.
 */
public class FeedBack extends EntityBase{
    private String content;
    private String status;
    private DateTime dateTime;
    private ClientUser clientUser;

    public FeedBack() {
    }

    public FeedBack(String content, String status, DateTime dateTime, ClientUser clientUser) {
        this.content = content;
        this.status = status;
        this.dateTime = dateTime;
        this.clientUser = clientUser;
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

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public ClientUser getClientUser() {
        return clientUser;
    }

    public void setClientUser(ClientUser clientUser) {
        this.clientUser = clientUser;
    }
}
