package com.changhong.system.repository;

import com.changhong.common.domain.EntityBase;
import com.changhong.common.repository.EntityObjectDao;
import com.changhong.system.domain.TvChannel;
import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
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
    boolean loadFeedBackExist(int feedbackId, String usermac);

    JSONArray obtainFeedBackInfoByMonth(String status,String year,String month) throws JSONException;
    JSONArray obtainCollectorInfoAmountByProgram(String tvChannelName,String year,String month) throws JSONException;

    List<TvChannelInfo> obtainAllTvChannelInfo();
    List<TvChannel> obtainAllTvChannel(int startPosition,int pageSize,String channelName);
    int loadAllTvChannelInfoSize();
}
