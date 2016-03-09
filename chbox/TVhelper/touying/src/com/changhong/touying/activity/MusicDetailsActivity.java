package com.changhong.touying.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.changhong.thirdpart.sharesdk.ShareFactory;
import com.changhong.thirdpart.sharesdk.util.L;
import com.changhong.touying.R;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.music.MediaUtil;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;

/**
 * Created by maren on 2015/4/9.
 */
public class MusicDetailsActivity extends AppCompatActivity {

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
//    private ImageView returnImage;

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

    //ToolBar
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        selectedMusic = (Music) intent.getSerializableExtra("selectedMusic");
        //musicService = new MusicServiceImpl(MusicDetailsActivity.this);


        //initialViews();

        initialEvents();
//        initShare();

        musicPlayer = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout, musicPlayer, MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.music_detail_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

	private void initialEvents() {

        musicImage = (ImageView) findViewById(R.id.details_image);
        musicName = (TextView) findViewById(R.id.music_name);
        musicName.setText(selectedMusic.getTitle());

        String musicAuthorStr = selectedMusic.getArtist();
        musicAuthor = (TextView) findViewById(R.id.music_author);
        if (!TextUtils.isEmpty(musicAuthorStr)) {
            musicAuthor.setText("—— " + musicAuthorStr + " ——");
        } else {
            musicAuthor.setVisibility(View.GONE);
        }

        defaultImage = (ImageView) findViewById(R.id.iv_music_ablum);
        defaultImage.setImageBitmap(MediaUtil.getArtwork(this, selectedMusic.getId(), selectedMusic.getArtistId(), true, false));

//        returnImage = (ImageView) findViewById(R.id.d_btn_return);
        playImage = (ImageView) findViewById(R.id.d_btn_play);

        musicPlayer = new MusicPlayer();
        getSupportFragmentManager().beginTransaction().add(R.id.music_seek_layout, musicPlayer, MusicPlayer.TAG).show(musicPlayer).commitAllowingStateLoss();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.music_detail_drawer);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        musicPlayer.attachMusic(selectedMusic).autoPlaying(true);
    }


    /**
     * **********************************************分享相关********************************************************
     */
//    public void initShare() {
//        findViewById(R.id.bt_sharemusic).setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
////                doShare();
//            }
//        });
//
//    }

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