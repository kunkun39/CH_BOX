package com.changhong.system.service;

import com.changhong.system.domain.AppIcon;

/**
 * Created by kerio on 2015/7/7.
 */
public interface FileManageService {
    void uploadAppIconFile(AppIcon file);
    void deleteAppIconFile(AppIcon file);
}
