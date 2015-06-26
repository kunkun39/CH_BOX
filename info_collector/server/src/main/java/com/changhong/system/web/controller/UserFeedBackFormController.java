package com.changhong.system.web.controller;

import com.changhong.system.domain.FeedBack;
import com.changhong.system.repository.FeedBackDao;
import com.changhong.system.service.FeedBackService;
import com.changhong.system.service.UserService;
import com.changhong.system.web.facade.dto.FeedBackDTO;
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
public class UserFeedBackFormController extends SimpleFormController {

    private FeedBackService feedBackService;

    public UserFeedBackFormController() {
        setCommandClass(FeedBackDTO.class);
        setCommandName("feedback");
        setFormView("/backend/system/clientuserfeedbackform");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        int feedbackId = ServletRequestUtils.getIntParameter(request, "feedbackId", -1);
        String current = ServletRequestUtils.getStringParameter(request, "current", "");
        request.setAttribute("current", current);

        if (feedbackId > 0) {
            return feedBackService.obtainFeedBackById(feedbackId);
        }else{
            return new FeedBackDTO();
        }
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        int feedbackId = ServletRequestUtils.getIntParameter(request, "feedbackId", -1);
//        String name = ServletRequestUtils.getStringParameter(request, "username", "");
//        if (!StringUtils.hasText(name)) {
//            errors.rejectValue("name", "user.name.empty");
//        }

        String usermac = ServletRequestUtils.getStringParameter(request, "usermac", "");
        if (!StringUtils.hasText(usermac)) {
            errors.rejectValue("usermac", "user.username.empty");
        }
//        else {
//            boolean exist = feedBackService.loadFeedBackExist(feedbackId, usermac);
//            if (exist) {
//                errors.rejectValue("username", "user.username.exist");
//            }
//        }

        String content = ServletRequestUtils.getStringParameter(request, "content", "");
        if (!StringUtils.hasText(content)) {
            errors.rejectValue("content", "user.name.empty");
        }

        String reply = ServletRequestUtils.getStringParameter(request, "reply", "");
        if (!StringUtils.hasText(reply)) {
            errors.rejectValue("reply", "user.name.empty");
        }

    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        String current = ServletRequestUtils.getStringParameter(request, "current", "");

        FeedBackDTO feedBackDTO = (FeedBackDTO) command;
        feedBackService.persistReply(feedBackDTO);

        return new ModelAndView(new RedirectView("clientuserfeedbackoverview.html?current="+current));
    }

    public void setFeedBackService(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }
}
