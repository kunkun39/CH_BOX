/**
 * 
 */
package com.changhong.touying.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.utils.UnicodeReader;
import com.changhong.common.widgets.IpSelectorDataServer;
import com.changhong.touying.R;
import com.changhong.touying.R.string;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.M3UPlayList;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.music.MusicProvider;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

/**
 * @author yves.yang
 *
 */
public class MusicPlayerActivity extends FragmentActivity{

	/**
	 * 播放器
	 */
	MusicPlayer musicPlayer;
	
	/**
	 * 正在播放的歌曲
	 */
	Music music;
	
	/**
	 * 正在播放的歌曲列表
	 */
	List<Music> playlist = new ArrayList<Music>();				
	
/*==================================================系统方法==========================*/
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music_player);		
		initView();		
		initData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		
//		if (ClientSendCommandService.serverIpList.isEmpty()) {
//			Toast.makeText(this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
//			finish();
//			return ;
//		}
//		
//		ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(0);        
//        ClientSendCommandService.handler.sendEmptyMessage(2);
        
		if (music != null) {
			playMusic();
		}
		else if (playlist != null
				&& playlist.size() > 0) {
			playMusics();
		}		
	}

	@Override
	protected void onPause() {
		// TODO 自动生成的方法存根
		super.onPause();	
	}

	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();
		finish();
		//moveTaskToBack(true);
	}
	
	@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	boolean result = false;
    	result = musicPlayer.OnKeyPress(keyCode, event);
		if (!result) {
			result = super.onKeyUp(keyCode, event);
		}
    	return result;
    }

/*===================================================初始化方法=======================*/
	private void initView()
	{
		musicPlayer = new MusicPlayer();        
        getSupportFragmentManager().beginTransaction().add(R.id.music_player,musicPlayer,MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();
        
        findViewById(R.id.music_player_container_above).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
				finish();
			}
		});
        
        findViewById(R.id.music_player_container_below).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	
	private void initData()
	{		        
		MusicProvider provider = new MusicProvider(this);
        List<Music> musics = (ArrayList<Music>) provider.getList();
        
		Intent intent = getIntent();
		try {					
			if (intent.getAction().equals(Intent.ACTION_VIEW)) {			
				String path = intent.getData().getPath();
				if (path.endsWith(".mp3")
						|| path.endsWith(".m4a")) {
					for (Music music : musics) {
		    			if (music.getPath().equals(path)) {
		    				this.music = music;
		    			}
		    		}
				}
				else if (path.endsWith(".m3u")) {
					playlist.clear();
					List<Music> list = M3UPlayList.loadPlayListToMusicList(this,path);
					if (list == null) {
						finish();
					}
					playlist.addAll(list);
				}
				else {
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}    

/*============================================================私有方法=================*/
	private void playMusics()
    {    	
		if (playlist == null
				|| playlist.size() == 0) {
			finish();
		}
    	getSupportFragmentManager().beginTransaction().show(musicPlayer).commitAllowingStateLoss();
    	musicPlayer.attachMusics(playlist).playMusics(null);       
    }
	
    private void playMusic()
    {    	
    	getSupportFragmentManager().beginTransaction().show(musicPlayer).commitAllowingStateLoss();    	
    	musicPlayer.attachMusic(music).playMusic(music);        
    }
	
}
