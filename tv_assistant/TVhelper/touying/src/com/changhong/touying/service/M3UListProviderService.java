/**
 * 
 */
package com.changhong.touying.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.changhong.common.system.AppConfig;
import com.changhong.touying.music.MusicUtils;

import android.R.bool;
import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;

/**
 * @author yves.yang
 *
 */
public class M3UListProviderService extends Service{

	private static final String NAME = "PLAY_LIST";
	
	private static final String PLAYLIST_KEY = "LIST_SET";
	
	public static final String UPDATE_INTENT = "com.changhong.touying.service.playlistupdate";
	
	private static String path = Environment.getExternalStoragePublicDirectory(".m3u").getPath();
	
	private static String dataPath;
	
	private static final int PATH_DEEPTH = 6;		
	
	static Set<String> playlistInStorage = new HashSet<String>(),
			playlistInData = new HashSet<String>(),
			playlistInM3uDir = new HashSet<String>();		
	
	/**
     * 音频列表文件后缀
     */
    private static final String SUFFIX = ".m3u";

	BroadcastReceiver bReceiver;	
	boolean isSearching = false;


	@Override
	public void onCreate() {

		super.onCreate();
		dataPath = getApplicationInfo().dataDir + "/" + SUFFIX;
		bReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
						|| action.equals(UPDATE_INTENT)) {
					AsyncTask.execute(new PlaylistLoadHandle());					
				}
			}
		};	
		AsyncTask.execute(new PlaylistLoadHandle());
		AsyncTask.execute(new PlaylistLoadALLHandle());	
		
		// 在IntentFilter中选择你要监听的行为  
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);// sd卡被插入，且已经挂载
        intentFilter.addAction(UPDATE_INTENT);
        intentFilter.setPriority(1000);// 设置最高优先级  
        registerReceiver(bReceiver, intentFilter);// 注册监听函数        
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}	

	@Override
	public void onDestroy() {
		unregisterReceiver(bReceiver);	
		super.onDestroy();
	}		
	
	public static  List<String> getList()
	{				
		List<String> result = new ArrayList<String>();
		result.addAll(playlistInData);
		result.addAll(playlistInStorage);
		result.addAll(playlistInM3uDir);
		
		return result;
	}
	
	private synchronized void saveList(Set<String> list,Set<String> value)
	{
		String preferenceName = PLAYLIST_KEY;
		
		if (list == playlistInData) {
			preferenceName += "DATA";			
		}else if (list == playlistInM3uDir) {			
			preferenceName += "M3U";
		}else {
			preferenceName += "ALL";
		}	
		
		list.clear();
		list.addAll(value);
		
		try
		{
			SharedPreferences preferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE);
			preferences.edit().putStringSet(preferenceName, list).commit();	
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	
	}
	
	private synchronized void loadPlayList(Set<String> list)
	{	
		String preferenceName = PLAYLIST_KEY;
		
		if (list == playlistInData) {
			preferenceName += "DATA";			
		}else if (list == playlistInM3uDir) {			
			preferenceName += "M3U";
		}else {
			preferenceName += "ALL";
		}
		
		SharedPreferences preferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE);
				
		Set<String> tempSet = preferences.getStringSet(preferenceName, null);
		if (tempSet != null) {	
			list.clear();
			list.addAll(tempSet);
		}		 
	}
	
	class PlaylistLoadHandle implements Runnable {
		public void run() {
			searchListPlayList(playlistInData,dataPath, SUFFIX);			
			if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			{
				searchListPlayList(playlistInM3uDir,path, SUFFIX);	
			}
		}
	}
	
	class PlaylistLoadALLHandle implements Runnable {
		public void run() {					
			if(Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			{	
				searchListPlayList(playlistInStorage,Environment.getExternalStorageState(), SUFFIX);
			}
		}
	}

	private void searchListPlayList(Set<String> list,String path,final String suffix)
    {    	    	    	
		int pathDeepth = 1;		
		Set<String> tempList = new HashSet<String>();
		listFile(new File(path),suffix,tempList,pathDeepth);      			 			
		saveList(list,tempList);    			
		loadPlayList(list);
    }
    
    private void listFile(File file,String suffix,Collection<String> list,int deepth)
    {
    	if (deepth > PATH_DEEPTH) {
			return ;
		}
    	try {					
	    	File[] fs = file.listFiles();
	    	String tempString;
	    	if (fs == null) {
				return ;
			}
	    	
	    	for (File f : fs) 
	    	{
				if (f.isDirectory()) {
					listFile(f,suffix,list,++deepth);
				}
				else {
					tempString = f.getPath().trim();
					if (tempString.toLowerCase().endsWith(suffix)) {
						list.add(tempString);					
					}				
				}
			}
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	

}
