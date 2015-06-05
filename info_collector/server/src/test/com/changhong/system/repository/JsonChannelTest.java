package com.changhong.system.repository;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.changhong.system.domain.live.LiveChannel;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: maren
 * Date: 14-11-20
 * Time: 下午3:45
 * To change this template use File | Settings | File Templates.
 */
public  class JsonChannelTest {
    private static  String channel="{\"ResponseHeader\":{\n" +
            "    \"TransactionId\":\"TransactionId\",\n" +
            "    \"Status\":0,\n" +
            "    \"Params\":{\n" +
            "        \"ChannelID\":31378,\n" +
            "         \"ChannelTypeID\": null  }\n" +
            "},\"ChannelList\": {\n" +
            "        \"Channel_item\": [\n" +
            "            {\n" +
            "                \"id\": \"0b0b0f8e-5c66-4b43-a067-95dc38fc4c74\",\n" +
            "                \"DVBAudioCodec\": \"AC3\",\n" +
            "                \"DVBBitRate\": \"10000\",\n" +
            "                \"DVBLogicChannelID\": \"622\",\n" +
            "                \"DVBNetworkID\": \"16512\",\n" +
            "                \"DVBResolution\": \"19201080\",\n" +
            "                \"DVBServiceID\": \"622\",\n" +
            "                \"DVBTSID\": \"26\",\n" +
            "                \"DVBVideoCodec\": \"H264\",\n" +
            "                \"AudioCodecInfo\": \"AAC\",\n" +
            "                \"BitRateInfo\": \"800,1200\",\n" +
            "                \"ChannelID\": \"31378\",\n" +
            "                \"ChannelIcon\": \"/channel/icon/BTVWYHD.jpg\",\n" +
            "                \"ChannelImage\": \"/channel/image/BTVWYHD_888_666.jpg;/channel/image/BTVWYHD_684_513.jpg;/channel/image/BTVWYHD_432_324.jpg\",\n" +
            "                \"ChannelName\": \"BTV文艺高清\",\n" +
            "                \"ChannelType\": \"高清频道\",\n" +
            "                \"ChannelTypeID\": \"21\",\n" +
            "                \"FeeType\": \"0\",\n" +
            "                \"MuxInfo\": \"HLS\",\n" +
            "                \"OtherInfoArray\": \"\",\n" +
            "                \"PlayUrl\": \"{\\\"MAIN\\\":\\\"http://172.16.188.1:8088/live/BTVWYHD.m3u8\\\",\\\"800K\\\":\\\"http://172.16.188.1:8088/live/BTVWYHD_800.m3u8\\\",\\\"1200K\\\":\\\"http://172.16.188.1:8088/live/BTVWYHD_1200.m3u8\\\"}\",\n" +
            "                \"ResolutionInfo\": \"640480,720576\",\n" +
            "                \"VideoCodecInfo\": \"H264\",\n" +
            "                \"VideoType\": \"1\",\n" +
            "                \"DVBFrequency\": \"459\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }}";
    List<LiveChannel> channels=new ArrayList<LiveChannel>();

//    @Test
//    public void testjsonchannel(){
//       List<LiveChannel> channels=jsonToObject(channel);
//        System.out.println("channels size is ----->"+channels.size());
//
//    }

    @Test
    public  void jsonToObject(){

        JSONObject jsonObject=JSON.parseObject(channel);
        JSONObject objectResponse=jsonObject.getJSONObject("ResponseHeader");

        if (objectResponse.getIntValue("Status")==0){
            JSONObject channel=jsonObject.getJSONObject("ChannelList");
            JSONArray jsonArray=channel.getJSONArray("Channel_item");
            for(int i=0;i<jsonArray.size();i++){
                JSONObject object=jsonArray.getJSONObject(i);
            LiveChannel liveChannel=new LiveChannel();
            int id=object.getIntValue("ChannelID");
            liveChannel.setChannelID(id);

            String dvbBitRate=object.getString("DVBBitRate");
            String dvbLogicChannelId=object.getString("DVBLogicChannelID");
            String dvbNetworkId=object.getString("DVBNetworkID");
            String dvbResolutionc=object.getString("DVBResolution");
            String dvbServiceId=object.getString("DVBServiceID");
            String dvbTSID=object.getString("DVBTSID");
            String dvbVideoCodec=object.getString("DVBVideoCodec");
            String dvbAudioCodec=object.getString("AudioCodecInfo");
            String dvbBitRateInfo=object.getString("BitRateInfo");
            String channelIcon=object.getString("ChannelIcon");
            String channelImage=object.getString("ChannelImage");
            String channelName=object.getString("ChannelName");
            String channelType=object.getString("ChannelType");
            String channelTypeId=object.getString("ChannelTypeID");
            String feeType=object.getString("FeeType");
            String muxInfo=object.getString("MuxInfo");
            String otherInfoArray=object.getString("OtherInfoArray");
            String resolutionInfo=object.getString("ResolutionInfo");
            String videoCodeInfo=object.getString("VideoCodecInfo");
            String videoType=object.getString("VideoType");
            String dvbFrequency=object.getString("DVBFrequency");
            liveChannel.setDvbLogicChannelID(dvbLogicChannelId);
            liveChannel.setDvbNetworkID(dvbNetworkId);
            liveChannel.setResolutionInfo(dvbResolutionc);
            liveChannel.setDvbServiceID(dvbServiceId);
            liveChannel.setDvbTSID(dvbTSID);
            liveChannel.setDvbVideoCodec(dvbVideoCodec);
            liveChannel.setFeeType(feeType);
            liveChannel.setAudioCodeInfo(dvbAudioCodec);
            liveChannel.setChannelName(channelName);
            liveChannel.setBitRateInfo(dvbBitRateInfo);
            liveChannel.setChannelIcon(channelIcon);
            liveChannel.setChannelType(channelType);
            liveChannel.setChannelImage(channelImage);
            liveChannel.setChannelTypeID(channelTypeId);
            liveChannel.setMuxInfo(muxInfo);
            liveChannel.setOtherInfoArray(otherInfoArray);
            liveChannel.setResolutionInfo(resolutionInfo);
            liveChannel.setVideoCodeInfo(videoCodeInfo);
            liveChannel.setVideoType(videoType);
            liveChannel.setDvbBitRate(dvbBitRate);
            channels.add(liveChannel);

        }



        }


    }

}
