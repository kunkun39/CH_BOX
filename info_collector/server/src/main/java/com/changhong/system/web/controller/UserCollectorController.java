package com.changhong.system.web.controller;

import com.changhong.common.utils.CHDateUtils;
import com.changhong.common.utils.SecurityUtils;
import com.changhong.common.web.session.SessionKey;
import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.service.FeedBackService;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
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

    private FeedBackService feedBackService;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "COLLECTOR");

        Map<String, Object> model = new HashMap<String, Object>();
        List<TvChannelInfo> tvChannelInfos=feedBackService.obtainAllTvChannelInfo();
        model.put("tvChannelInfos",tvChannelInfos);
        model.put("reportYear", CHDateUtils.getCurrentYear());
        model.put("reportMonth", CHDateUtils.getCurrentMonth());
        return new ModelAndView("/backend/system/clientusercollector",model);
    }

    public void setFeedBackService(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }
}
