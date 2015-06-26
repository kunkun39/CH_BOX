package com.changhong.system.web.controller;

import com.changhong.system.service.FeedBackService;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by kerio on 2015/6/26.
 */
public class UserFeedBackChangeStatusController extends AbstractController {
    private FeedBackService feedBackService;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int feedbackId = ServletRequestUtils.getIntParameter(request, "feedbackId", -1);
        String current = ServletRequestUtils.getStringParameter(request, "current", "");

        feedBackService.changeStatusForFeedBack(feedbackId);

        return new ModelAndView(new RedirectView("clientuserfeedbackoverview.html?current="+current));
    }

    public void setFeedBackService(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }
}
