package com.changhong.touying.tab;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.changhong.common.system.MyApplication;
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
	private SingleMusicAdapter singleMusicAdapter;
	/**
	 * 
	 * 所有的音乐信息
	 */
	private List<Music> musics;
	/**
	 * 单曲TAB的内容
	 */
	private ListView lv;
	/**
	 * 视频浏览部分
	 */
	private MusicPlayer player;

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
		musics = (List<Music>) provider.getList();

		// TODO 自动生成的方法存根
		View v = inflater.inflate(R.layout.activity_music_all_view, container,
				false);

		initView(v);

		initEvent();

		return v;
	}

	private void initView(View v) {

		initPlayer();

		/**
		 * 歌曲部分
		 */
		SetDefaultImage.getInstance().setContext(getActivity());
		lv = (ListView) v.findViewById(R.id.music_list_view_all);
		singleMusicAdapter = new SingleMusicAdapter(getActivity(), musics,
				player);
		lv.setAdapter(singleMusicAdapter);
	}

	/**
	 * 播放控制栏
	 */
	private void initPlayer() {
		player = new MusicPlayer();
		getActivity().getSupportFragmentManager().beginTransaction()
				.add(R.id.music_seek_layout_all, player, MusicPlayer.TAG)
				.show(player).commitAllowingStateLoss();
		player.setOnPlayListener(new OnPlayListener() {
			boolean isLastSong = false;

			@Override
			public void OnPlayFinished() {
				if (isLastSong) {
					player.stopTVPlayer();
					isLastSong = false;
				} else {
					player.nextMusic();
				}
			}

			@Override
			public void OnPlayBegin(String path, String name, String artist) {
				if (musics.get(musics.size() - 1).getPath().equals(path)) {
					isLastSong = true;
				}

			}
		});
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
				intent.putExtras(bundle);
				intent.setClass(getActivity(), MusicDetailsActivity.class);
				startActivity(intent);

			}
		});

	}

}