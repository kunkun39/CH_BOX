package com.changhong.system.service;

import com.changhong.system.web.facade.dto.FeedBackDTO;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;


/**
 * Created by kerio on 2015/6/9.
 */
public interface FeedBackService {
    List<FeedBackDTO> obtainUserFeedBacks(int startPosition, int pageSize);
    int obtainUserFeedBackSize();
    String obtainUsernameByMac(String mac);

    JSONArray obtainFeedBackInfoByMonth(String status,String year,String month) throws JSONException;
    JSONArray obtainCollectorInfoByMonth(String status,String year,String month) throws JSONException;
}
