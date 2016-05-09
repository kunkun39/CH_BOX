package com.changhong.touying.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.changhong.common.utils.Debug;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.touying.R;
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
//	private Button listClients;
	private Button back;
//	private ListView clients = null;
//	private BoxSelecter ipSelecter;
	private TextView allMusicBtn, specialBtn, playlistBtn;
	private Fragment fragmentAll = null;
	private Fragment fragmentSpecial = null;
	private Fragment fragmentList = null;

	public static MusicCategoryActivity musicCategory = null;

	// public MusicCategoryActivity(){
	// if(null==musicCategory){
	// musicCategory=new MusicCategoryActivity();
	// }
	//
	// }

	public static MusicCategoryActivity getInstance() {
		if (null == musicCategory) {
			musicCategory = new MusicCategoryActivity();
		}
		return musicCategory;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initEvent();

		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(
				MusicCategoryActivity.this);
		musicService.findAllMusicLrc();
	}

	

	private void initView() {
		setContentView(R.layout.activity_music_category_pan);

		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		//clients = (ListView) findViewById(R.id.clients);
		//listClients = (Button) findViewById(R.id.btn_list);

		fragmentAll = new MusicCategoryAllTab();
		// fragmentSpecial = new MusicCategorySpecialTab();
		// fragmentList = new MusicCategoryPlaylistTab();

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// myTransaction =
				// getSupportFragmentManager().beginTransaction();
				// myTransaction.add(R.id.realtabcontent, fragmentAll,
				// MusicCategoryAllTab.TAG).show(fragmentAll);
				// myTransaction.commitAllowingStateLoss();
       
				//启动manager:用framentAll替换realtabcontent
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.realtabcontent, fragmentAll,
								MusicCategoryAllTab.TAG).show(fragmentAll)
						.commitAllowingStateLoss();
				// getSupportFragmentManager()
				// .beginTransaction()
				// .add(R.id.realtabcontent, fragmentSpecial,
				// MusicCategorySpecialTab.TAG)
				// .hide(fragmentSpecial).commitAllowingStateLoss();
				// getSupportFragmentManager()
				// .beginTransaction()
				// .add(R.id.realtabcontent, fragmentList,
				// MusicCategoryPlaylistTab.TAG)
				// .hide(fragmentList).commitAllowingStateLoss();
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
			// myTransaction.add(R.id.realtabcontent, fragmentAll,
			// MusicCategoryAllTab.TAG).hide(fragmentAll);
		}

		if (null == fragmentSpecial) {
			fragmentSpecial = new MusicCategorySpecialTab();
			// myTransaction.add(R.id.realtabcontent, fragmentSpecial,
			// MusicCategorySpecialTab.TAG).hide(fragmentSpecial);
		}
		if (null == fragmentList) {
			fragmentList = new MusicCategoryPlaylistTab();
			// myTransaction.add(R.id.realtabcontent, fragmentList,
			// MusicCategoryPlaylistTab.TAG).hide(fragmentList);
		}

		showFragment(i);

	}

	private void showFragment(int i) {
		findViewById(R.id.music_category_all_line).setVisibility(View.GONE);
		findViewById(R.id.music_category_specail_line).setVisibility(View.GONE);
		findViewById(R.id.music_category_playlist_line).setVisibility(View.GONE);
		//开启frament事务
		FragmentTransaction myTransaction = getSupportFragmentManager()
				.beginTransaction();
		if (1 == i) {

			fragmentAll = getSupportFragmentManager().findFragmentByTag(
					MusicCategoryAllTab.TAG);
			if (null == fragmentAll) {
				fragmentAll = new MusicCategoryAllTab();  //创新一个MusicCategoryAllTab对象
				myTransaction.add(R.id.realtabcontent, fragmentAll,
						MusicCategoryAllTab.TAG);                 //加载替换Fragment
			}
			myTransaction.show(fragmentAll);

			if (fragmentSpecial.isVisible()) {
				myTransaction.hide(fragmentSpecial);
			}

			if (fragmentList.isVisible()) {
				myTransaction.hide(fragmentList);
			}
			findViewById(R.id.music_category_all_line).setVisibility(View.VISIBLE);

		} else if (2 == i) {

			fragmentSpecial = getSupportFragmentManager().findFragmentByTag(
					MusicCategorySpecialTab.TAG);
			if (null == fragmentSpecial) {
				fragmentSpecial = new MusicCategorySpecialTab();
				myTransaction.add(R.id.realtabcontent, fragmentSpecial,
						MusicCategorySpecialTab.TAG);
			}
			myTransaction.show(fragmentSpecial);

			if (fragmentAll.isVisible()) {
				myTransaction.hide(fragmentAll);
			}
			if (fragmentList.isVisible()) {
				myTransaction.hide(fragmentList);
			}
			findViewById(R.id.music_category_specail_line).setVisibility(View.VISIBLE);
		} else if (3 == i) {

			fragmentList = getSupportFragmentManager().findFragmentByTag(
					MusicCategoryPlaylistTab.TAG);
			if (null == fragmentList) {
				fragmentList = new MusicCategoryPlaylistTab();
				myTransaction.add(R.id.realtabcontent, fragmentList,
						MusicCategoryPlaylistTab.TAG);
			}
			myTransaction.show(fragmentList);

			if (fragmentAll.isVisible()) {
				myTransaction.hide(fragmentAll);
			}
			if (fragmentSpecial.isVisible()) {
				myTransaction.hide(fragmentSpecial);
			}
			findViewById(R.id.music_category_playlist_line).setVisibility(View.VISIBLE);
		}
		myTransaction.commitAllowingStateLoss();
	}

	private void initEvent() {

		/**
		 * IP连接部分
		 */
//		ipSelecter = new BoxSelecter(this, (TextView)findViewById(R.id.title), (ListView)findViewById(R.id.clients), (Button)findViewById(R.id.btn_list), new Handler(getMainLooper()));
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});

	}

	private void changeTextColor(TextView tv) {
		allMusicBtn.setTextColor(getResources().getColor(R.color.Grey_500));
		;
		specialBtn.setTextColor(getResources().getColor(R.color.Grey_500));
		;
		playlistBtn.setTextColor(getResources().getColor(R.color.Grey_500));
		;
		tv.setTextColor(getResources().getColor(R.color.orange));

	}

	/********************************************** 系统发发重载 *********************************************************/

	@Override
	protected void onResume() {
		super.onResume();
//		if (ClientSendCommandService.titletxt != null) {
//			title.setText(ClientSendCommandService.titletxt);
//		}
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
	protected void onDestroy() {
		super.onDestroy();
		/*if (ipSelecter != null) {
			ipSelecter.release();
		}*/
		
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
	}
}
