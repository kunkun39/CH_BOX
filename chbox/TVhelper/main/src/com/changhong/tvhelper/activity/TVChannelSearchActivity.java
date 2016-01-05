package com.changhong.tvhelper.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelecter;
import com.changhong.tvhelper.R;
import com.changhong.tvhelper.view.SearchPageDefault;
import com.changhong.tvhelper.view.SearchPageList;

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
