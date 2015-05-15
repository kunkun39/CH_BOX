/**
 * 
 */
package com.changhong.touying.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.changhong.touying.R;
import com.changhong.touying.music.M3UPlayList;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.MusicUtils;
import com.changhong.common.utils.UnicodeReader;

import android.R.anim;
import android.app.Activity;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author yves.yang
 *
 */
public class MusicPlayListActivity extends Activity{

	/**
	 * 接收到的对象名称
	 */
	public static final String SERIALIZABLE_OBJECT = "playlist";

	/**
	 * 列表元素名
	 */
	private static final String BTN_SELECTED = "SELECTED";
	private static final String TEXT_NAME = "NAME";
	private static final String TEXT_COMMENT = "COMMENT";
		
	/**
	 * 播放列表项
	 */
	private MusicPlayList mMusicPlayList;
	
	/**
	 * 用于显示的列表
	 */
	private List<Map<String, Object>> mPlayList;
	
	/**
	 * 播放列表的列表
	 */
	private ListView mFileListView;	
	
	/*
	 * 音乐列表
	 */
	private List<Music> musics;

/**********************************************************系统函数******************************/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_music_playlist);
		
		Intent intent = getIntent();
		if (intent != null) {
			mMusicPlayList = (MusicPlayList) intent.getSerializableExtra(SERIALIZABLE_OBJECT);			
		}
		
		if (mMusicPlayList == null) {
			finish();
		}
		
		initListData();
	}
	
	@Override
	protected void onStart() {
		super.onStart();		
		initListView();
	}
	@Override
	protected void onPause() {
		M3UPlayList.savePlayList(MusicPlayListActivity.this, mMusicPlayList.getPath(), mMusicPlayList.getPlayList());
		super.onPause();
	}
	
	/* （非 Javadoc）
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();		
	}
	/**************************************************************初始化函数************************/
	
	private void initListView()
	{
		mFileListView = (ListView)findViewById(R.id.music_playlist);
		
		mFileListView.setAdapter(new SimpleAdapter(this
				, mPlayList
				, R.layout.playlist_item
				, new String[]{BTN_SELECTED,TEXT_NAME,TEXT_COMMENT}
				, new int[]{R.id.playlist_radio_btn,R.id.playlist_item_name,R.id.playlist_item_comment}));
				
		mFileListView.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
		
		mFileListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO 自动生成的方法存根					
				Map<String, Object> checkBox = mPlayList.get(arg2);
				boolean checked = (Boolean)mPlayList.get(arg2).get(BTN_SELECTED);
				checkBox.put(BTN_SELECTED, !checked);
				onSelectedChanged(((TextView)arg1.findViewById(R.id.playlist_item_name)).getText().toString()
						,!checked);
				((SimpleAdapter)(arg0.getAdapter())).notifyDataSetChanged();
			}
		});
		
		Button confirmBtn = (Button) findViewById(R.id.muslic_playlist_comfirm);
		confirmBtn.setText(android.R.string.yes);
		confirmBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				boolean isResult = M3UPlayList.savePlayList(MusicPlayListActivity.this, mMusicPlayList.getPath(), mMusicPlayList.getPlayList());
				
				setResult(isResult ? RESULT_OK : RESULT_CANCELED);
								
				finish();
			}
		});
	}
	
	private void initListData()
	{
		MusicProvider provider = new MusicProvider(this);
        musics = (List<Music>) provider.getList();
        
        List<String> stringList = M3UPlayList.loadPlayListToStringList(this, mMusicPlayList.getPath());
        mMusicPlayList.getPlayList().clear();
        if(stringList != null)
        	mMusicPlayList.getPlayList().addAll(stringList);        		
        
        if(mPlayList == null)
        	mPlayList = new ArrayList<Map<String, Object>>();
        
        mPlayList.clear();
        
        //初始化列表
        for (Music music : musics) {
        	Map<String, Object> item = new HashMap<String, Object>();
       
        	item.put(TEXT_NAME, music.getTitle());
        	item.put(TEXT_COMMENT, music.getArtist());
        	item.put(BTN_SELECTED, new Boolean(false));
        	
        	for (String f : mMusicPlayList.getPlayList()) {
        		if (f.startsWith("#")) {
					continue;
				}
				if (music.getPath().equals(f)) {
					item.put(BTN_SELECTED, new Boolean(true));
					break;
				}				
			} 
        	        	
        	mPlayList.add(item);
		} 
		
	}

	/**************************************************************自定义方法************************/			
	
	private void onSelectedChanged(String name,boolean isSelected)
	{		
		Music music = null;
		
		String nameTrimed = name.trim();
		
		// 根据名字找歌
		try {
			for (Music m : musics) {
				if (m.getTitle().trim().equals(nameTrimed)) {
					music = m;					
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		if (music == null) 
			return ;
		
		//在列表里面天剑或者删除
		if (isSelected) {
			
			for(String song : mMusicPlayList.getPlayList())
			{
				if (music.getPath().equals(song)) {					
					return ;
				}
			}
			mMusicPlayList.getPlayList().add(music.getPath());						
		}
		else {
			mMusicPlayList.getPlayList().remove(music.getPath());
		}
	}
}
