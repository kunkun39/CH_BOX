package com.changhong.system.service;

import com.changhong.system.domain.FeedBack;
import com.changhong.system.repository.FeedBackDao;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by kerio on 2015/6/9.
 */
@Service("feedBackService")
public class FeedBackServiceImpl implements FeedBackService{

    @Autowired
    private FeedBackDao feedBackDao;
    @Override
    public JSONArray obtainFeedBackInfoByMonth(String status, int year, int month) throws JSONException {
        return feedBackDao.obtainFeedBackInfoByMonth(status,year,month);
    }

}
