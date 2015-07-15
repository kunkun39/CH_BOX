package com.changhong.system.web.paging;

import com.changhong.system.domain.ClientLocationInfo;
import com.changhong.system.service.FeedBackService;
import com.changhong.system.web.facade.dto.ClientLocationInfoDTO;
import com.changhong.system.web.facade.dto.TvChannelDTO;

import java.util.List;

/**
 * Created by maren on 2015/6/15.
 */
public class ClientLocationOverViewPaging extends AbstractPaging<ClientLocationInfoDTO> {
    private FeedBackService feedBackService;
    private String clientName;
    public ClientLocationOverViewPaging(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    public List<ClientLocationInfoDTO> getItems() {
        return feedBackService.obtainClientLocationInfos(getStartPosition(), getPageSize(), clientName);
    }

    public long getTotalItemSize() {
        if (totalItemSize >= 0) {
            return totalItemSize;
        }
        totalItemSize =feedBackService.loadAllTvChannelInfoSize();
        return totalItemSize;
    }

    public String getParameterValues() {
        return "&filername=" + getClientName();
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
