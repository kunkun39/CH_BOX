package com.changhong.system.web.facade.assember;

import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.domain.User;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
import com.changhong.system.web.facade.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maren on 2015/6/15.
 */
public class TvChannelWebAssember {
    public static TvChannelInfoDTO toTvChannelInfoDTO(TvChannelInfo tvChannelInfo) {
        final String tvChannelName = tvChannelInfo.getTvChannelName();
        final String tvProgramName = tvChannelInfo.getTvProgramName();
        final String userMac = tvChannelInfo.getUserMac();
        final String year = tvChannelInfo.getYear();
        final String month = tvChannelInfo.getMonth();
        final String date=tvChannelInfo.getDay();
        final String hour=tvChannelInfo.getHour();

        TvChannelInfoDTO dto =  new TvChannelInfoDTO(date,hour,month,tvChannelName,tvProgramName,userMac,year);
        return dto;
    }

    public static List<TvChannelInfoDTO> toTvChannelInfoDTOList(List<TvChannelInfo> tvChannelInfos) {
        List<TvChannelInfoDTO> dtos = new ArrayList<TvChannelInfoDTO>();
        if (tvChannelInfos != null) {
            for (TvChannelInfo tvChannelInfo : tvChannelInfos) {
                dtos.add(toTvChannelInfoDTO(tvChannelInfo));
            }
        }
        return dtos;
    }

}
