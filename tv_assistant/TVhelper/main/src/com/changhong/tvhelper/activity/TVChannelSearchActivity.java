package com.changhong.tvhelper.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.db.sqlite.DatabaseContainer;
import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.StringUtils;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.touying.activity.MusicDetailsActivity;
import com.changhong.touying.activity.VedioDetailsActivity;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.vedio.Vedio;
import com.changhong.touying.vedio.VedioProvider;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.domain.Program;
import com.changhong.tvhelper.service.ChannelService;
import com.changhong.tvhelper.service.ClientGetCommandService;
import com.changhong.tvhelper.utils.YuYingWordsUtils;
import com.changhong.tvhelper.view.SearchPageDefault;
import com.changhong.tvhelper.view.SearchPageList;
import com.nostra13.universalimageloader.cache.disc.utils.DiskCacheFileManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class TVChannelSearchActivity extends FragmentActivity {

	private static final String TAG = "tvplayer";

	

	/**
	 * *****************************************Server IP Part ******************************************************
	 */
	public static BoxSelectAdapter ipAdapter = null;
	public static TextView title = null;
	private ListView clients = null;
	private Button list = null;
	private Button back = null;




	/**
	 * **********************************************Vedio Part******************************************************
	 */
	private InputMethodManager imm = null;
	private EditText searchEditText = null;
	private Button searchButton;
	private String searchString = null;
	
	SearchPageDefault fragmentDefault;
	SearchPageList fragmentList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_channel_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		initData();

//		initTVchannel();//hs修改为使用ClientGetCommandService.channelLogoMapping

		initViewAndEvent();
	}

	private void initData() {		
		fragmentDefault = new  SearchPageDefault();
		fragmentList = new SearchPageList();		
	}

	private void initViewAndEvent() {
		title = (TextView) findViewById(R.id.title);
		clients = (ListView) findViewById(R.id.clients);
		back = (Button) findViewById(R.id.btn_back);
		list = (Button) findViewById(R.id.btn_list);

		searchEditText = (EditText) findViewById(R.id.searchstring);
		searchButton = (Button) findViewById(R.id.btn_search);

//        channelSearchList = (ListView) findViewById(R.id.list_channels);
//        musicSearchList = (ListView) findViewById(R.id.list_musics);
//        vedioSearchList = (ListView) findViewById(R.id.list_vedios);

		// channel
//		channelText = (TextView) findViewById(R.id.text_channel);
		
		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content,fragmentDefault, "default").show(fragmentDefault).commitAllowingStateLoss();		
		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content, fragmentList, "list").commitAllowingStateLoss();

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				searchString = searchEditText.getText().toString();
				getSupportFragmentManager().beginTransaction().hide(fragmentDefault).show(fragmentList).commitAllowingStateLoss();
				fragmentDefault.saveSentences(TVChannelSearchActivity.this, searchString);
				fragmentList.setCondition(searchString);
				//TODO:fragmentList
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
		});
		
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO 自动生成的方法存根
				if (hasFocus) {
					SearchPageList searchPageList = (SearchPageList) getSupportFragmentManager().findFragmentByTag("list");
					if(searchPageList != null)
					{
						getSupportFragmentManager().beginTransaction().hide(fragmentList).show(fragmentDefault).commitAllowingStateLoss();
					}					
				}
				else {
					SearchPageList searchPageList = (SearchPageList) getSupportFragmentManager().findFragmentByTag("list");
					if (searchPageList != null) {
						getSupportFragmentManager().beginTransaction().hide(fragmentDefault).show(fragmentList).commitAllowingStateLoss();	
					}
					
				}
			}
		});

		/**
		 * Ip Part
		 */
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
		ipAdapter = new BoxSelectAdapter(TVChannelSearchActivity.this, ClientSendCommandService.serverIpList);
		clients.setAdapter(ipAdapter);
		clients.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				clients.setVisibility(View.GONE);
				return false;
			}
		});
		clients.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                String boxName = ClientSendCommandService.getCurrentConnectBoxName();
                ClientSendCommandService.titletxt = boxName;
                title.setText(boxName);
				ClientSendCommandService.handler.sendEmptyMessage(2);
				clients.setVisibility(View.GONE);
			}
		});
		list.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ClientSendCommandService.serverIpList.isEmpty()) {
					Toast.makeText(TVChannelSearchActivity.this,
							"没有发现长虹智能机顶盒，请确认盒子和手机连在同一个路由器?", Toast.LENGTH_LONG)
							.show();
				} else {
					clients.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * **********************************************系统方法重载*********************
	 * ********************************
	 */

	@Override
	protected void onResume() {
		super.onResume();
		if (ClientSendCommandService.titletxt != null) {
			title.setText(ClientSendCommandService.titletxt);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
