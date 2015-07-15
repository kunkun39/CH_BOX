package com.changhong.system.web.facade.dto;

/**
 * Created by maren on 15-6-29.
 */
public class TvChannelDTO {
    int id;
    String tvChannelName;
    String channelSequence;

    public TvChannelDTO() {
    }

    public TvChannelDTO(String channelSequence, String tvChannelName,int id) {
        this.id=id;
        this.channelSequence = channelSequence;
        this.tvChannelName = tvChannelName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getChannelSequence() {
        return channelSequence;
    }

    public void setChannelSequence(String channelSequence) {
        this.channelSequence = channelSequence;
    }

    public String getTvChannelName() {
        return tvChannelName;
    }

    public void setTvChannelName(String tvChannelName) {
        this.tvChannelName = tvChannelName;
    }
}
