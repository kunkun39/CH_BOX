package com.changhong.system.web.facade.assember;

import com.changhong.common.repository.EntityLoadHolder;
import com.changhong.system.domain.TvChannel;
import com.changhong.system.domain.TvChannelInfo;
import com.changhong.system.domain.User;
import com.changhong.system.web.facade.dto.TvChannelDTO;
import com.changhong.system.web.facade.dto.TvChannelInfoDTO;
import com.changhong.system.web.facade.dto.UserDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by maren on 2015/6/15.
 */
public class TvChannelWebAssember {


    public static TvChannel toTvChannelDomain(TvChannelDTO tvChannelDTO) {
        TvChannel tvChannel = null;
        if(tvChannelDTO == null)
            return null;

        if (tvChannelDTO.getId() > 0) {
            tvChannel = (TvChannel) EntityLoadHolder.getFeedBackDao().findById(tvChannelDTO.getId(), TvChannel.class);
            tvChannel.setId(tvChannelDTO.getId());
            tvChannel.setChannelName(tvChannelDTO.getTvChannelName());
            tvChannel.setChannelSequence(tvChannelDTO.getChannelSequence());

        } else {
            tvChannel = new TvChannel(tvChannelDTO.getTvChannelName(), tvChannelDTO.getChannelSequence());
        }
        return tvChannel;
    }
    public static TvChannelDTO toTvChannelDTO(TvChannel tvChannel) {
        final String channelName = tvChannel.getChannelName();
        final String channelSequence = tvChannel.getChannelSequence();
        final int id=tvChannel.getId();

        TvChannelDTO tvChannelDTO =  new TvChannelDTO(channelSequence,channelName,id);
        return tvChannelDTO;
    }

    public static List<TvChannelDTO> toTvChannelDTOList(List<TvChannel> tvChannels) {
        List<TvChannelDTO> dtos = new ArrayList<TvChannelDTO>();
        if (tvChannels != null) {
            for (TvChannel tvChannel : tvChannels) {
                dtos.add(toTvChannelDTO(tvChannel));
            }
        }
        return dtos;
    }

}
