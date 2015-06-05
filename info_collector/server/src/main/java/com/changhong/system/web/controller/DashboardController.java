package com.changhong.system.web.controller;

import com.changhong.common.web.session.SessionKey;
import org.joda.time.LocalDate;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.changhong.common.utils.*;

/**
 * User: Jack Wang
 * Date: 14-4-9
 * Time: 上午9:07
 */
public class DashboardController extends AbstractController {

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "DASHBOARD");

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("today", JodaUtils.currentTime());

        //get all the notifications for select year and month
        int year = ServletRequestUtils.getIntParameter(request, "year", new LocalDate().getYear());
        int month = ServletRequestUtils.getIntParameter(request, "month", new LocalDate().getMonthOfYear());
        model.put("selectYear", year);
        model.put("selectMonth", month);

        //caculate selected month has how many days
        int totalDays = JodaUtils.getCurrentMonthTotalDays(year, month);
        model.put("totalDays", totalDays);
        // used for check today is which day
        int currentDay = new LocalDate().getDayOfMonth();
        model.put("currentDay", currentDay);

        return new ModelAndView("backend/dashboard", model);
    }
}
