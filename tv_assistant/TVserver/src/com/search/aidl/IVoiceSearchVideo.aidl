package com.search.aidl;
import com.search.aidl.VideoInfo;

interface IVoiceSearchVideo{
	void registerApplication(String authid);
	void setEpgList(in String source,in String action, in List<VideoInfo> videoList);
	void unRegister(String authid);
}