package com.changhong.client.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.changhong.client.service.ChannelService;
import com.changhong.common.utils.CHDateUtils;
import com.changhong.system.domain.ClientUser;
import com.ibatis.sqlmap.client.SqlMapExecutor;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.*;

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
    public void saveBatchClientInfo(String userInfo) {
        JSONObject userObject=JSON.parseObject(userInfo);
        ClientUser clientUser=new ClientUser();

        clientUser.setName(userObject.getString("userName"));
        clientUser.setContactway(userObject.getString("userTel"));
        clientUser.setMac(userObject.getString("userMac"));
        final List<ClientUser> userInfoList=new ArrayList<ClientUser>(100);
        for(int i=0;i<200;i++){
            userInfoList.add(clientUser);
        }

        this.getSqlMapClientTemplate().execute(new SqlMapClientCallback() {
            public Object doInSqlMapClient(SqlMapExecutor executor)
                    throws SQLException {
                executor.startBatch();
                // 每次提交最大条数
                final int batchSize = 200;
                int count = 0;
                for (ClientUser record : userInfoList) {
                    executor.insert("Movie.insertRecord", record);
                    // 每200条数据提交一次
                    if (++count % batchSize == 0) {
                        executor.executeBatch();
                    }
                }
                // 提交剩余的数据
                executor.executeBatch();
                return null;
            }
        });


    }



    @Override
    public void saveFeedBackInfo(String feedInfo) {
        Map<String, String> values = new HashMap<String, String>();
        JSONObject feedInfoObject=JSON.parseObject(feedInfo);
        values.put("feedInfoContent", feedInfoObject.getString("feedInfoMessage"));
        values.put("userMac", feedInfoObject.getString("userMac"));
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
        Iterator it = ChannelService.channelMap.entrySet().iterator();
        HashMap<String,String> hashMap=new HashMap<String, String>();
        while(it.hasNext()){
            java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
            hashMap.put("channelName",entry.getKey().toString());
            hashMap.put("channelSequence",entry.getValue().toString());
            initSqlMapClient();
            getSqlMapClientTemplate().insert("Movie.insertTvChannel", hashMap);


        }

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
        values.put("fd_hour", String.valueOf(CHDateUtils.getCurrentHours()));
        initSqlMapClient();
        getSqlMapClientTemplate().insert("Movie.insertTvChannelInfo", values);
    }


}
