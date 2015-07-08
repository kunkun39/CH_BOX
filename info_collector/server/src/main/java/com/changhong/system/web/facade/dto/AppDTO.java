package com.changhong.system.web.facade.dto;

import com.changhong.common.utils.CHDateUtils;
import com.changhong.common.utils.CHStringUtils;
import org.springframework.web.multipart.MultipartFile;

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
    private String dateTime;

    private int appIconId;
    private String uploadFileName;
    private String actualFileName;
    private MultipartFile appIconFile;

    public AppDTO() {
        this.appkey= getRandomApiKey(24);
        this.dateTime=CHDateUtils.getDateFormat(new Date());

    }

    public AppDTO(int id, String appname, String appkey, String appdes, String dateTime,int appIconId,String uploadFileName,String actualFileName) {
        this.id = id;
        this.appname = appname;
        this.appkey = appkey;
        this.appdes = appdes;
        this.dateTime = dateTime;
        this.appIconId=appIconId;
        this.uploadFileName=uploadFileName;
        this.actualFileName=actualFileName;
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

    public String getAppdes() {
        return appdes;
    }

    public void setAppdes(String appdes) {
        this.appdes = appdes;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getAppIconId() {
        return appIconId;
    }

    public void setAppIconId(int appIconId) {
        this.appIconId = appIconId;
    }

    public String getActualFileName() {
        return actualFileName;
    }

    public void setActualFileName(String actualFileName) {
        this.actualFileName = actualFileName;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public MultipartFile getAppIconFile() {
        return appIconFile;
    }

    public void setAppIconFile(MultipartFile appIconFile) {
        this.appIconFile = appIconFile;
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
