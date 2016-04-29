package com.changhong.touying.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.changhong.common.service.ClientSendCommandService;
import com.changhong.common.system.AppConfig;
import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.QuickQuireMessageUtil;
import com.changhong.common.utils.Utils;
import com.changhong.thirdpart.sharesdk.ShareFactory;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.test.ThirdpartTestActivity;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.MediaUtil;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

/**
 * Created by maren on 2015/4/9.
 */
public class MusicDetailsActivity extends FragmentActivity implements QuickQuireMessageUtil.OnFeedBackListener{

	private final static String TAG = "MusicDetailsActivity";
	
	/**
	 * 消息处理
	 */
	public static Handler handler;
	
	private final static String CMD_TAG = "music:";
	
	private final static String CMD_SEEK = CMD_TAG + "seekto";

	
	public int position;
	/**
	 * 被选中的音乐文件
	 */
	private Music selectedMusic;
	
	/**
	 * 
	 * 所有的音乐信息
	 */
	private ArrayList<Music> receiverMusics=null;
	private List<Music> musics=null;
	
	private SeekBar seekBarVolum;
	public AudioManager audioManager;
	
	 /**
     * 音量控制按钮
     */
    private ImageView volUpBtn;
    private ImageView volDownBtn;


	/**
	 * 图片
	 */
	private ImageView musicImage;

	/**
	 * 歌曲名
	 */
	private TextView musicName;

	/**
	 * 歌手
	 */
	private TextView musicAuthor;

	/**
	 * 歌曲内置图片
	 */
	private ImageView defaultImage;
	
	/**
	 * 返回按钮
	 */
	private ImageView returnImage;

	/**
	 * 播放按钮
	 */
//	private ImageView playImage;

	/**
	 * 播放器
	 */
	MusicPlayer musicPlayer;

	/**
	 * 是否为暂停状态
	 */
	private boolean isPausing = false;

	private MusicService musicService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		selectedMusic = (Music) intent.getSerializableExtra("selectedMusic");
		receiverMusics = (ArrayList<Music>)intent.getSerializableExtra("musics");
		
		musics = new ArrayList<Music>();
		
		for(Music music :receiverMusics){
			musics.add(music);
		}
		
	
		musicService = new MusicServiceImpl(MusicDetailsActivity.this);
		QuickQuireMessageUtil.getInstance().setFeedbackListener(this,this);
		Utils.requireServerVolume(this);

		initialViews();

