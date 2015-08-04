package com.search.aidl;


import java.util.ArrayList;
import java.util.List;

import com.search.aidl.IKeyWords;
import com.search.aidl.IVoiceSearchVideo;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;


public class KeyWordsService extends Service{
	
	public static final String BOARDCAST_INTENT_ACTION = "mallservice.updateepg";
	 
	IKeyWords keywordsService;
	List<VideoInfo> videoInfos = new ArrayList<VideoInfo>();	
//	MallBroadcastReceiver broadcastReceiver = new MallBroadcastReceiver();
	

	
	@Override
	public IBinder onBind(Intent intent) {
		
		IKeyWords.Stub keywords = new IKeyWords.Stub() {

			@Override
			public void SetKeyWords(String searchText, KeyWords keyWords)
					throws RemoteException {
				
			}			
		};
		return keywords;
	}			


	
//	public void searchByWord(String searchText
//			, String person
//			, String area
//			, String category
//			, String modifier
//			, String name
//			, String year)
//	{
//		KeyWords keyWords = new KeyWords(person, area, category, modifier, name, year);
//		_MallActivity_SetKeyWords(searchText,keyWords);
//	}
	
	
//	public void playVideo(VideoInfo video)
//	{
//		Intent intent = new Intent();		
//		intent.setClassName("com.changhong.tvmall", "com.changhong.ivideo.activity.DetailActivity");
//		intent.putExtra("POSTER_TAG", video.getVideoId());
//		intent.putExtra("POSTER_CODE_TAG", video.getPrivatecode());
//		this.startActivity(intent);
//	}
	/**=====================================================================================================
	 *  类继承
	 */
//	class MallBroadcastReceiver extends BroadcastReceiver 
//	{
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			if (intent != null
//					&& intent.getAction().equalsIgnoreCase(BOARDCAST_INTENT_ACTION)) {				
//				playVideo(videoInfos.get(0));
//			}
//		}		
//	}
//	
//	class MallServiceConnection implements ServiceConnection
//	{
//
//		@Override
//		public void onServiceConnected(ComponentName name, IBinder service) {
//			keywordsService = IKeyWords.Stub.asInterface(service);
//			voiceSearchVideoService = IVoiceSearchVideo.Stub.asInterface(service);
//			_MallActivity_Register(name.getPackageName());
//		}
//
//		@Override
//		public void onServiceDisconnected(ComponentName name) {
//			keywordsService = null;
//			voiceSearchVideoService = null;
//			_MallActivity_Unregister(name.getPackageName());
//		}
//		
//	}
//	
//	/**=====================================================================================================
//	 *  私有函数
//	 */
//	public void _MallActivity_Register(String authID)
//	{
//		try {
//			voiceSearchVideoService.registerApplication(authID);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public void _MallActivity_Unregister(String authID)
//	{
//		try {
//			voiceSearchVideoService.unRegister(authID);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//	
//	void _MallActivity_SetKeyWords(String searchText,KeyWords keyWords)
//	{			
//		try {
//			keywordsService.SetKeyWords(searchText, keyWords);
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
//
//	void _MallActivity_getEpgList()
//	{					
//		try {
//			if (voiceSearchVideoService != null) {
//				voiceSearchVideoService.setEpgList(KeyWordsService.this.getPackageName(), BOARDCAST_INTENT_ACTION, videoInfos);
//			}			
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
//	}
	
}
