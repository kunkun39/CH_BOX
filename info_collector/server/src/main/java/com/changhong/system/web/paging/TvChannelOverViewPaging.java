package com.changhong.system.web.paging;

import com.changhong.system.service.FeedBackService;
import com.changhong.system.web.facade.dto.TvChannelDTO;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
import java.util.List;

/**
 * Created by maren on 2015/6/15.
 */
public class TvChannelOverViewPaging extends AbstractPaging<TvChannelDTO> {
    private FeedBackService feedBackService;
    private String channelName;
    public TvChannelOverViewPaging(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    public List<TvChannelDTO> getItems() {
        return feedBackService.obtainAllTvChannel(getStartPosition(), getPageSize(),channelName);
    }

    public long getTotalItemSize() {
        if (totalItemSize >= 0) {
            return totalItemSize;
        }
        totalItemSize =feedBackService.loadAllTvChannelInfoSize();
        return totalItemSize;
    }

    public String getParameterValues() {
        return "&filername=" + getChannelName();
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