		initialEvents();
		initShare();
	}

	private void initialViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music_details_pan);
		musicPlayer = new MusicPlayer();
		
		initPlayer();
		
		//初始化音量
		seekBarVolum = (SeekBar)findViewById(R.id.music_seek_volue);
		
		volUpBtn = (ImageView)findViewById(R.id.control_volume_bigger);     //放大音量键
        volDownBtn = (ImageView)findViewById(R.id.control_volume_small);    //减小音量键
        
        

		musicImage = (ImageView) findViewById(R.id.details_image);
		musicName = (TextView) findViewById(R.id.music_name);
		musicName.setText(selectedMusic.getTitle());

		String musicAuthorStr=selectedMusic.getArtist();
		musicAuthor = (TextView) findViewById(R.id.music_author);
		if (!TextUtils.isEmpty(musicAuthorStr)) {
			musicAuthor.setText(musicAuthorStr);
//			musicAuthor.setText("—— " + musicAuthorStr + " ——");
		}else {
			musicAuthor.setVisibility(View.GONE);
		}
		
		defaultImage=(ImageView)findViewById(R.id.iv_music_ablum);
		defaultImage.setImageBitmap(MediaUtil.getArtwork(this, selectedMusic.getId(), selectedMusic.getArtistId(), true, false));

	
		returnImage = (ImageView) findViewById(R.id.d_btn_return);
	//	playImage = (ImageView) findViewById(R.id.d_btn_play);
		
	//	musicPlayer = new MusicPlayer(); 
		
		//往activity中添加一个fragment
       // getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,musicPlayer,MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();
	}
	
	private void initPlayer(){
		//musicPlayer = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,musicPlayer,MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();
        musicPlayer.setOnPlayListener(new OnPlayListener() {
			boolean isLastSong = false;
			@Override
			public void OnPlayFinished() {
				if (isLastSong) {
					musicPlayer.stopTVPlayer();
					isLastSong = false;
				}
				else {
					Log.d(TAG, "OnPlayFinished被执行了!!!");
					musicPlayer.nextMusic();					
				}
			}
			
			@Override
			public void OnPlayBegin(String path,String name,String artist,long id,long artistId) {
				//public void OnPlayBegin(String path, String name, String artist) {
				
				//歌曲更换,歌曲的信息UI也要跟着改变
				musicName.setText(name);
				musicAuthor.setText(artist);
				defaultImage.setImageBitmap(MediaUtil.getArtwork(getApplicationContext(), id, artistId, true, false));
				
				//defaultImage.setIma
								
				if (musics.get(musics.size() -1).getPath().equals(path)) {
					isLastSong = true;
					Log.d(TAG, "OnPlayBegin被执行了!!!");
				}
				else {
					isLastSong = false;
				}
			}

		});
      //  musicPlayer.attachMusics(musics).autoPlaying(true);  //添加播放列表歌曲
    }

	@Override
	protected void onResume() {
		super.onResume();
		musicPlayer.attachMusic(selectedMusic).autoPlaying(true);
	//	musicPlayer.attachMusics(musics);  //添加播放列表歌曲
		musicPlayer.attachMusics(musics).autoPlaying(false);  //添加播放列表歌曲
	}
	private void initialEvents() {

		/*playImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				musicPlayer.playMusic(selectedMusic);				
			}
		});*/
		
		/**
         * 视频投影音量控制
         */
		if(volUpBtn != null)
		{
	        volUpBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                MyApplication.vibrator.vibrate(100);
	                ClientSendCommandService.sendMessage("key:volumeup");
	            }
	        });
		}
		if(volDownBtn != null)
		{
	        volDownBtn.setOnClickListener(new View.OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                MyApplication.vibrator.vibrate(100);
	                ClientSendCommandService.sendMessage("key:volumedown");
	            }
	        });
		}
		
		/*
		 * 
		 * 手动拖动进度条时执行
		 * */

		returnImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.vibrator.vibrate(100);
				finish();
			}
		});
	}
	 /**
     * **********************************************分享相关********************************************************
     */
    public void initShare() {
		View v = findViewById(R.id.bt_sharemusic);
		if (!AppConfig.USE_SHARE)
		{
			v.setVisibility(View.INVISIBLE);
			return;
		}
		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				doShare();
			}
		});
		
	}
    public void doShare() {
    	String title=getResources().getString(R.string.share_title);//标题
    	String artist=selectedMusic.getArtist();
    	String musicname=selectedMusic.getTitle();
    	String album=new MusicProvider(MusicDetailsActivity.this).getAlbumName(selectedMusic.getAlbumId());
    	
    	String text=getResources().getString(R.string.share_song_tag)+"\n"+ getResources().getString(R.string.singer)+"："+artist+"\n"+getResources().getString(R.string.album)+"："+album+"\n "+getResources().getString(R.string.song_name)+"："+musicname;
    	L.d("sharepic "+text+"  ");
		ShareFactory.getShareCenter(MusicDetailsActivity.this).showShareMenu(title, "  ",text, "");
	}

	@Override
	public void onFinish(QuickQuireMessageUtil vervify, Object result) {
		if (result == null
				|| !(result instanceof String)
				|| ((String)result).isEmpty()
				|| ((String)result).indexOf("/") == -1){
			return;
		}
		String value = (String)result;

		int volCurrent = Integer.parseInt(value.substring(0,value.indexOf("/")));
		int volMax  = Integer.parseInt(value.substring(value.indexOf("/") + 1,value.length()));
		seekBarVolum.setMax(volMax);
		seekBarVolum.setProgress(volCurrent);
	}
}