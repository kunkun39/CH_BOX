package com.changhong.system.web.controller;

import com.changhong.common.web.session.SessionKey;
import com.changhong.system.service.FeedBackService;
import com.changhong.system.web.facade.dto.FeedBackDTO;
import com.changhong.system.web.paging.UserFeedBackOverviewPaging;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/12.
 */
public class UserFeedBackManagementController extends AbstractController {

    private FeedBackService feedBackService;

    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "FEEDBACK");

        Map<String, Object> model = new HashMap<String, Object>();
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        request.setAttribute("current", current);

        UserFeedBackOverviewPaging paging = new UserFeedBackOverviewPaging(feedBackService);
        constructPaging(paging, current);
        List<FeedBackDTO> feedbacks = paging.getItems();
        model.put("feedbacks", feedbacks);
        model.put("paging", paging);

        return new ModelAndView("backend/system/clientuserfeedbackoverview", model);
    }

    private void constructPaging(UserFeedBackOverviewPaging paging, int current) {
        paging.setCurrentPageNumber(current);
    }

    public void setFeedBackService(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }
}
