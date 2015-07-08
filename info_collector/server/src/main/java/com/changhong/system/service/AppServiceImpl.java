package com.changhong.system.service;

import com.changhong.system.domain.App;
import com.changhong.system.domain.AppIcon;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.domain.User;
import com.changhong.system.repository.AppDao;
import com.changhong.system.web.facade.assember.AppWebAssember;
import com.changhong.system.web.facade.assember.FeedBackWebAssember;
import com.changhong.system.web.facade.assember.UserWebAssember;
import com.changhong.system.web.facade.dto.AppDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by kerio on 2015/6/26.
 */
@Service("appService")
public class AppServiceImpl implements AppService {

    @Autowired
    private AppDao appDao;

    @Autowired
    private FileManageService fileManageService;

    @Override
    public List<AppDTO> obtainApps(int startPosition, int pageSize) {
        List<App> apps=appDao.obtainApps(startPosition,pageSize);
        return AppWebAssember.toAppDTOList(apps);
    }

    @Override
    public int obtainAppSize() {
        return appDao.obtainAppSize();
    }

    @Override
    public AppDTO obtainAppById(int appId) {
        App app = (App) appDao.findById(appId, App.class);
        return AppWebAssember.toAppDTO(app, false);
    }

    @Override
    public void changeAppDetails(AppDTO appDTO) {
        App app = AppWebAssember.toAppDomain(appDTO);
        //获得上传的文件
        MultipartFile file = appDTO.getAppIconFile();
        AppIcon appIcon = null;
        if(file != null && file.getSize() > 0) {
            appIcon = AppWebAssember.toAppIconDomain(file);
        }

        if(file!=null&&file.getSize()>0){
            fileManageService.uploadAppIconFile(appIcon);
        }

        //获得老的上传的文件，如果存在就删除
        AppIcon oldAppIconFile = app.changeAppIconFile(appIcon);
        if (oldAppIconFile != null) {
            fileManageService.deleteAppIconFile(oldAppIconFile);
        }
        //如果新的文件存在，则从新上传
        if(file != null && file.getSize() > 0) {
            fileManageService.uploadAppIconFile(app.getAppIcon());
        }

        //设置AppIcon
        app.setAppIcon(appIcon);
        appDao.saveOrUpdate(app);

    }
}
