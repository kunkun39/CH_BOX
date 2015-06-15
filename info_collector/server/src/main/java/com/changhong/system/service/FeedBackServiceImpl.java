package com.changhong.system.service;

import com.changhong.system.domain.FeedBack;
import com.changhong.system.repository.FeedBackDao;
import com.changhong.system.web.facade.assember.FeedBackWebAssember;
import com.changhong.system.web.facade.dto.FeedBackDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/9.
 */
@Service("feedBackService")
public class FeedBackServiceImpl implements FeedBackService{

    @Autowired
    private FeedBackDao feedBackDao;

    @Override
    public List<FeedBackDTO> obtainUserFeedBacks( int startPosition, int pageSize) {
        List<FeedBack> feedBacks=feedBackDao.loadUserFeedBacks(startPosition, pageSize);
        Map<String,String> userNames=new HashMap<String,String>();
        if (feedBacks != null) {
            for (FeedBack feedBack : feedBacks) {
                 String usemac=feedBack.getUsermac();
                 String usename=feedBackDao.obtainUsernameByMac(usemac);
                 userNames.put(usemac,usename);
            }

        }
        return FeedBackWebAssember.toFeedBackDTOList(feedBacks,userNames);
    }

    @Override
    public int obtainUserFeedBackSize() {
        return feedBackDao.loadUserFeedBackSize();
    }

    @Override
    public String obtainUsernameByMac(String mac) {
        return null;
    }

    @Override
    public JSONArray obtainFeedBackInfoByMonth(String status, String year, String month) throws JSONException {
        return feedBackDao.obtainFeedBackInfoByMonth(status,year,month);
    }

    @Override
    public JSONArray obtainCollectorInfoByMonth(String status, String year, String month) throws JSONException {
        return feedBackDao.obtainCollectorInfoByMonth(status, year, month);
    }

}
