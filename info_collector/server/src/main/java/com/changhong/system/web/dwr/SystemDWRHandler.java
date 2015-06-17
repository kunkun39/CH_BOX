package com.changhong.system.web.dwr;

import com.changhong.system.service.FeedBackService;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("systemDWRHandler")
public class SystemDWRHandler {

    @Autowired
    private FeedBackService feedBackService;

    public String obtainFeedBackInfoAmountByMonth(String status,String year, String month) throws JSONException {
        return feedBackService.obtainFeedBackInfoByMonth(status, year, month).toString();
    }

    public String obtainCollectorInfoAmountByProgram(String tvChannelName,String year, String month) throws JSONException {
        return feedBackService.obtainCollectorInfoAmountByProgram(tvChannelName, year, month).toString();
    }
}
