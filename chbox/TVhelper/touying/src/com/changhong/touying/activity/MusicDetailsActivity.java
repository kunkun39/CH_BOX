package com.changhong.touying.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.common.system.MyApplication;
import com.changhong.thirdpart.sharesdk.ShareFactory;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.thirdpart.test.ThirdpartTestActivity;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.music.MediaUtil;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

/**
 * Created by maren on 2015/4/9.
 */
public class MusicDetailsActivity extends FragmentActivity {

	/**
	 * 消息处理
	 */
	public static Handler handler;

	/**
	 * 被选中的音乐文件
	 */
	private Music selectedMusic;

	/**
	 * 图片
	 */
	private ImageView musicImage;

	/**
	 * 歌曲名
	 */
	private TextView musicName;

	/**
	 * 歌曲名
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
	private ImageView playImage;

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
		musicService = new MusicServiceImpl(MusicDetailsActivity.this);
		

		initialViews();

		initialEvents();
		initShare();
	}

	private void initialViews() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_music_details);

		musicImage = (ImageView) findViewById(R.id.details_image);
		musicName = (TextView) findViewById(R.id.music_name);
		musicName.setText(selectedMusic.getTitle());

		String musicAuthorStr=selectedMusic.getArtist();
		musicAuthor = (TextView) findViewById(R.id.music_author);
		if (!TextUtils.isEmpty(musicAuthorStr)) {
			musicAuthor.setText("—— " + musicAuthorStr + " ——");
		}else {
			musicAuthor.setVisibility(View.GONE);
		}
		
		defaultImage=(ImageView)findViewById(R.id.iv_music_ablum);
		defaultImage.setImageBitmap(MediaUtil.getArtwork(this, selectedMusic.getId(), selectedMusic.getArtistId(), true, false));

		returnImage = (ImageView) findViewById(R.id.d_btn_return);
		playImage = (ImageView) findViewById(R.id.d_btn_play);
		
		musicPlayer = new MusicPlayer();        
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout,musicPlayer,MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();
	}

	@Override
	protected void onResume() {
		super.onResume();
		musicPlayer.attachMusic(selectedMusic).autoPlaying(true);
	}
	private void initialEvents() {

		playImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				musicPlayer.playMusic(selectedMusic);				
			}
		});

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
		findViewById(R.id.bt_sharemusic).setOnClickListener(new OnClickListener() {
			
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
}