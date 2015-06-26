package com.changhong.system.service;

import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
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
    boolean loadFeedBackExist(int feedbackId, String usermac);
    void changeStatusForFeedBack(int feedbackId);

    void persistReply(FeedBackDTO feedBackDTO);
    FeedBackDTO obtainFeedBackById(int feedbackId);

    JSONArray obtainFeedBackInfoByMonth(String status,String year,String month) throws JSONException;
    JSONArray obtainCollectorInfoAmountByProgram(String tvChannelName,String year,String month) throws JSONException;
    List<TvChannelInfo> obtainTvChannelInfo(String channelName,String year,String month,String day,String hour);
    List<TvChannelInfo> obtainAllTvChannelInfo();
    List<TvChannelInfoDTO> obtainAllTvChannelInfo(int startPosition,int pageSize,String channelName);
    int loadAllTvChannelInfoSize();
}
