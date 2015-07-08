package com.changhong.system.web.controller;

import com.changhong.system.service.AppService;
import com.changhong.system.service.UserService;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.UserDTO;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午11:42
 */
public class AppFormController extends SimpleFormController {

    private AppService appService;
    private String applicationWebAddress;
    public AppFormController() {
        setCommandClass(AppDTO.class);
        setCommandName("app");
        setFormView("/backend/system/appform");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        int appId = ServletRequestUtils.getIntParameter(request, "appId", -1);
        String current = ServletRequestUtils.getStringParameter(request, "current", "");
        request.setAttribute("current", current);
        request.setAttribute("applicationWebAddress", applicationWebAddress);

        if (appId > 0) {
            return appService.obtainAppById(appId);
        }
        return new AppDTO();
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        int appId = ServletRequestUtils.getIntParameter(request, "appId", -1);
        String appname = ServletRequestUtils.getStringParameter(request, "appname", "");
        int appIconId=ServletRequestUtils.getIntParameter(request, "appIconId", -1);
        if (!StringUtils.hasText(appname)) {
            errors.rejectValue("appname", "user.name.empty");
        }

        String appdes = ServletRequestUtils.getStringParameter(request, "appdes", "");
        if (!StringUtils.hasText(appdes)) {
            errors.rejectValue("appdes", "user.username.empty");
        }


        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("appIconFile");
        if (appIconId <= 0) {
            if (file == null || file.getSize() == 0) {
                errors.rejectValue("appIconFile", "user.username.empty");
            }
        }

//        }
//        String appkey = ServletRequestUtils.getStringParameter(request, "appkey", "");
//        if (!StringUtils.hasText(appkey)) {
//            errors.rejectValue("appdes", "user.username.empty");
//        }

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        String current = ServletRequestUtils.getStringParameter(request, "current", "");

        AppDTO appDTO = (AppDTO) command;
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("appIconFile");
        if (file != null && file.getSize() > 0) {
            appDTO.setAppIconFile(file);
        }

        appService.changeAppDetails(appDTO);

        return new ModelAndView(new RedirectView("tvchannelinfooverview.html?current="+current));
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    public void setApplicationWebAddress(String applicationWebAddress) {
        this.applicationWebAddress = applicationWebAddress;
    }
}
