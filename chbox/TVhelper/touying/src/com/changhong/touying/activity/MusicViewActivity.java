package com.changhong.touying.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.changhong.common.widgets.BoxSelecter;
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
    private Button back;
    private BoxSelecter ipBoxSelecter;

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
        

        back = (Button) findViewById(R.id.btn_back);
        musicListView = (ListView) findViewById(R.id.music_list_view);
        singleMusicAdapter = new SingleMusicAdapter(this,musics,player);
        musicListView.setAdapter(singleMusicAdapter);

        musicSinger = (TextView) findViewById(R.id.music_singer);
        musicSinger.setText(playlistName + getResources().getString(R.string.space_total) + musics.size()+ getResources().getString(R.string.song_unit));
        
    }
    
    private void initPlayer(){
    	player = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,player,MusicPlayer.TAG).hide(player).commitAllowingStateLoss();
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
        
    	ipBoxSelecter = new BoxSelecter(this, (TextView) findViewById(R.id.title), (ListView) findViewById(R.id.clients), (Button) findViewById(R.id.btn_list), new Handler(getMainLooper()));        
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
//        if (ClientSendCommandService.titletxt != null) {
//            title.setText(ClientSendCommandService.titletxt);
//        }    
        
		player.attachMusics(musics,playlistName).autoPlaying(true);
		
		
    }
    @Override
    protected void onDestroy() {
    
    	super.onDestroy();
    	if (ipBoxSelecter != null) {
			ipBoxSelecter.release();
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
    
}
