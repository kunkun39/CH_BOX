package com.changhong.system.web.facade.assember;

import com.changhong.system.domain.App;
import com.changhong.system.web.facade.dto.AppDTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午10:58
 */
public class AppWebAssember {

    public static App toAppDomain(AppDTO appDTO) {
        App app = null;
        if(appDTO == null) return null;

        if (appDTO.getId() > 0) {
            app=new App();
            app.setId(appDTO.getId());
            app.setAppname(appDTO.getAppname());
            app.setAppkey(appDTO.getAppkey());
            app.setAppdes(appDTO.getappdes());
            app.setTimestamp(appDTO.getDateTime());

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
        Date dateTime=app.getTimestamp();

        AppDTO dto=new AppDTO(id,appname,appkey,appdes,dateTime);
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
