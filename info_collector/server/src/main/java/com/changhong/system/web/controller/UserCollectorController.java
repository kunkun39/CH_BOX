package com.changhong.system.web.controller;

import com.changhong.common.utils.CHDateUtils;
import com.changhong.common.utils.SecurityUtils;
import com.changhong.common.web.session.SessionKey;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/9.
 */
public class UserCollectorController extends AbstractController{

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "COLLECTOR");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("reportYear", CHDateUtils.getCurrentYear());
        model.put("reportMonth", CHDateUtils.getCurrentMonth());
        return new ModelAndView("/backend/system/clientusercollector");
    }
}
