package com.changhong.touying.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.touying.R;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;
import com.changhong.touying.tab.MusicCategoryAllTab;
import com.changhong.touying.tab.MusicCategoryPlaylistTab;
import com.changhong.touying.tab.MusicCategorySpecialTab;

/**
 * Created by Jack Wang
 */
public class MusicCategoryActivity extends FragmentActivity {

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	private Button listClients;
	private Button back;
	private ListView clients = null;
	private BoxSelectAdapter ipAdapter;
	private TextView allMusicBtn, specialBtn, playlistBtn;
	private Fragment fragmentAll = null;
	private Fragment fragmentSpecial = null;
	private Fragment fragmentList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(
				MusicCategoryActivity.this);
		musicService.findAllMusicLrc();

		initView();

		initEvent();
	}

	private void initView() {
		setContentView(R.layout.activity_music_category);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		fragmentAll = new MusicCategoryAllTab();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				getSupportFragmentManager().beginTransaction().add(R.id.realtabcontent, fragmentAll, MusicCategoryAllTab.TAG).show(fragmentAll).commitAllowingStateLoss();
			}
		}).start();
		

		allMusicBtn = (TextView) findViewById(R.id.music_category_all);
		allMusicBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTextColor(allMusicBtn);
				MyApplication.vibrator.vibrate(100);
				selectorFragment(1);
			}
		});

		specialBtn = (TextView) findViewById(R.id.music_category_specail);
		specialBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTextColor(specialBtn);
				MyApplication.vibrator.vibrate(100);
				selectorFragment(2);
			}
		});

		playlistBtn = (TextView) findViewById(R.id.music_category_playlist);
		playlistBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changeTextColor(playlistBtn);
				MyApplication.vibrator.vibrate(100);
				selectorFragment(3);
			}
		});

	}

	private void selectorFragment(int i) {
		if (null == fragmentAll) {
			fragmentAll = new MusicCategoryAllTab();
			getSupportFragmentManager().beginTransaction()
			.add(R.id.realtabcontent, fragmentAll, MusicCategoryAllTab.TAG).commitAllowingStateLoss();
		}
		if (null == fragmentSpecial) {
			fragmentSpecial = new MusicCategorySpecialTab();
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.realtabcontent, fragmentSpecial,
					MusicCategorySpecialTab.TAG).commitAllowingStateLoss();
		}
		if (null == fragmentList) {
			fragmentList = new MusicCategoryPlaylistTab();
			getSupportFragmentManager()
			.beginTransaction()
			.add(R.id.realtabcontent, fragmentList,
					MusicCategoryPlaylistTab.TAG).commitAllowingStateLoss();
		}

		getSupportFragmentManager().beginTransaction().hide(fragmentAll).commitAllowingStateLoss();
		getSupportFragmentManager().beginTransaction().hide(fragmentSpecial).commitAllowingStateLoss();
		getSupportFragmentManager().beginTransaction().hide(fragmentList).commitAllowingStateLoss();
		
		

		if (1 == i) {
			getSupportFragmentManager().beginTransaction().show(fragmentAll).commitAllowingStateLoss();
		} else if (2 == i) {
			getSupportFragmentManager().beginTransaction().show(fragmentSpecial).commitAllowingStateLoss();
		} else if (3 == i) {
			getSupportFragmentManager().beginTransaction().show(fragmentList).commitAllowingStateLoss();
		}
	}

	private void initEvent() {

		/**
		 * IP连接部分
		 */
		ipAdapter = new BoxSelectAdapter(MusicCategoryActivity.this,
				ClientSendCommandService.serverIpList);
		clients.setAdapter(ipAdapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList
						.get(arg2);
				title.setText(ClientSendCommandService
						.getCurrentConnectBoxName());
				ClientSendCommandService.handler.sendEmptyMessage(2);
				clients.setVisibility(View.GONE);
			}
		});
		listClients.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					MyApplication.vibrator.vibrate(100);
					if (ClientSendCommandService.serverIpList.isEmpty()) {
						Toast.makeText(MusicCategoryActivity.this, "未获取到服务器IP",
								Toast.LENGTH_LONG).show();
					} else {
						clients.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});

	}

	private void changeTextColor(TextView tv) {
		allMusicBtn.setTextColor(getResources().getColor(R.color.white));
		;
		specialBtn.setTextColor(getResources().getColor(R.color.white));
		;
		playlistBtn.setTextColor(getResources().getColor(R.color.white));
		;
		tv.setTextColor(getResources().getColor(R.color.orange));

	}

	/********************************************** 系统发发重载 *********************************************************/

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			break;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}
}
