package com.changhong.touying.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.DateUtils;
import com.changhong.common.utils.MobilePerformanceUtils;
import com.changhong.common.utils.NetworkUtils;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicLrc;
import com.changhong.touying.music.MusicPlayList;
import com.changhong.touying.nanohttpd.NanoHTTPDService;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack Wang
 */
public class MusicViewActivity extends FragmentActivity {

    /**************************************************IP连接部分*******************************************************/

    public static TextView title = null;
    private Button listClients;
    private ListView clients = null;
    private Button back;
    private ArrayAdapter<String> IpAdapter;

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
    private MusicAdapter musicAdapter;
    
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

        title = (TextView) findViewById(R.id.title);
        back = (Button) findViewById(R.id.btn_back);
        clients = (ListView) findViewById(R.id.clients);
        listClients = (Button) findViewById(R.id.btn_list);

        musicListView = (ListView) findViewById(R.id.music_list_view);
        musicAdapter = new MusicAdapter(this);
        musicListView.setAdapter(musicAdapter);

        musicSinger = (TextView) findViewById(R.id.music_singer);
        musicSinger.setText(playlistName + "       共" + musics.size()+ "首");
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
				
			}
		});
    }

    private void initEvents() {
    	
        /**
         * IP part
         */
        IpAdapter = new ArrayAdapter<String>(MusicViewActivity.this, android.R.layout.simple_list_item_1, ClientSendCommandService.serverIpList);
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
     * *******************************************Music List data adapter**********************************************
     */

    public class MusicAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private boolean isReadyExit = false;

        public MusicAdapter(Context context) {
            this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount() {
            return musics.size();
        }

        public Object getItem(int item) {
            return item;
        }

        public long getItemId(int id) {
            return id;
        }

        //创建View方法
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView musicName = null;

            TextView fullPath = null;
            
            ImageView playBtn = null;

            if (convertView == null) {
                //获得view
                convertView = inflater.inflate(R.layout.music_list_item, null);
                musicName = (TextView) convertView.findViewById(R.id.music_item_name);
                fullPath = (TextView) convertView.findViewById(R.id.music_item_path);
                playBtn = (ImageView)convertView.findViewById(R.id.music_list_play);
                
                //组装view
                DataWapper wapper = new DataWapper(musicName, fullPath,playBtn);
                convertView.setTag(wapper);
            } else {
                DataWapper wapper = (DataWapper) convertView.getTag();
                musicName = wapper.musicName;
                fullPath = wapper.fullPath;
                playBtn = wapper.playBtn;
            }

            final Music music = musics.get(position);
            musicName.setText("  > " + music.getArtist() + " - " + music.getTitle() + " [" + DateUtils.getTimeShow(music.getDuration() / 1000) + "]");
            fullPath.setText(music.getPath());  
            
            playBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					playMusics(musics, music);
				}
			});

            return convertView;
        }

        private final class DataWapper {

            //音乐的名字
            public TextView musicName;

            //视屏的全路径
            public TextView fullPath;
            
            public ImageView playBtn;

            private DataWapper(TextView musicName, TextView fullPath,ImageView playBtn) {
                this.musicName = musicName;
                this.fullPath = fullPath;
                this.playBtn = playBtn;
            }
        }
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
    
	private void playMusics(List<Music> musics,Music music)
    {   	
		getSupportFragmentManager().beginTransaction().show(player).commitAllowingStateLoss();		
		
		if (music != null) {
    		player.playMusics(music);
		}else {
			player.autoPlaying(true);
		}    	    	    	
    }
}
