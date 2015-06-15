package com.changhong.system.web.paging;

import com.changhong.system.service.FeedBackService;
import com.changhong.system.web.facade.dto.FeedBackDTO;

import java.util.List;
/**
 * Created by kerio on 2015/6/12.
 */
public class UserFeedBackOverviewPaging extends AbstractPaging<FeedBackDTO> {

    private FeedBackService feedBackService;

    public UserFeedBackOverviewPaging(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }

    public List<FeedBackDTO> getItems() {
        return feedBackService.obtainUserFeedBacks(getStartPosition(), getPageSize());
    }

    public long getTotalItemSize() {
        if (totalItemSize >= 0) {
            return totalItemSize;
        }
        totalItemSize = feedBackService.obtainUserFeedBackSize();
        return totalItemSize;
    }

    public String getParameterValues() {
        return "";
    }

}

