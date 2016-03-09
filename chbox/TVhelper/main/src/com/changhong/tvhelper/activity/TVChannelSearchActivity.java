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
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
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

public class TVChannelSearchActivity extends AppCompatActivity {

	private static final String TAG = "tvplayer";
	
	private DrawerLayout mDrawerLayout;

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
        setContentView(R.layout.search_page_list_new);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.search_drawer);
		Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
		toolbar.setTitle(" ");

		setSupportActionBar(toolbar);

		final ActionBar ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		initData();

		initViewAndEvent();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.search_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			finish();
		} else if (item.getItemId() == R.id.ipbutton) {
			mDrawerLayout.openDrawer(GravityCompat.START);
		}
		return true;
	}

	
	
	
	private void initData() {		
		fragmentDefault = new  SearchPageDefault();
		fragmentList = new SearchPageList();		
	}

	private void initViewAndEvent() {
		
		searchEditText = (EditText) findViewById(R.id.searchstring);
		searchButton = (Button) findViewById(R.id.btn_search);
		searchEditText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content, fragmentList, "list").commitAllowingStateLoss();
		getSupportFragmentManager().beginTransaction().add(R.id.search_page_content, fragmentDefault, "default").show(fragmentDefault).commitAllowingStateLoss();

		searchButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);

				//fix bug:
				searchString = searchEditText.getText().toString().trim();
				if (searchString.isEmpty()) {
					return;
				}
				getSupportFragmentManager().beginTransaction().hide(fragmentDefault).show(fragmentList).commitAllowingStateLoss();
				saveHistory(searchString);
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
				if (searchPageList != null) {
					getSupportFragmentManager().beginTransaction().hide(fragmentList).show(fragmentDefault).commitAllowingStateLoss();
				}
				loadHistory();
			}
		});

		searchEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}

			@Override
			public void afterTextChanged(Editable editable) {
				searchString = searchEditText.getText().toString().trim();
				if (searchString.isEmpty()) {
					return;
				}
				getSupportFragmentManager().beginTransaction().hide(fragmentDefault).show(fragmentList).commitAllowingStateLoss();
				fragmentList.setCondition(searchString);
			}
		});

		ipSelecter = new BoxSelecter(this, (TextView)findViewById(R.id.title), (ListView)findViewById(R.id.clients), new Handler(getMainLooper()));		
	}

	/**
	 *	=============================历史记录 ===================================
	 */
	public void saveHistory(String text)
	{
		if (fragmentDefault != null
				&& searchString != null
				&& !searchString.isEmpty())
			fragmentDefault.saveSentences(TVChannelSearchActivity.this, searchString);
	}

	public void loadHistory()
	{
		fragmentDefault.reInit();
	}
	/**
	 * **********************************************系统方法重载*********************
	 * ********************************
	 */

	@Override
	protected void onResume() {
		super.onResume();
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
