package com.changhong.touying.tab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.changhong.common.system.MyApplication;
import com.changhong.common.utils.Debug;
import com.changhong.touying.R;
import com.changhong.touying.activity.MusicDetailsActivity;
import com.changhong.touying.dialog.MusicPlayer;
import com.changhong.touying.dialog.MusicPlayer.OnPlayListener;
import com.changhong.touying.music.Music;
import com.changhong.touying.music.MusicProvider;
import com.changhong.touying.music.SetDefaultImage;
import com.changhong.touying.music.SingleMusicAdapter;
import com.changhong.touying.service.MusicService;
import com.changhong.touying.service.MusicServiceImpl;

public class MusicCategoryAllTab extends Fragment {

	public static final String TAG = "MusicCategoryAllTab";
	/************************************************** 歌曲部分 *******************************************************/

	/**
	 * 数据适配器
	 */
	private SingleMusicAdapter singleMusicAdapter=null;
	/**
	 * 
	 * 所有的音乐信息
	 */
	private List<Music> musics=null;
	ArrayList<Music> musicsPlay =null;
	
	/**
	 * 单曲TAB的内容
	 */
	private ListView lv=null;
	/**
	 * 视频浏览部分
	 */
//	private MusicPlayer player=null;
	

	private View v=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		/**
		 * 启动歌词扫描服务
		 */
		MusicService musicService = new MusicServiceImpl(getActivity());
		musicService.findAllMusicLrc();

		MusicProvider provider = new MusicProvider(getActivity());
		musics = (List<Music>) provider.getList();   //从本地获取所有的music
		
		//musics序列化,便于在activity之间传递数据
		musicsPlay = new ArrayList<Music>();  		
		for (Music music : musics) {
			musicsPlay.add(music);
		}
		

		// 加载该activity对应的布局文件,也既是替换fragment的布局文件
		v = inflater.inflate(R.layout.activity_music_all_view, container,  
				false);
		initView(v);
		initEvent();
		return v;
	}

	
	

	private void initView(View v) {

		initPlayer();

		/**
		 * 歌曲部分:自定义list样式绑定数据到布局中
		 */
		SetDefaultImage.getInstance().setContext(getActivity());
		lv = (ListView) v.findViewById(R.id.music_list_view_all);
		singleMusicAdapter = new SingleMusicAdapter(getActivity(), musics,
				/*player*/null);
		lv.setAdapter(singleMusicAdapter);
	}

	/**
	 * 播放控制栏
	 */
	private void initPlayer() {
//		player = new MusicPlayer();
		
		Log.d(TAG, "initPlayer()被执行了");
		
		//开启fragment事务,使用播放器填充fragment
//		getActivity().getSupportFragmentManager().beginTransaction()
//				.add(R.id.music_seek_layout_all, player, MusicPlayer.TAG)
//				.hide(player).commitAllowingStateLoss();
		
		//给播放器设置监听事件
//		player.setOnPlayListener(new OnPlayListener() {
//			boolean isLastSong = false;
//
//
//			@Override
//			public void OnPlayFinished() {
//				if (isLastSong) {
//					player.stopTVPlayer();
//					isLastSong = false;
//				} else {
//					player.nextMusic();
//				}
//			}
//
//			@Override
////			public void OnPlayBegin(String path, String name, String artist) {
//			public void OnPlayBegin(String path,String name,String artist,long id,long artistId) {
//
//				if (musics.get(musics.size() - 1).getPath().equals(path)) {
//					isLastSong = true;
//				}
//
//			}
//
//
//		});
//		player.attachMusics(musics).autoPlaying(true);  //给播放列表播放器添加待播放的音乐
	}

	
	private void initEvent() {

		/**
		 * 歌曲部分
		 */
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				MyApplication.vibrator.vibrate(100);
				Intent intent = new Intent();
				Music music = musics.get(position);
				Bundle bundle = new Bundle();
				bundle.putSerializable("selectedMusic", music);
				bundle.putSerializable("musics", musicsPlay);
				intent.putExtras(bundle);
				intent.setClass(getActivity(), MusicDetailsActivity.class);
				startActivity(intent);
				
				//播放选中的音乐单曲
//				player.playMusic(music);

			}
		});

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		player.attachMusics(musics).autoPlaying(true);  //该activity启动后在oncreateView()后首先被执行
	}

}