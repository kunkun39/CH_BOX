/**
 * 
 */
package com.changhong.touying.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.changhong.touying.R;
import com.changhong.touying.adapter.MusicPlayListFragmentPagerAdapter;
import com.changhong.touying.music.M3UPlayList;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.tab.MusicPlayListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

/**
 * @author yves.yang
 *
 */
public class MusicPlayListActivity extends AppCompatActivity {

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

	/*
	 * CardView添加
	 */

	private CardView mCardView;
	private ViewPager viewpager;
	private ArrayList<ListFragment> listViews; // Tab页面列表
	MusicPlayListFragment musicPlayListFragment;

	/********************************************************** 系统函数 ******************************/
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

	/************************************************************** 初始化函数 ************************/
	private void initListView() {

		initCardView();

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

		Button cancelBtn = (Button) findViewById(R.id.muslic_playlist_cancel);
		cancelBtn.setText("退出");
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
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
		
		musicPlayListFragment = new MusicPlayListFragment(mPlayList, this, mMusicPlayList, musics);
	}

	/************************************************************** viewerpage适配 ************************/

	private void InitViewPager() {
		viewpager = (ViewPager) findViewById(R.id.viewpager);
		listViews = new ArrayList<ListFragment>();
		listViews.add(musicPlayListFragment);
		FragmentManager fragmentManager = this.getSupportFragmentManager();
		viewpager.setAdapter(new MusicPlayListFragmentPagerAdapter(fragmentManager, listViews));
		viewpager.setCurrentItem(0);
		viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	/************************************************************** CardView及窗体适配 ************************/
	public void initCardView() {
		mCardView = (CardView) findViewById(R.id.card_view_list);
		Window window = getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		layoutParams.type = android.view.WindowManager.LayoutParams.TYPE_PHONE; // 设置window
		layoutParams.gravity = Gravity.TOP; // 调整悬浮窗口至右侧中间
		layoutParams.width = 700;// 设置悬浮窗口长宽数据
		layoutParams.height = 1100;
		layoutParams.y = 80;
		layoutParams.alpha = 1;
		window.setAttributes(layoutParams);
		InitViewPager();
		mCardView.setRadius(30);
		mCardView.setCardElevation(500);
	}

	/**
	 * 页卡切换监听,改变动画位置
	 */
	public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
}
