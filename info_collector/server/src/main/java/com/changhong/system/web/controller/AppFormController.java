package com.changhong.system.web.controller;

import com.changhong.system.service.AppService;
import com.changhong.system.service.UserService;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.UserDTO;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午11:42
 */
public class AppFormController extends SimpleFormController {

    private AppService appService;

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

        if (appId > 0) {
            return appService.obtainAppById(appId);
        }
        return new AppDTO();
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        int appId = ServletRequestUtils.getIntParameter(request, "appId", -1);
        String appname = ServletRequestUtils.getStringParameter(request, "appname", "");
        if (!StringUtils.hasText(appname)) {
            errors.rejectValue("appname", "user.name.empty");
        }

        String appdes = ServletRequestUtils.getStringParameter(request, "appdes", "");
        if (!StringUtils.hasText(appdes)) {
            errors.rejectValue("appdes", "user.username.empty");
        }
//        else {
//            boolean exist = userService.obtainUserExist(userId, username);
//            if (exist) {
//                errors.rejectValue("username", "user.username.exist");
//            }
//        }
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        String current = ServletRequestUtils.getStringParameter(request, "current", "");

        AppDTO appDTO = (AppDTO) command;
        appService.changeAppDetails(appDTO);

        return new ModelAndView(new RedirectView("appoverview.html?current="+current));
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }
}