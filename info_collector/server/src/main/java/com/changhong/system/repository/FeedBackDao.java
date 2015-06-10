package com.changhong.system.repository;

import com.changhong.common.repository.EntityObjectDao;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by kerio on 2015/6/9.
 */
public interface FeedBackDao extends EntityObjectDao {
    JSONArray obtainFeedBackInfoByMonth(String status,int year,int month) throws JSONException;
}
