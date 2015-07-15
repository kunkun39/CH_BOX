package com.changhong.system.domain;

import com.changhong.common.domain.EntityBase;

/**
 * Created by maren on 15-6-29.
 */
public class TvChannel extends EntityBase{
    String channelName;
    String channelSequence;

    public TvChannel() {

    }


    public TvChannel(String channelName, String channelSequence) {
        this.channelName = channelName;
        this.channelSequence = channelSequence;
    }


    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelSequence() {
        return channelSequence;
    }

    public void setChannelSequence(String channelSequence) {
        this.channelSequence = channelSequence;
    }
}
