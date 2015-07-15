package com.changhong.system.web.controller;

import com.changhong.common.web.session.SessionKey;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.paging.AppOverviewPaging;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午11:42
 */
public class ClientLocationFormController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletRequest.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "ClientLocation");
        Map<String, Object> model = new HashMap<String, Object>();
        String lon = ServletRequestUtils.getStringParameter(httpServletRequest, "lon", "");
        String lat = ServletRequestUtils.getStringParameter(httpServletRequest, "lat", "");
        model.put("lon", lon);
        model.put("lat", lat);
        return new ModelAndView("backend/system/clientlocationmap", model);
    }
}
