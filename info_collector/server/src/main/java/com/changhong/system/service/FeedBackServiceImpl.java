package com.changhong.system.service;

import com.changhong.system.domain.*;
import com.changhong.system.domain.FeedBack;
import com.changhong.system.domain.TvChannel;
import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.domain.User;
import com.changhong.system.repository.FeedBackDao;
import com.changhong.system.web.facade.assember.ClientLocationInfoWebAssember;
import com.changhong.system.web.facade.assember.TvChannelWebAssember;
import com.changhong.system.web.facade.dto.*;
import com.changhong.system.web.facade.dto.TvChannelDTO;
import com.changhong.system.web.facade.assember.UserWebAssember;
import com.changhong.system.web.facade.assember.FeedBackWebAssember;
import com.changhong.system.web.facade.dto.UserDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kerio on 2015/6/9.
 */
@Service("feedBackService")
public class FeedBackServiceImpl implements FeedBackService{

    @Autowired
    private FeedBackDao feedBackDao;



    @Override
    public List<FeedBackDTO> obtainUserFeedBacks( int startPosition, int pageSize) {
        List<FeedBack> feedBacks=feedBackDao.loadUserFeedBacks(startPosition, pageSize);
        Map<String,String> userNames=new HashMap<String,String>();
        if (feedBacks != null) {
            for (FeedBack feedBack : feedBacks) {
                 String usemac=feedBack.getUsermac();
                 String usename=feedBackDao.obtainUsernameByMac(usemac);
                 userNames.put(usemac,usename);
            }

        }
        return FeedBackWebAssember.toFeedBackDTOList(feedBacks,userNames);
    }

    @Override
    public int obtainUserFeedBackSize() {
        return feedBackDao.loadUserFeedBackSize();
    }

    @Override
    public String obtainUsernameByMac(String mac) {
        return null;
    }

    @Override
    public boolean loadFeedBackExist(int feedbackId, String usermac) {
        return feedBackDao.loadFeedBackExist(feedbackId,usermac);
    }

    @Override
    public void changeStatusForFeedBack(int feedbackId) {
        FeedBack feedBack = (FeedBack) feedBackDao.findById(feedbackId, FeedBack.class);
        if (feedBack.getStatus().equals("0")) {
            feedBack.setStatus("1");
        }
    }

    @Override
    public void persistReply(FeedBackDTO feedBackDTO) {
        FeedBack feedBack = FeedBackWebAssember.toFeedBackDomain(feedBackDTO);
        feedBackDao.saveOrUpdate(feedBack);
    }

    @Override
    public FeedBackDTO obtainFeedBackById(int feedbackId) {
        FeedBack feedBack = (FeedBack) feedBackDao.findById(feedbackId, FeedBack.class);
        return FeedBackWebAssember.toFeedBackDTO(feedBack,null);
    }

    @Override
    public JSONArray obtainFeedBackInfoByMonth(String status, String year, String month) throws JSONException {
        return feedBackDao.obtainFeedBackInfoByMonth(status,year,month);
    }

    @Override
    public List<TvChannelInfo> obtainAllTvChannelInfo() {
        return feedBackDao.obtainAllTvChannelInfo();
    }

    @Override
    public List<TvChannelDTO> obtainAllTvChannel(int startPosition,int pageSize,String channelName) {
        List<TvChannel> tvChannels= feedBackDao.obtainAllTvChannel(startPosition, pageSize, channelName);
        return TvChannelWebAssember.toTvChannelDTOList(tvChannels);
    }

    @Override
    public int loadAllTvChannelInfoSize() {
        return feedBackDao.loadAllTvChannelInfoSize();
    }
    @Override
    public JSONArray obtainCollectorInfoAmountByProgram(String tvChannelName, String year, String month) throws JSONException {
        return feedBackDao.obtainCollectorInfoAmountByProgram(tvChannelName, year, month);
    }

    @Override
    public TvChannelDTO obtainChannelById(int channelId) {
        TvChannel tvChannel = (TvChannel) feedBackDao.findById(channelId, TvChannel.class);
        return TvChannelWebAssember.toTvChannelDTO(tvChannel);
    }

    public void changeChannelDetails(TvChannelDTO tvChannelDTO) {
        TvChannel tvChannel = TvChannelWebAssember.toTvChannelDomain(tvChannelDTO);
        feedBackDao.persist(tvChannel);
    }

    @Override
    public int loadAllClientLocationInfoSize() {
        return feedBackDao.loadAllTvChannelInfoSize();
    }

    @Override
    public List<ClientLocationInfoDTO> obtainClientLocationInfos(int startPosition, int pageSize, String clientName) {
        List<ClientLocationInfo> clientLocationInfos=feedBackDao.obtainAllLocationInfo(startPosition, pageSize, clientName);
        HashMap<String,String> userNames=new HashMap<String,String>();
        if (clientLocationInfos != null) {
            for (ClientLocationInfo clientLocationInfo : clientLocationInfos) {
                String usemac=clientLocationInfo.getUserMac();
                String usename=feedBackDao.obtainUsernameByMac(usemac);
                userNames.put(usemac,usename);
            }
            return ClientLocationInfoWebAssember.toClientLocationInfoDTOs(clientLocationInfos, userNames);

        }else {
            return null;
        }
    }

}
