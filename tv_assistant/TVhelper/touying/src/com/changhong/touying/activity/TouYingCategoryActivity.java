package com.changhong.touying.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.touying.R;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.service.M3UListProviderService;

/**
 * Created by Jack Wang
 */
public class TouYingCategoryActivity extends Activity {

	/************************************************** IP连接部分 *******************************************************/

	public static TextView title = null;
	private Button listClients;
	private Button back;
	private ListView clients = null;
	private BoxSelectAdapter ipAdapter;

	/************************************************** 菜单部分 *******************************************************/
	private ImageView imageTouYing;
	private ImageView vedioTouYing;
	private ImageView musicTouYing;
	private ImageView otherTouYing;
	private ImageButton touying_smb;
//	private BidirSlidingLayout bidirSlidingLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/**
		 * 启动Http服务
         */
        Intent http = new Intent(TouYingCategoryActivity.this, NanoHTTPDService.class);
        startService(http);
        initMedia();

		initView();

		initEvent();
	}

	
	

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	unbindService(conn);
		super.onDestroy();
	}

    ServiceConnection conn=new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			
		}
	};


	private void initMedia() {
        /**
         * 通知系统媒体去更新媒体库
         */
        String[] types = {"video/3gpp", "video/x-msvideo", "video/mp4", "video/mpeg", "video/quicktime",
                "audio/x-wav", "audio/x-pn-realaudio", "audio/x-ms-wma", "audio/x-ms-wmv", "audio/x-mpeg", "image/jpeg", "image/png"};
        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{Environment.getExternalStorageDirectory().getAbsolutePath()}, types, null);
        
        Intent intent=new Intent(getApplicationContext(), M3UListProviderService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    
    
	private void initView() {
		setContentView(R.layout.activity_touying_category);
//		bidirSlidingLayout = (BidirSlidingLayout) findViewById(R.id.bidir_sliding_layout);
		/**
		 * IP连接部分
		 */
		title = (TextView) findViewById(R.id.title);
		back = (Button) findViewById(R.id.btn_back);
		clients = (ListView) findViewById(R.id.clients);
		listClients = (Button) findViewById(R.id.btn_list);

		/**
		 * 菜单部分
		 */
		imageTouYing = (ImageView) findViewById(R.id.button_image_touying);
		vedioTouYing = (ImageView) findViewById(R.id.button_vedio_touying);
		musicTouYing = (ImageView) findViewById(R.id.button_music_touying);
		otherTouYing = (ImageView) findViewById(R.id.button_other_touying);

//		touying_smb = (ImageButton) findViewById(R.id.touying_sidemunubutton);
//		touying_smb.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.clickSideMenu();
//			}
//		});
	}

	private void initEvent() {
		/**
		 * IP连接部分
		 */
		ipAdapter = new BoxSelectAdapter(TouYingCategoryActivity.this, ClientSendCommandService.serverIpList);
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
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ClientSendCommandService.serverIP = ClientSendCommandService.serverIpList.get(arg2);
                String boxName = ClientSendCommandService.getCurrentConnectBoxName();
                ClientSendCommandService.titletxt = boxName;
                title.setText(boxName);
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
                        Toast.makeText(TouYingCategoryActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
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

		/**
		 * 菜单部分
		 */
		imageTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(TouYingCategoryActivity.this, PictureCategoryActivity.class);
                startActivity(intent);
            }
        });
        vedioTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(TouYingCategoryActivity.this, VedioCategoryActivity.class);
                startActivity(intent);
            }
        });
		musicTouYing.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent(TouYingCategoryActivity.this, MusicCategoryActivity.class);
                startActivity(intent);
            }
        });
        otherTouYing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TouYingCategoryActivity.this, "敬请期待中...", Toast.LENGTH_LONG).show();
                Intent intent=new Intent(TouYingCategoryActivity.this,PDFViewActivity.class);
                startActivity(intent);
            }
        });

//		bidirSlidingLayout.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				bidirSlidingLayout.closeRightMenu();
//			}
//		});
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
//		case KeyEvent.KEYCODE_MENU:
//			bidirSlidingLayout.clickSideMenu();
//			return true;
		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

}
