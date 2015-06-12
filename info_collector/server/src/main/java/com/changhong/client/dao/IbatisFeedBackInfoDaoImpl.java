package com.changhong.client.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.FeedChannelInfo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by maren on 2015/6/9.
 */
@Repository("ibatisFeedBackInfoDao")
public class IbatisFeedBackInfoDaoImpl extends IbatisEntityObjectDao implements IbatisFeedBackInfoDao  {
    /**
     * 反馈模块
     */

    @Override
    public void saveClientInfo(String userInfo) {
        Map<String, String> values = new HashMap<String, String>();
        JSONObject userObject=JSON.parseObject(userInfo);
        values.put("userName", userObject.getString("userName"));
        values.put("userTel", userObject.getString("userTel"));
        values.put("userMac", userObject.getString("userMac"));
        initSqlMapClient();
        getSqlMapClientTemplate().insert("Movie.insertClientUserInfo", values);

    }



    @Override
    public void saveFeedBackInfo(String feedInfo) {
        Map<String, String> values = new HashMap<String, String>();
        JSONObject feedInfoObject=JSON.parseObject(feedInfo);
        values.put("feedInfoContent", feedInfoObject.getString("feedInfoMessage"));
        values.put("userMac", feedInfoObject.getString("feedInfoMessage"));
        values.put("status","0");
        values.put("fd_year", String.valueOf(CHDateUtils.getCurrentYear()));
        values.put("fd_month", String.valueOf(CHDateUtils.getCurrentMonth()));
        values.put("fd_day", String.valueOf(CHDateUtils.getCurrentDate()));
        initSqlMapClient();
        getSqlMapClientTemplate().insert("Movie.insertFeedBackInfo", values);
    }

    /**
     * 统计模块
     * @param channel
     */
    @Override
    public void saveTvChannel(String channel) {

    }
    @Override
    public void saveTvChannelInfo(String tvChannelInfo) {
        JSONObject tvChannelObject= JSON.parseObject(tvChannelInfo);
        Map<String, String> values = new HashMap<String, String>();
        values.put("tvChannelName", tvChannelObject.getString("tvChannelName"));
        values.put("tvProgramName", tvChannelObject.getString("tvProgramName"));
        values.put("userMac", tvChannelObject.getString("userMac"));
        values.put("appKey", tvChannelObject.getString("appKey"));
        values.put("status","0");
        values.put("fd_year", String.valueOf(CHDateUtils.getCurrentYear()));
        values.put("fd_month", String.valueOf(CHDateUtils.getCurrentMonth()));
        values.put("fd_day", String.valueOf(CHDateUtils.getCurrentDate()));
        initSqlMapClient();
        getSqlMapClientTemplate().insert("Movie.insertTvChannelInfo", values);
    }


}
