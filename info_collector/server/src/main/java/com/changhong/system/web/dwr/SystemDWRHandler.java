package com.changhong.system.web.dwr;

import com.changhong.system.service.FeedBackService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("systemDWRHandler")
public class SystemDWRHandler {

    @Autowired
    private FeedBackService feedBackService;

    public String obtainFeedBackInfoAmountByMonth(String status,int year, int month) throws JSONException {
        return feedBackService.obtainFeedBackInfoByMonth(status, year, month).toString();
    }

    public void setFeedBackService(FeedBackService feedBackService) {
        this.feedBackService = feedBackService;
    }
}
