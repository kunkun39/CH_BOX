/**
 * 
 */
package com.changhong.touying.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.changhong.common.system.AppConfig;
import com.changhong.touying.music.MusicUtils;

import android.R.integer;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
	
	private static final int PATH_DEEPTH = 6;		
	
	static List<String> list = new ArrayList<String>();
	
	/**
     * 音频列表文件后缀
     */
    private static final String SUFFIX = ".m3u";

	BroadcastReceiver bReceiver;
	Set<String> playlist;


	@Override
	public void onCreate() {

		super.onCreate();
		bReceiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				
				if (action.equals(Intent.ACTION_MEDIA_MOUNTED)
						|| action.equals(UPDATE_INTENT)) {
					loadList();
					getPlayList(path, SUFFIX);
				}
			}
		};	
		loadList();
		getPlayList(path, SUFFIX);
		
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
		return list;
	}
	
	private void saveList(List<String> list)
	{
		Set<String> set = new HashSet<String>();
		
		for (String string : list) {
			set.add(string);
		}
		
		SharedPreferences preferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE);
		preferences.edit().putStringSet(PLAYLIST_KEY, set).commit();		
	}
	
	private  void loadList()
	{			
		SharedPreferences preferences = this.getSharedPreferences(NAME, Context.MODE_PRIVATE);//PreferenceManager.getDefaultSharedPreferences(this);
		
		Set<String> set = preferences.getStringSet(PLAYLIST_KEY, null);
		
		if (set== null
				|| set.size() == 0) {
			return ;
		}
		list.clear();
		for (String s : set) {
			list.add(s);
		}		
	}
	
	private synchronized void getPlayList(final String path,final String suffix)
    {    	    	    	
    	new Thread()
    	{
    		@Override
			public void run()
    		{
    			int pathDeepth = 1;
    			List<String> list = new ArrayList<String>();    			
    			
    			listFile(new File(path),suffix,list,pathDeepth);    			
    			saveList(list);    			
    			loadList();
    		}
    	}.start();
    }
    
    private void listFile(File file,String suffix,List<String> list,int deepth)
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
