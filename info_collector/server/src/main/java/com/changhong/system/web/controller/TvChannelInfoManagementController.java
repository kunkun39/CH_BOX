package com.changhong.system.web.controller;

import com.changhong.common.web.session.SessionKey;
import com.changhong.system.service.FeedBackServiceImpl;
import com.changhong.system.web.facade.dto.TvChannelDTO;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
import com.changhong.system.web.paging.TvChannelOverViewPaging;
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
 * Created by maren on 2015/6/15.
 */
public class TvChannelInfoManagementController extends AbstractController{
    private FeedBackServiceImpl feedBackService;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "USER");

        Map<String, Object> model = new HashMap<String, Object>();
        int current = ServletRequestUtils.getIntParameter(request, "current", 1);
        String filername = StringUtils.trimWhitespace(ServletRequestUtils.getStringParameter(request, "filername", ""));
        request.setAttribute("current", current);
        request.setAttribute("filername", filername);
        TvChannelOverViewPaging paging=new TvChannelOverViewPaging(feedBackService);
        constructPaging(paging, current, filername);
        List<TvChannelDTO> tvChannelDTOs=paging.getItems();
        model.put("tvChannels",tvChannelDTOs);
        model.put("paging", paging);
        return new ModelAndView("/backend/system/tvchannelinfooverview",model);
    }
    private void constructPaging(TvChannelOverViewPaging paging, int current, String name) {
        paging.setCurrentPageNumber(current);
        paging.setChannelName(name);
    }

    public void  setFeedBackService(FeedBackServiceImpl feedBackService){
        this.feedBackService=feedBackService;
    }

}
