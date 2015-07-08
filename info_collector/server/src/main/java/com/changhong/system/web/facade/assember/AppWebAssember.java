package com.changhong.system.web.facade.assember;

import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.App;
import com.changhong.system.domain.AppIcon;
import com.changhong.system.web.facade.dto.AppDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午10:58
 */
public class AppWebAssember {

    public static AppIcon toAppIconDomain(MultipartFile file) {
        return new AppIcon(file);
    }

    public static App toAppDomain(AppDTO appDTO) {
        App app = null;
        if(appDTO == null) return null;

        if (appDTO.getId() > 0) {
            app=new App();
            app.setId(appDTO.getId());
            app.setAppname(appDTO.getAppname());
            app.setAppkey(appDTO.getAppkey());
            app.setAppdes(appDTO.getappdes());

        } else {
            app = new App(appDTO.getAppname(),appDTO.getAppkey(),appDTO.getappdes());
        }
        return app;
    }

    public static AppDTO toAppDTO(App app, boolean addSubInfo) {
        int id=app.getId();
        String appname=app.getAppname();
        String appkey=app.getAppkey();
        String appdes=app.getAppdes();
        String dateTime= CHDateUtils.getDateFormat(app.getTimestamp());
        AppIcon file=app.getAppIcon();
        int appIconId = file != null ? file.getId() : -1;
        String uploadFileName=file!=null?file.getUploadFileName():"";
        String actualFileName = file != null ? file.getActualFileName() : "";

        AppDTO dto=new AppDTO(id,appname,appkey,appdes,dateTime,appIconId,uploadFileName,actualFileName);
        return dto;
    }

    public static List<AppDTO> toAppDTOList(List<App> apps) {
        List<AppDTO> dtos = new ArrayList<AppDTO>();
        if (apps != null) {
            for (App app : apps) {
                dtos.add(toAppDTO(app, false));
            }
        }
        return dtos;
    }
}
