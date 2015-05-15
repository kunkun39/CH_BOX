package com.changhong.touying.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.touying.R;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;
import com.changhong.touying.tab.MusicCategoryPlaylistTab;
import com.changhong.touying.tab.MusicCategorySpecialTab;

/**
 * Created by Jack Wang
 */
public class MusicCategoryActivity extends FragmentActivity {

    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private Button back;
    private ListView clients = null;
    private ArrayAdapter<String> IpAdapter;        

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * 启动歌词扫描服务
         */
        MusicService musicService = new MusicServiceImpl(MusicCategoryActivity.this);
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
        
        Fragment fragment = new MusicCategoryPlaylistTab();
        getSupportFragmentManager().beginTransaction().add(R.id.realtabcontent,fragment, MusicCategoryPlaylistTab.TAG).hide(fragment).commitAllowingStateLoss();
        fragment = new MusicCategorySpecialTab();
        getSupportFragmentManager().beginTransaction().add(R.id.realtabcontent, fragment, MusicCategorySpecialTab.TAG).show(fragment).commitAllowingStateLoss();
        
        TextView specialBtn = (TextView)findViewById(R.id.music_category_specail);
        specialBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Fragment fragment =  getSupportFragmentManager().findFragmentByTag(MusicCategoryPlaylistTab.TAG);
				getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
				fragment =  getSupportFragmentManager().findFragmentByTag(MusicCategorySpecialTab.TAG);
				getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();

			}
		});
        
        TextView playlistBtn = (TextView)findViewById(R.id.music_category_playlist);
        playlistBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				Fragment fragment =  getSupportFragmentManager().findFragmentByTag(MusicCategorySpecialTab.TAG);
				getSupportFragmentManager().beginTransaction().hide(fragment).commitAllowingStateLoss();
				fragment =  getSupportFragmentManager().findFragmentByTag(MusicCategoryPlaylistTab.TAG);
				getSupportFragmentManager().beginTransaction().show(fragment).commitAllowingStateLoss();
				

			}
		});

    }

    private void initEvent() {
    	
        /**
         * IP连接部分
         */
        IpAdapter = new ArrayAdapter<String>(MusicCategoryActivity.this, android.R.layout.simple_list_item_1, ClientSendCommandService.serverIpList);
        clients.setAdapter(IpAdapter);
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
                title.setText("CHBOX");
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
                        Toast.makeText(MusicCategoryActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
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

    /**********************************************系统发发重载*********************************************************/

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
    protected void onSaveInstanceState(Bundle outState) {}
}
