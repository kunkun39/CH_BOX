package com.changhong.system.web.facade.dto;

/**
 * Created by maren on 15-7-13.
 */
public class ClientLocationInfoDTO {
    private int id=-1;
    private String clientName;
    private String userMac;
    private String lon;
    private String lat;


    public ClientLocationInfoDTO() {
    }

    public ClientLocationInfoDTO(String clientName, int id, String lat, String lon, String userMac) {
        this.clientName = clientName;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.userMac = userMac;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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
