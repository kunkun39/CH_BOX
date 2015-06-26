package com.changhong.system.web.facade.dto;

import com.changhong.common.utils.CHStringUtils;
import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by kerio on 2015/6/12.
 */
public class FeedBackDTO implements Serializable {
    private int id=-1;
    private String content;
    private String reply;
    private Date dateTime;
    private String username;
    private String usermac;
    private String status;

    public FeedBackDTO() {
    }

    public FeedBackDTO(int id, String content,String reply, String username, String usermac, String status,Date dateTime) {
        this.id =id;
        this.content = content;
        this.reply=reply;
        this.dateTime = dateTime;
        this.username = username;
        this.usermac = usermac;
        this.status = status;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsermac() {
        return usermac;
    }

    public void setUsermac(String usermac) {
        this.usermac = usermac;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
