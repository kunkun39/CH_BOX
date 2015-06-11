package com.changhong.system.service;

import org.json.JSONArray;
import org.json.JSONException;


/**
 * Created by kerio on 2015/6/9.
 */
public interface FeedBackService {
    JSONArray obtainFeedBackInfoByMonth(String status,int year,int month) throws JSONException;
}
