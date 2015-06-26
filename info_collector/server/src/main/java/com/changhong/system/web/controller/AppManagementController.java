package com.changhong.system.web.controller;

import com.changhong.common.utils.SecurityUtils;
import com.changhong.common.web.session.SessionKey;
import com.changhong.system.service.AppService;
import com.changhong.system.service.UserService;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.UserDTO;
import com.changhong.system.web.paging.AppOverviewPaging;
import com.changhong.system.web.paging.UserOverviewPaging;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午10:30
 */
public class AppManagementController extends AbstractController {

    private AppService appService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "USER");

        Map<String, Object> model = new HashMap<String, Object>();
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        request.setAttribute("current", current);

        AppOverviewPaging paging = new AppOverviewPaging(appService);
        constructPaging(paging, current);
        List<AppDTO> apps = paging.getItems();
        model.put("apps", apps);
        model.put("paging", paging);

        return new ModelAndView("backend/system/appoverview", model);
    }

    private void constructPaging(AppOverviewPaging paging, int current) {
        paging.setCurrentPageNumber(current);
    }

    public void setAppService(AppService appService) {
        this.appService = appService;
    }
}
