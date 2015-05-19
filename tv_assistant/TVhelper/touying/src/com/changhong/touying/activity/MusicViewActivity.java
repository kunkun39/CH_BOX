package com.changhong.touying.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.widgets.BoxSelectAdapter;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.SingleMusicAdapter;

/**
 * Created by Jack Wang
 */
public class MusicViewActivity extends FragmentActivity {

    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private ListView clients = null;
    private Button back;
    private BoxSelectAdapter ipAdapter;

    /************************************************music basic related info******************************************/

    /**
     * 从上个Activity传过来的musics
     */
    private List<Music> musics;

    private String playlistName;
    /**
     * 演唱者
     */
    private TextView musicSinger;    

    /**
     * 视频音乐部分
     */
    private ListView musicListView;

    /**
     * 数据适配器
     */
    private SingleMusicAdapter singleMusicAdapter;
    
    private MusicPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();

        initViews();

        initEvents();
    }

    private void initData() {
        musics = (List<Music>) getIntent().getSerializableExtra("musics");
        playlistName = getIntent().getStringExtra("name");
        if (playlistName == null) {
        	playlistName = musics.get(0).getArtist();
		}          
    }

    private void initViews() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_music_view_list);
        
        initPlayer();
        
        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        musicListView = (ListView) findViewById(R.id.music_list_view);
        singleMusicAdapter = new SingleMusicAdapter(this,musics,player);
        musicListView.setAdapter(singleMusicAdapter);

        musicSinger = (TextView) findViewById(R.id.music_singer);
        musicSinger.setText(playlistName + "       共" + musics.size()+ "首");
        
    }
    
    private void initPlayer(){
    	player = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,player,MusicPlayer.TAG).show(player).commitAllowingStateLoss();
        player.setOnPlayListener(new OnPlayListener() {
			boolean isLastSong = false;
			@Override
			public void OnPlayFinished() {
				if (isLastSong) {
					player.stopTVPlayer();
					isLastSong = false;
				}
				else {
						player.nextMusic();					
				}
			}
			
			@Override
			public void OnPlayBegin(String path, String name, String artist) {
				if (musics.get(musics.size() -1).getPath().equals(path)) {
					isLastSong = true;
				}
				else {
					isLastSong = false;
				}
				
			}
		});
    }

    private void initEvents() {
    	
        /**
         * IP part
         */
        ipAdapter = new BoxSelectAdapter(MusicViewActivity.this);
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
                title.setText(ClientSendCommandService.getCurrentConnectBoxName());
                ClientSendCommandService.handler.sendEmptyMessage(2);
                clients.setVisibility(View.GONE);
            }
        });
        listClients.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                if (ClientSendCommandService.serverIpList.isEmpty()) {
                    Toast.makeText(MusicViewActivity.this, "未获取到服务器IP", Toast.LENGTH_LONG).show();
                } else {
                    clients.setVisibility(View.VISIBLE);
                }
            }
        });
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.vibrator.vibrate(100);
                finish();
            }
        });

        /**
         * music part
         */
        musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyApplication.vibrator.vibrate(100);
                Intent intent = new Intent();
                Music music = musics.get(position);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selectedMusic", music);
                intent.putExtras(bundle);
                intent.setClass(MusicViewActivity.this, MusicDetailsActivity.class);
                startActivity(intent);

            }
        });
    }



    /**
     * *******************************************系统发发重载********************************************************
     */

    @Override
    protected void onResume() {
        super.onResume();
        if (ClientSendCommandService.titletxt != null) {
            title.setText(ClientSendCommandService.titletxt);
        }    
        
		player.attachMusics(musics,playlistName).autoPlaying(true);
		
		
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
    
}
