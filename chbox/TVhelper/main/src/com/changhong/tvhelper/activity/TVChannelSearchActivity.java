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
import com.changhong.common.widgets.BoxSelecter;
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
	public static BoxSelecter ipSelecter = null;
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

		initViewAndEvent();
	}

	private void initData() {		
		fragmentDefault = new  SearchPageDefault();
		fragmentList = new SearchPageList();		
	}

	private void initViewAndEvent() {
		back = (Button) findViewById(R.id.btn_back);


		searchEditText = (EditText) findViewById(R.id.searchstring);
		searchButton = (Button) findViewById(R.id.btn_search);

		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content, fragmentList, "list").commitAllowingStateLoss();
		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content,fragmentDefault, "default").show(fragmentDefault).commitAllowingStateLoss();

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				
				//fix bug:
				searchString = searchEditText.getText().toString().trim();
				if (searchString.isEmpty()) {
					return ;
				}
				getSupportFragmentManager().beginTransaction().hide(fragmentDefault).show(fragmentList).commitAllowingStateLoss();
				fragmentDefault.saveSentences(TVChannelSearchActivity.this, searchString);
				fragmentList.setCondition(searchString);
				//TODO:fragmentList
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
		});
		
		searchEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				SearchPageList searchPageList = (SearchPageList) getSupportFragmentManager().findFragmentByTag("list");
				if(searchPageList != null)
				{
					getSupportFragmentManager().beginTransaction().hide(fragmentList).show(fragmentDefault).commitAllowingStateLoss();
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
		ipSelecter = new BoxSelecter(this, (TextView)findViewById(R.id.title), (ListView)findViewById(R.id.clients), (Button)findViewById(R.id.btn_list), new Handler(getMainLooper()));		
	}

	/**
	 * **********************************************系统方法重载*********************
	 * ********************************
	 */

	@Override
	protected void onResume() {
		super.onResume();
//		if (ClientSendCommandService.titletxt != null) {
//			title.setText(ClientSendCommandService.titletxt);
//		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ipSelecter != null) {
			ipSelecter.release();
		}
	}
}
