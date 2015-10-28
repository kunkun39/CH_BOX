package Com.DTV;

import Com.DTV.TVService_CTunerInfo;
import Com.DTV.TVService_ChannelInfo;
import Com.DTV.TVService_EpgEvent;
import Com.DTV.TVService_DT_Date;
import Com.DTV.TVService_DT_Time;
import Com.DTV.TVService_DT_DT;
import Com.DTV.TVService_CaInformation;
import Com.DTV.TVService_SystemSetInfo;
import Com.DTV.TVService_ProgramBook;

interface TVServiceAidl{

	void TVService_AIDL_AV_PlayDefaultStart();
	void TVService_AIDL_AV_PlayStart(int VideoPid, int AudioPid, int PcrPid, int ri_VideoType, int ri_AudioType );
	boolean TVService_AIDL_AV_PlayStop();
	void TVService_AIDL_TUNNING_SetCarrier(in TVService_CTunerInfo obj_TVService_CTunerInfo);
	boolean TVService_AIDL_DTV_CheckChannelExist();	
	void TVService_AIDL_SEARCH_ScanStart(byte rb_ScanMode,int i_Freqency,
										int i_SymbolRate,byte rb_QamMode,
										int i_StartFre,int i_EndFre);	
	int TVService_AIDL_CA_GetCarType();
	
	TVService_ChannelInfo[] TVService_DATA_GetChannelInfoArray_By_Type ( int ri_ChannelType );
	boolean	TVService_AIDL_AV_NewChannel(int ri_ChannelIndex);
	TVService_EpgEvent[] TVService_EPG_GetChannelEpgInfoByIndex(int ri_ChannelIndex,int ri_WeekIndex);
	TVService_DT_Date TVService_DT_GetDTDate();
	TVService_DT_Time TVService_DT_GetDTTime();
	TVService_DT_DT TVService_DT_GetDT();
	
	TVService_EpgEvent TVService_EPG_GetChannelPfInfo(int ri_ChannelIndex);
	
	boolean TVService_AUDIO_SetAudioChannel();
	
	TVService_CaInformation TVService_CA_GetCaInFormation();
	TVService_SystemSetInfo TVService_AIDL_SYSTEM_GetSystemSetInfo();
	void TVService_AIDL_SYSTEM_SetSystemSetInfo(in TVService_SystemSetInfo obj_TVService_SystemSetInfo);
	void TVService_AIDL_CHANNEL_SetChannelInfo(in TVService_ChannelInfo obj_TVService_ChannelInfo);
	boolean TVService_AIDL_AV_SetVideoStopMode(boolean b_mode);
	boolean TVService_AIDL_CA_SetOPId(int i_opid);
	boolean TVService_AIDL_AVM_HideVWindow();
	boolean TVService_AIDL_DATA_RemoveAll();
	int		TVService_AIDL_AV_GetCurChannelIndex();
	TVService_ChannelInfo  TVService_DATA_GetChannelInfoByIndex(int ri_Index);
	TVService_ChannelInfo[] TVService_DATA_GetCurPlayingChannelInfoArray();
	
	int TVService_DATA_GetCurPlayingChanelType();
	void TVService_AIDL_AV_NewChannelUp();
	void TVService_AIDL_AV_NewChannelDown();
	boolean TVService_AIDL_DATA_CheckDatabase();
	void TVService_AIDL_AV_PlayPreProgram();
	int  TVService_AIDL_DATA_GetProgramNum();
	int	 TVService_AIDL_AVM_SetAudioChannel();
	String getCurrentProgram();
	boolean setWindowRect(boolean sizeFlag);
	boolean TVservice_AIDL_AVM_SetVWindowRect(int rs_X, int rs_Y, int rs_Width, int rs_Height);
	boolean TVservice_AIDL_AVM_GetVWindowRect(int rs_X, int rs_Y, int rs_Width, int rs_Height);
	boolean TVservice_AIDL_AVM_ShowVWindow();
	boolean TVservice_AIDL_AVM_HideVWindow();
	
	boolean TVService_AIDL_PVR_StartRec(int ri_Index);
	boolean TVService_AIDL_PVR_StOPRec();
	void TVService_AIDL_AVM_ClearVideoBuffer();/* Vanlen add for Ningbo 20130910.*/
	
	int TVService_AIDL_CA_Set_SCRating(int r_Rating);
	int TVService_AIDL_CA_Input_PinCode(String r_PinCode);
	int TVService_AIDL_CA_Set_SCWorkTime(int r_StartHour, int r_StartMinute, int r_StartSecond,
	  								int r_EndHour, int r_EndMinute, int r_EndSecond);
	int TVService_AIDL_CA_Change_PinCode(String r_OldPin, String r_NewPin);
	
	TVService_ProgramBook[] AIDL_ProgramBook_GetList();
	boolean AIDL_ProgramBook_AddOne(in TVService_ProgramBook AddOneProgram);
	boolean AIDL_ProgramBook_DelOne(in TVService_ProgramBook DelOneProgram);
	TVService_ProgramBook[] AIDL_ProgramBook_SortList();
	int AIDL_ProgramBook_TotalNum();
	void AIDL_ProgramBook_DelInvalid();
}



