package com.changhong.setting.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BidirSlidingLayout;
import com.changhong.setting.R;
import com.changhong.setting.service.UserUpdateService;
import com.changhong.setting.view.AppHelpDialog;
import com.changhong.setting.view.ScoreDialog;

/**
 * Created by Jack Wang
 */
public class SettingActivity extends AppCompatActivity {

	private static final String LOG_TAG = "SettingActivity";

	/**
	 * 返回到主菜单按钮
	 */
	private Button settingReturn;
//	private BidirSlidingLayout bidirSlidingLayout;
	private ImageButton setting_smb;
	/**
	 * 升级按钮, 下载的进度条, 系统升级的服务
	 */
	private TextView updateInfo;
	private LinearLayout updateBtn;
	private ProgressDialog m_pDialog;
	private UserUpdateService updateService;

	/**
	 * 系统评分、系统帮助
	 */
	private LinearLayout scoreBtn;
	private LinearLayout helpBtn;
	private ScoreDialog scoreDialog;
	private AppHelpDialog appHelpDialog;

	/**
	 * 消息处理
	 */
	private Handler handler;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		initialViews();

		initialEvents();

		initData();
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            finish();
        }

        return true;
    }

    private void initialViews() {
        // 初始化主界面
        setContentView(R.layout.setting_content);

//		bidirSlidingLayout = (BidirSlidingLayout) findViewById(R.id.bidir_sliding_layout);
		// 初始化按钮和事件
		settingReturn = (Button) findViewById(R.id.btn_back);
		updateInfo = (TextView) findViewById(R.id.update_info);
		updateBtn = (LinearLayout) findViewById(R.id.update_info_btn);
		scoreBtn = (LinearLayout) findViewById(R.id.btn_sys_score);
		helpBtn = (LinearLayout) findViewById(R.id.btn_sys_help);
//		setting_smb = (ImageButton) findViewById(R.id.setting_sidemunubutton);

		

		/**
		 * 进度条初始化
		 */
		m_pDialog = new ProgressDialog(SettingActivity.this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		m_pDialog.setMessage(getResources().getString(R.string.downloading));
		m_pDialog.setIndeterminate(false);
		m_pDialog.setCancelable(false);
		// m_pDialog.setProgress(0);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_content);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" 设置 ");
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
	}

	private void initialEvents() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int what = msg.arg1;
				switch (what) {
				case 100:
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.the_latest_version),
							Toast.LENGTH_SHORT).show();
					break;
				case 200:
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.phone_no_network),
							Toast.LENGTH_SHORT).show();
					break;
				case 300:
					// 缺省
					break;
				case 400:
					if (m_pDialog != null && m_pDialog.isShowing()) {
						m_pDialog.dismiss();
					}
					break;
				case 500:
					if (m_pDialog != null && m_pDialog.isShowing()) {
						m_pDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomaly),
							Toast.LENGTH_SHORT).show();
					break;
				case 600:
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomaly),
							Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
//		setting_smb.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.clickSideMenu();
//			}
//		});


		updateBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				startUpdate();
			}
		});

		scoreBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				scoreDialog = new ScoreDialog(SettingActivity.this);
				scoreDialog.setTitle(getResources().getString(R.string.system_score));
				scoreDialog.show();
			}
		});

		if (AppConfig.USE_TV
				|| AppConfig.USE_OTHER_AIRDISPLAY
				|| AppConfig.USE_VOICE_INPUT) {
			helpBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					MyApplication.vibrator.vibrate(100);
					appHelpDialog = new AppHelpDialog(SettingActivity.this);
					appHelpDialog.setTitle(getResources().getString(R.string.system_help));
					appHelpDialog.show();
				}
			});
		}else {
			helpBtn.setVisibility(View.GONE);
		}

//		bidirSlidingLayout.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.closeRightMenu();
//			}
//		});
	}

	private void initData() {
		updateInfo.setText(getResources().getString(R.string.cur_version)+ getCurrentSystemVersion());

		/**
		 * 注册广播
		 */
		updateService = new UserUpdateService(SettingActivity.this, handler,
				m_pDialog);
		IntentFilter homefilter = new IntentFilter();
		homefilter.addAction("SETTING_UPDATE_DOWNLOAD");
		homefilter.addAction("SETTING_UPDATE_INSTALL");
		registerReceiver(updateService.updateReceiver, homefilter);
	}

	private String getCurrentSystemVersion() {
		try {
			return this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
			return "1.0";
		}
	}

	/**
	 * *********************************************升级部分************************
	 * **********************************
	 */

	private void startUpdate() {
		updateService.initUpdateThread();
	}

	/**
	 * *************************************************系统方法重载******************
	 * ********************************
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (updateService.updateReceiver != null) {
			unregisterReceiver(updateService.updateReceiver);
			updateService.updateReceiver = null;
		}
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		switch (keyCode) {
//		case KeyEvent.KEYCODE_MENU:
//			bidirSlidingLayout.clickSideMenu();
//			return true;
//		default:
//			break;
//		}
//		return super.onKeyDown(keyCode, event);
//	}

}
