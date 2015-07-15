package com.changhong.system.web.controller;

import com.changhong.system.domain.TvChannel;
import com.changhong.system.service.AppService;
import com.changhong.system.service.FeedBackServiceImpl;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.TvChannelDTO;
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
public class ChannelFormController extends SimpleFormController {

    private FeedBackServiceImpl feedBackService;

    public ChannelFormController() {
        setCommandClass(TvChannelDTO.class);
        setCommandName("tvChannel");
        setFormView("/backend/system/tvchannelform");
    }

    @Override
    protected Object formBackingObject(HttpServletRequest request) throws Exception {
        int tvChannelId = ServletRequestUtils.getIntParameter(request, "tvChannelId", -1);
        String current = ServletRequestUtils.getStringParameter(request, "current", "");
        request.setAttribute("current", current);

        if (tvChannelId > 0) {
            return feedBackService.obtainChannelById(tvChannelId);
        }
        return new TvChannelDTO();
    }

    @Override
    protected void onBindAndValidate(HttpServletRequest request, Object command, BindException errors) throws Exception {
        int channelId = ServletRequestUtils.getIntParameter(request, "channelId", -1);
        String channelName = ServletRequestUtils.getStringParameter(request, "tvChannelName", "");
        if (!StringUtils.hasText(channelName)) {
            errors.rejectValue("channelName", "user.name.empty");
        }

        String appdes = ServletRequestUtils.getStringParameter(request, "tvChannelName", "");
        if (!StringUtils.hasText(appdes)) {
            errors.rejectValue("channelName", "user.username.empty");
        }
    }

    @Override
    protected ModelAndView onSubmit(HttpServletRequest request, HttpServletResponse response, Object command, BindException errors) throws Exception {
        String current = ServletRequestUtils.getStringParameter(request, "current", "");

        TvChannelDTO tvChannelDTO = (TvChannelDTO) command;
        feedBackService.changeChannelDetails(tvChannelDTO);
        return new ModelAndView(new RedirectView("tvchannelinfooverview.html?current="+current));
    }

    public void setFeedBackService(FeedBackServiceImpl feedBackService) {
        this.feedBackService = feedBackService;
    }
}
