package com.changhong.system.repository;

import com.changhong.common.repository.EntityObjectDao;
import com.changhong.system.domain.FeedBack;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

/**
 * Created by kerio on 2015/6/9.
 */
public interface FeedBackDao extends EntityObjectDao {
    List<FeedBack> loadUserFeedBacks(int startPosition, int pageSize);
    int loadUserFeedBackSize();
    String obtainUsernameByMac(String mac);

    JSONArray obtainFeedBackInfoByMonth(String status,String year,String month) throws JSONException;
    JSONArray obtainCollectorInfoByMonth(String status,String year,String month) throws JSONException;
}
