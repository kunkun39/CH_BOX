package com.changhong.system.web.controller;

import com.changhong.common.web.session.SessionKey;
import com.changhong.system.service.AppService;
import com.changhong.system.service.FeedBackService;
import com.changhong.system.service.FeedBackServiceImpl;
import com.changhong.system.web.facade.dto.AppDTO;
import com.changhong.system.web.facade.dto.ClientLocationInfoDTO;
import com.changhong.system.web.facade.dto.TvChannelDTO;
import com.changhong.system.web.paging.ClientLocationOverViewPaging;
import com.changhong.system.web.paging.TvChannelOverViewPaging;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

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
public class ClientLocationManagementController extends AbstractController {
    FeedBackServiceImpl feedBackService;
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        httpServletRequest.getSession().setAttribute(SessionKey.BROSWER_LOCATION, "ClientLocation");

        Map<String, Object> model = new HashMap<String, Object>();
        int current = ServletRequestUtils.getIntParameter(httpServletRequest, "current", 1);
        String filername = StringUtils.trimWhitespace(ServletRequestUtils.getStringParameter(httpServletRequest, "filername", ""));
        httpServletRequest.setAttribute("current", current);
        httpServletRequest.setAttribute("filername", filername);
        ClientLocationOverViewPaging paging=new ClientLocationOverViewPaging(feedBackService);
        constructPaging(paging, current, filername);
        List<ClientLocationInfoDTO> clientLocationInfoDTOs=paging.getItems();
        model.put("clientLocationDTOs",clientLocationInfoDTOs);
        model.put("paging", paging);

        return  new ModelAndView("/backend/system/clientlocationoverview",model);
    }
    public void constructPaging(ClientLocationOverViewPaging paging,int current,String filterName){
        paging.setClientName(filterName);
        paging.setCurrentPageNumber(current);
    }
    public void  setFeedBackService(FeedBackServiceImpl feedBackService){
        this.feedBackService=feedBackService;
    }

}
