package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * Created by kerio on 2015/6/26.
 * 应用
 */
public class App extends EntityBase {
    private String appname;
    private String appkey;
    private String appdes;
    private AppIcon appIcon;

    public App() {
    }



    public App(String appname, String appkey, String appdes ) {
        this.appname = appname;
        this.appkey = appkey;
        this.appdes = appdes;

    }
    //提供一个更改appIcon的方法
    public AppIcon changeAppIconFile(AppIcon newAppIconFile) {
        AppIcon oldAppIconFile = null;

        if (appIcon != null) {
            if (newAppIconFile != null) {
                oldAppIconFile = new AppIcon();
                oldAppIconFile.setUploadTime(appIcon.getUploadTime());
                oldAppIconFile.setUploadFileName(appIcon.getUploadFileName());
                oldAppIconFile.setActualFileName(appIcon.getActualFileName());

                appIcon.setUploadFileName(newAppIconFile.getUploadFileName());
                appIcon.setActualFileName(newAppIconFile.getActualFileName());
                appIcon.setUploadTime(newAppIconFile.getUploadTime());
                appIcon.setFile(newAppIconFile.getFile());
            }
        } else {
            this.appIcon = newAppIconFile;
        }

        return oldAppIconFile;
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

    public String getAppdes() {
        return appdes;
    }

    public void setAppdes(String appdes) {
        this.appdes = appdes;
    }

    public AppIcon getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(AppIcon appIcon) {
        this.appIcon = appIcon;
    }
}
