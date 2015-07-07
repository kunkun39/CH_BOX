package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;
import com.changhong.common.utils.CHStringUtils;
import com.changhong.common.utils.JodaUtils;
import org.joda.time.DateTime;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by kerio on 2015/7/7.
 */
public class AppIcon extends EntityBase {
    private final static int FAKE_FILENAME_LENGTH = 12;
    private MultipartFile file;
    private String uploadFileName;
    private String actualFileName;

    private DateTime uploadTime;

    public AppIcon() {
    }

    public AppIcon(MultipartFile file) {
        this.uploadFileName = file != null ? file.getOriginalFilename() : "";
        this.uploadTime = JodaUtils.currentTime();
        this.file = file;

        int lastDot = uploadFileName.lastIndexOf(".");
        String fileNameSuffix = uploadFileName.substring(lastDot);
        this.actualFileName = CHStringUtils.getRandomString(FAKE_FILENAME_LENGTH) + fileNameSuffix;
    }


    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public String getUploadFileName() {
        return uploadFileName;
    }

    public void setUploadFileName(String uploadFileName) {
        this.uploadFileName = uploadFileName;
    }

    public String getActualFileName() {
        return actualFileName;
    }

    public void setActualFileName(String actualFileName) {
        this.actualFileName = actualFileName;
    }

    public DateTime getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(DateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
}
