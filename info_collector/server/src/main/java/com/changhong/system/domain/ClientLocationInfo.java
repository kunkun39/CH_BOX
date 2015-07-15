package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by maren on 15-7-13.
 */
public class ClientLocationInfo extends EntityBase {
    private String userMac;
    private String lat;
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getUserMac() {
        return userMac;
    }

    public void setUserMac(String userMac) {
        this.userMac = userMac;
    }
}
