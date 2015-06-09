package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by kerio on 2015/6/8.
 */
public class ClientUser extends EntityBase{
    private String name;
    private String contactway;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactway() {
        return contactway;
    }

    public void setContactway(String contactway) {
        this.contactway = contactway;
    }

    public ClientUser() {
    }

    public ClientUser(String name, String contactway) {
        this.name = name;
        this.contactway = contactway;
    }
}
