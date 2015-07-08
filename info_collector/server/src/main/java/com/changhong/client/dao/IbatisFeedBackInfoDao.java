package com.changhong.client.dao;


import java.util.List;

/**
 * Created by maren on 2015/6/9.
 */
public interface IbatisFeedBackInfoDao {
    /**
     * 反馈模块
     * @param
     */


    public void saveClientInfo(String userInfo);
    public void saveFeedBackInfo(String feedInfo);
    public void saveBatchClientInfo(String userInfo);


    /**
     * 统计模块
     * @param channel
     */
    public void saveTvChannel(String  channel);
    public void saveTvChannelInfo(String tvChannelInfo);
}
